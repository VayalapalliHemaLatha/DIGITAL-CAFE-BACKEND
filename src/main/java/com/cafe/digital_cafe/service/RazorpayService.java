package com.cafe.digital_cafe.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayService {

    @Autowired
    private RazorpayClient razorpayClient;

    public Map<String, Object> createOrder(int amount, String currency, String receipt) throws RazorpayException {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // Convert to paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);
            orderRequest.put("payment_capture", 1);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", razorpayOrder.get("id"));
            response.put("amount", razorpayOrder.get("amount"));
            response.put("currency", razorpayOrder.get("currency"));

            return response;
        } catch (RazorpayException e) {
            throw new RazorpayException("Failed to create Razorpay order: " + e.getMessage());
        }
    }
    
    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);
            
            return com.razorpay.Utils.verifyPaymentSignature(options, "YOUR_SECRET_KEY");
        } catch (Exception e) {
            return false;
        }
    }
}
