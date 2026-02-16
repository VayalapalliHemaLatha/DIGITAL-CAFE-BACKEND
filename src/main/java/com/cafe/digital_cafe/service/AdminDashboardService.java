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
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;
    private final CafeOrderRepository orderRepository;

    public AdminDashboardService(UserRepository userRepository, CafeRepository cafeRepository,
                                 CafeOrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.cafeRepository = cafeRepository;
        this.orderRepository = orderRepository;
    }

    public DashboardSummaryResponse getSummary(LocalDate startDate, LocalDate endDate) {
        ensureAdmin();
        DashboardSummaryResponse r = new DashboardSummaryResponse();
        r.setTotalCustomers(userRepository.countByRoleType(RoleType.CUSTOMER));
        r.setTotalCafes(cafeRepository.count());
        if (startDate != null && endDate != null) {
            r.setTotalOrders(orderRepository.countByOrderDateBetween(startDate, endDate));
            r.setTotalSales(orderRepository.sumTotalAmountByOrderDateBetween(startDate, endDate));
            Map<String, Long> byStatus = new LinkedHashMap<>();
            for (OrderStatus s : OrderStatus.values()) {
                byStatus.put(s.getApiValue(), orderRepository.countByStatusAndOrderDateBetween(s, startDate, endDate));
            }
            r.setOrdersByStatus(byStatus);
        } else {
            r.setTotalOrders(orderRepository.count());
            r.setTotalSales(orderRepository.sumTotalAmount());
            Map<String, Long> byStatus = new LinkedHashMap<>();
            for (OrderStatus s : OrderStatus.values()) {
                byStatus.put(s.getApiValue(), orderRepository.countByStatus(s));
            }
            r.setOrdersByStatus(byStatus);
        }
        return r;
    }

    public List<CafeLocationResponse> getCafeLocations() {
        ensureAdmin();
        return cafeRepository.findAllByOrderByNameAsc().stream()
                .map(c -> new CafeLocationResponse(c.getId(), c.getName(), c.getAddress(), c.getPhone()))
                .collect(Collectors.toList());
    }

    public DailyStatsResponse.Wrapper getDailyStats(LocalDate startDate, LocalDate endDate) {
        ensureAdmin();
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();
        if (startDate.isAfter(endDate)) {
            LocalDate t = startDate; startDate = endDate; endDate = t;
        }
        List<CafeOrder> orders = orderRepository.findByOrderDateBetweenOrderByOrderDateAsc(startDate, endDate);
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
        ensureAdmin();
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
        List<CafeOrder> orders = orderRepository.findByOrderDateBetweenOrderByOrderDateAsc(startDate, endDate);
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

    private void ensureAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }
}
