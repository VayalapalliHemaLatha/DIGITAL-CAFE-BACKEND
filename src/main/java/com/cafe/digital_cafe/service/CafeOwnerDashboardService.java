package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.*;
import com.cafe.digital_cafe.entity.*;
import com.cafe.digital_cafe.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
public class CafeOwnerDashboardService {

    private final UserRepository userRepository;
    private final CafeOrderRepository orderRepository;

    public CafeOwnerDashboardService(UserRepository userRepository, CafeOrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public DashboardSummaryResponse getSummary(LocalDate startDate, LocalDate endDate) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        DashboardSummaryResponse r = new DashboardSummaryResponse();
        r.setTotalCustomers(orderRepository.countDistinctUsersByCafeId(cafeId));
        r.setTotalCafes(1); // One cafe for this owner context
        
        if (startDate != null && endDate != null) {
            r.setTotalOrders(orderRepository.countByCafeIdAndOrderDateBetween(cafeId, startDate, endDate));
            r.setTotalSales(orderRepository.sumTotalAmountByCafeIdAndOrderDateBetween(cafeId, startDate, endDate));
            Map<String, Long> byStatus = new LinkedHashMap<>();
            for (OrderStatus s : OrderStatus.values()) {
                byStatus.put(s.getApiValue(), orderRepository.countByCafeIdAndStatusAndOrderDateBetween(cafeId, s, startDate, endDate));
            }
            r.setOrdersByStatus(byStatus);
        } else {
            r.setTotalOrders(orderRepository.countByCafeId(cafeId));
            r.setTotalSales(orderRepository.sumTotalAmountByCafeId(cafeId));
            Map<String, Long> byStatus = new LinkedHashMap<>();
            for (OrderStatus s : OrderStatus.values()) {
                byStatus.put(s.getApiValue(), orderRepository.countByCafeIdAndStatus(cafeId, s));
            }
            r.setOrdersByStatus(byStatus);
        }
        return r;
    }

    public DailyStatsResponse.Wrapper getDailyStats(LocalDate startDate, LocalDate endDate) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();
        if (startDate.isAfter(endDate)) {
            LocalDate t = startDate; startDate = endDate; endDate = t;
        }
        List<CafeOrder> orders = orderRepository.findByCafeIdAndOrderDateBetweenOrderByOrderDateAsc(cafeId, startDate, endDate);
        Map<LocalDate, DailyStatsResponse> map = new TreeMap<>();
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            map.put(d, new DailyStatsResponse(d, 0, BigDecimal.ZERO));
        }
        for (CafeOrder o : orders) {
            LocalDate d = o.getOrderDate();
            DailyStatsResponse stat = map.computeIfAbsent(d, k -> new DailyStatsResponse(k, 0, BigDecimal.ZERO));
            stat.setOrderCount(stat.getOrderCount() + 1);
            stat.setSales(stat.getSales().add(o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO));
        }
        DailyStatsResponse.Wrapper w = new DailyStatsResponse.Wrapper();
        w.setDailyStats(new ArrayList<>(map.values()));
        w.setPeriod(startDate + " to " + endDate);
        return w;
    }

    public MonthlyStatsResponse.Wrapper getMonthlyStats(Integer year, Integer month) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        int y = year != null ? year : LocalDate.now().getYear();
        LocalDate startDate;
        LocalDate endDate;
        if (month != null) {
            startDate = LocalDate.of(y, month, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        } else {
            startDate = LocalDate.of(y, 1, 1);
            endDate = LocalDate.of(y, 12, 31);
        }
        List<CafeOrder> orders = orderRepository.findByCafeIdAndOrderDateBetweenOrderByOrderDateAsc(cafeId, startDate, endDate);
        Map<String, MonthlyStatsResponse> map = new LinkedHashMap<>();
        if (month != null) {
            String key = y + "-" + month;
            MonthlyStatsResponse stat = new MonthlyStatsResponse(y, month, Month.of(month).name() + " " + y, 0, BigDecimal.ZERO);
            map.put(key, stat);
        } else {
            for (int m = 1; m <= 12; m++) {
                String key = y + "-" + m;
                map.put(key, new MonthlyStatsResponse(y, m, Month.of(m).name() + " " + y, 0, BigDecimal.ZERO));
            }
        }
        for (CafeOrder o : orders) {
            LocalDate d = o.getOrderDate();
            String key = d.getYear() + "-" + d.getMonthValue();
            MonthlyStatsResponse stat = map.get(key);
            if (stat != null) {
                stat.setOrderCount(stat.getOrderCount() + 1);
                stat.setSales(stat.getSales().add(o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO));
            }
        }
        MonthlyStatsResponse.Wrapper w = new MonthlyStatsResponse.Wrapper();
        w.setMonthlyStats(new ArrayList<>(map.values()));
        w.setPeriod(month != null ? Month.of(month).name() + " " + y : "Year " + y);
        return w;
    }

    private Long getCurrentCafeOwnerCafeId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRoleType() != RoleType.CAFE_OWNER || user.getCafeId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cafe owner access required");
        }
        return user.getCafeId();
    }
}
