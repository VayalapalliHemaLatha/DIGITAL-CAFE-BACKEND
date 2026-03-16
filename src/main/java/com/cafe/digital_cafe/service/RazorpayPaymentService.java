package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.RazorpayOrderResponse;
import com.cafe.digital_cafe.dto.VerifyPaymentRequest;
import com.cafe.digital_cafe.entity.CafeOrder;
import com.cafe.digital_cafe.entity.RoleType;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.CafeOrderRepository;
import com.cafe.digital_cafe.repository.UserRepository;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class RazorpayPaymentService {

    private static final Logger log = LoggerFactory.getLogger(RazorpayPaymentService.class);

    private final String keyId;
    private final String keySecret;
    private final String currency;
    private final String companyName;
    private final CafeOrderRepository orderRepository;
    private final UserRepository userRepository;

    public RazorpayPaymentService(
            @Value("${rzp.key_id}") String keyId,
            @Value("${rzp.key_secret}") String keySecret,
            @Value("${rzp.currency:INR}") String currency,
            @Value("${rzp.company_name:Digital Cafe}") String companyName,
            CafeOrderRepository orderRepository,
            UserRepository userRepository) {
        this.keyId = keyId != null ? keyId.trim() : "";
        this.keySecret = keySecret != null ? keySecret.trim() : "";
        this.currency = currency != null ? currency.trim() : "INR";
        this.companyName = companyName != null ? companyName.trim() : "Digital Cafe";
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        log.info("Razorpay loaded: key_id={}, key_secret length={} (expected 24 for correct secret)", this.keyId, this.keySecret.length());
    }

    public RazorpayOrderResponse createRazorpayOrder(Long orderId, Long userId) {
        CafeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        boolean isOwner = order.getUserId().equals(userId);
        boolean isStaff = user.getRoleType() == RoleType.ADMIN ||
                ((user.getRoleType() == RoleType.CAFE_OWNER || user.getRoleType() == RoleType.WAITER)
                        && order.getCafeId().equals(user.getCafeId()));

        if (!isOwner && !isStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }

        if (order.getRazorpayPaymentId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order already paid");
        }
        BigDecimal total = order.getTotalAmount();
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order amount must be greater than zero");
        }
        int amountPaise = total.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();
        if (amountPaise < 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum amount is INR 1.00");
        }
        String receipt = "order_" + orderId;
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);
            JSONObject notes = new JSONObject();
            notes.put("cafe_order_id", String.valueOf(orderId));
            orderRequest.put("notes", notes);

            String razorpayOrderId = createOrderViaHttp(orderRequest);
            if (razorpayOrderId == null || razorpayOrderId.isEmpty()) {
                throw new IllegalStateException("Razorpay order response missing id");
            }

            order.setRazorpayOrderId(razorpayOrderId);
            orderRepository.save(order);

            RazorpayOrderResponse response = new RazorpayOrderResponse();
            response.setKeyId(keyId);
            response.setRazorpayOrderId(razorpayOrderId);
            response.setAmountPaise(amountPaise);
            response.setCurrency(currency);
            response.setReceipt(receipt);
            response.setCompanyName(companyName);
            return response;
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.toString();
            if (msg != null && (msg.contains("Authentication failed") || msg.contains("BAD_REQUEST_ERROR"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Razorpay authentication failed. Check rzp.key_id and rzp.key_secret in application.properties (or RZP_KEY_ID, RZP_KEY_SECRET env). Use Test Mode keys from https://dashboard.razorpay.com/app/keys.");
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create payment order: " + msg);
        }
    }

    private String createOrderViaHttp(JSONObject orderRequest) throws Exception {
        String auth = keyId + ":" + keySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.razorpay.com/v1/orders"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + encodedAuth)
                .POST(HttpRequest.BodyPublishers.ofString(orderRequest.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new RuntimeException("Razorpay API error: " + response.statusCode() + " " + response.body());
        }
        JSONObject json = new JSONObject(response.body());
        return json.optString("id", null);
    }

    public void verifyAndCapturePayment(Long orderId, Long userId, VerifyPaymentRequest request) {
        CafeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        boolean isOwner = order.getUserId().equals(userId);
        boolean isStaff = user.getRoleType() == RoleType.ADMIN ||
                ((user.getRoleType() == RoleType.CAFE_OWNER || user.getRoleType() == RoleType.WAITER)
                        && order.getCafeId().equals(user.getCafeId()));

        if (!isOwner && !isStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }

        if (order.getRazorpayPaymentId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order already paid");
        }
        if (!request.getRazorpayOrderId().equals(order.getRazorpayOrderId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order id does not match Razorpay order");
        }
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());
            boolean valid = Utils.verifyPaymentSignature(options, keySecret);
            if (!valid) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment signature");
            }
            order.setRazorpayPaymentId(request.getRazorpayPaymentId());
            orderRepository.save(order);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Payment verification failed: " + e.getMessage());
        }
    }
}
