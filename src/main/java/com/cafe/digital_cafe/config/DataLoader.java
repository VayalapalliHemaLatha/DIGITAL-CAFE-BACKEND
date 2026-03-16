package com.cafe.digital_cafe.config;

import com.cafe.digital_cafe.entity.Cafe;
import com.cafe.digital_cafe.entity.CafeOrder;
import com.cafe.digital_cafe.entity.MenuCategory;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.entity.OrderItem;
import com.cafe.digital_cafe.entity.OrderStatus;
import com.cafe.digital_cafe.entity.RestaurantTable;
import com.cafe.digital_cafe.entity.RoleType;
import com.cafe.digital_cafe.entity.TableStatus;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.CafeOrderRepository;
import com.cafe.digital_cafe.repository.CafeRepository;
import com.cafe.digital_cafe.repository.MenuItemRepository;
import com.cafe.digital_cafe.repository.RestaurantTableRepository;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {

    private static final String DEFAULT_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantTableRepository tableRepository;
    private final CafeOrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, CafeRepository cafeRepository,
                      MenuItemRepository menuItemRepository, RestaurantTableRepository tableRepository,
                      CafeOrderRepository orderRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cafeRepository = cafeRepository;
        this.menuItemRepository = menuItemRepository;
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void loadCafes() {
        if (cafeRepository.count() > 0) return;

        cafeRepository.save(new Cafe("Digital Cafe Downtown", "123 Main St, New York", "+1-555-1000"));
        cafeRepository.save(new Cafe("Digital Cafe West", "456 Oak Ave, Los Angeles", "+1-555-1001"));

        System.out.println("  Sample cafes inserted");
    }

    private void loadUsers() {
        if (userRepository.count() > 0) {
            return; // already has data
        }

        Long cafeId = cafeRepository.findAll().stream().findFirst().map(Cafe::getId).orElse(null);

        String encoded = passwordEncoder.encode(DEFAULT_PASSWORD);

        User admin = new User(
                "Admin User",
                "admin@digitalcafe.com",
                encoded,
                "+1-555-0000",
                "System",
                RoleType.ADMIN
        );
        User cafeOwner = new User(
                "Jane Smith",
                "jane@digitalcafe.com",
                encoded,
                "+1-555-0102",
                "456 Oak Ave, Los Angeles, CA",
                RoleType.CAFE_OWNER
        );
        cafeOwner.setCafeId(cafeId);
        cafeOwner.setProfileImageUrl("https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&h=400&fit=crop");
        User customer = new User(
                "John Doe",
                "john@digitalcafe.com",
                encoded,
                "+1-555-0101",
                "123 Main St, New York, NY",
                RoleType.CUSTOMER
        );
        customer.setProfileImageUrl("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=400&fit=crop");
        User chef = new User(
                "Bob Chef",
                "chef@digitalcafe.com",
                encoded,
                "+1-555-0103",
                "789 Kitchen Rd",
                RoleType.CHEF
        );
        chef.setCafeId(cafeId);
        chef.setProfileImageUrl("https://images.unsplash.com/photo-1583394838336-acd977730f90?w=400&h=400&fit=crop");
        User waiter = new User(
                "Alice Waiter",
                "waiter@digitalcafe.com",
                encoded,
                "+1-555-0104",
                "321 Service Ave",
                RoleType.WAITER
        );
        waiter.setCafeId(cafeId);
        waiter.setProfileImageUrl("https://images.unsplash.com/photo-1531123897727-8f129e16fd3c?w=400&h=400&fit=crop");
        User customer2 = new User(
                "Mary Customer",
                "mary@digitalcafe.com",
                encoded,
                "+1-555-0105",
                "100 Customer Lane",
                RoleType.CUSTOMER
        );

        userRepository.save(admin);
        userRepository.save(cafeOwner);
        userRepository.save(customer);
        userRepository.save(chef);
        userRepository.save(waiter);
        userRepository.save(customer2);

        printSeededUsers();
    }

    private void printSeededUsers() {
        System.out.println("  --- Seeded users (all password: " + DEFAULT_PASSWORD + ") ---");
        System.out.println("    admin@digitalcafe.com   (ADMIN)");
        System.out.println("    jane@digitalcafe.com   (CAFE_OWNER)");
        System.out.println("    john@digitalcafe.com   (CUSTOMER)");
        System.out.println("    mary@digitalcafe.com   (CUSTOMER)");
        System.out.println("    chef@digitalcafe.com   (CHEF)");
        System.out.println("    waiter@digitalcafe.com (WAITER)");
    }

    private void loadMenuItems() {
        List<Cafe> cafes = cafeRepository.findAll().stream().sorted(Comparator.comparing(Cafe::getId)).toList();
        if (cafes.isEmpty()) return;

        Object[][] menuRows = new Object[][]{
                {"Espresso", "Rich Italian espresso shot", "2.50", MenuCategory.BEVERAGE, "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04?w=800&q=80"},
                {"Cappuccino", "Espresso with steamed milk foam", "4.00", MenuCategory.BEVERAGE, "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=800&q=80"},
                {"Latte", "Espresso with steamed milk", "4.50", MenuCategory.BEVERAGE, "https://images.unsplash.com/photo-1536935338218-84117b461234?w=800&q=80"},
                {"Americano", "Espresso with hot water", "3.00", MenuCategory.BEVERAGE, "https://images.unsplash.com/photo-1551033406-611cf0a28f67?w=800&q=80"},
                {"Cold Brew", "Slow-steeped iced coffee", "4.50", MenuCategory.BEVERAGE, "https://images.unsplash.com/photo-1517701604599-bb0d6e84984f?w=800&q=80"},
                {"Fresh Orange Juice", "Freshly squeezed orange juice", "3.50", MenuCategory.BEVERAGE, "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=800&q=80"},
                {"Club Sandwich", "Chicken, bacon, lettuce, tomato on toast", "8.50", MenuCategory.FOOD, "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=800&q=80"},
                {"Caesar Salad", "Romaine, parmesan, croutons, Caesar dressing", "7.00", MenuCategory.FOOD, "https://images.unsplash.com/photo-1550304943-4f24f5c181c1?w=800&q=80"},
                {"Margherita Pizza", "Tomato, mozzarella, basil", "12.00", MenuCategory.FOOD, "https://images.unsplash.com/photo-1574071318508-1cdbad80ad38?w=800&q=80"},
                {"Bacon & Eggs", "Classic breakfast with toast", "9.00", MenuCategory.FOOD, "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=800&q=80"},
                {"Tiramisu", "Classic Italian coffee-flavored dessert", "6.00", MenuCategory.DESSERT, "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=800&q=80"},
                {"Chocolate Brownie", "Warm chocolate brownie with ice cream", "5.50", MenuCategory.DESSERT, "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=800&q=80"},
                {"Cheesecake", "New York style cheesecake slice", "5.00", MenuCategory.DESSERT, "https://images.unsplash.com/photo-1524351199679-46cddfdbe647?w=800&q=80"},
                {"Croissant", "Buttery French croissant", "3.50", MenuCategory.SNACK, "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=800&q=80"},
                {"Muffin", "Blueberry or chocolate chip muffin", "3.00", MenuCategory.SNACK, "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=800&q=80"},
                {"Nachos", "Crispy tortilla chips with cheese and salsa", "6.00", MenuCategory.SNACK, "https://images.unsplash.com/photo-1513456852971-30c0b8199d4d?w=800&q=80"}
      };

        for (Cafe cafe : cafes) {
            Long cafeId = cafe.getId();
            if (!menuItemRepository.findByCafeIdOrderByCategoryAscNameAsc(cafeId).isEmpty()) continue;

            for (Object[] row : menuRows) {
                MenuItem m = new MenuItem((String) row[0], (String) row[1], new BigDecimal((String) row[2]), (MenuCategory) row[3]);
                m.setCafeId(cafeId);
                m.setImageUrl((String) row[4]);
                menuItemRepository.save(m);
            }
            System.out.println("  Sample menu items inserted for cafe: " + cafe.getName());
        }
    }

    private void loadTables() {
        List<Cafe> cafes = cafeRepository.findAll().stream().sorted(Comparator.comparing(Cafe::getId)).toList();
        if (cafes.isEmpty()) return;

        for (Cafe cafe : cafes) {
            Long cafeId = cafe.getId();
            if (!tableRepository.findByCafeIdOrderByTableNumberAsc(cafeId).isEmpty()) continue;

            tableRepository.save(new RestaurantTable(cafeId, "T1", 4, TableStatus.AVAILABLE));
            tableRepository.save(new RestaurantTable(cafeId, "T2", 4, TableStatus.AVAILABLE));
            tableRepository.save(new RestaurantTable(cafeId, "T3", 6, TableStatus.AVAILABLE));
            tableRepository.save(new RestaurantTable(cafeId, "T4", 2, TableStatus.BOOKED));
            tableRepository.save(new RestaurantTable(cafeId, "T5", 8, TableStatus.AVAILABLE));
            System.out.println("  Sample restaurant tables inserted for cafe: " + cafe.getName());
        }
    }

    private void loadSampleOrders() {
        if (orderRepository.count() > 0) return;

        User customer = userRepository.findByEmail("john@digitalcafe.com").orElse(null);
        if (customer == null) return;

        List<Cafe> cafes = cafeRepository.findAll().stream().sorted(Comparator.comparing(Cafe::getId)).toList();
        if (cafes.isEmpty()) return;

        Cafe cafe = cafes.get(0);
        Long cafeId = cafe.getId();
        List<RestaurantTable> tables = tableRepository.findByCafeIdOrderByTableNumberAsc(cafeId);
        if (tables.isEmpty()) return;

        List<MenuItem> items = menuItemRepository.findByCafeIdAndAvailableTrueOrderByCategoryAscNameAsc(cafeId);
        if (items.size() < 2) return;

        RestaurantTable table = tables.get(0);
        CafeOrder order = new CafeOrder();
        order.setUserId(customer.getId());
        order.setCafeId(cafeId);
        order.setTableId(table.getId());
        order.setOrderDate(LocalDate.now());
        order.setOrderTime(LocalTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(BigDecimal.ZERO);

        MenuItem m1 = items.get(0);
        MenuItem m2 = items.get(1);
        BigDecimal total = m1.getPrice().multiply(BigDecimal.valueOf(2)).add(m2.getPrice().multiply(BigDecimal.ONE));

        OrderItem oi1 = new OrderItem();
        oi1.setOrder(order);
        oi1.setMenuItemId(m1.getId());
        oi1.setItemName(m1.getName());
        oi1.setQuantity(2);
        oi1.setUnitPrice(m1.getPrice());
        order.getItems().add(oi1);

        OrderItem oi2 = new OrderItem();
        oi2.setOrder(order);
        oi2.setMenuItemId(m2.getId());
        oi2.setItemName(m2.getName());
        oi2.setQuantity(1);
        oi2.setUnitPrice(m2.getPrice());
        order.getItems().add(oi2);

        order.setTotalAmount(total);
        orderRepository.save(order);
        System.out.println("  Sample order inserted for customer john@digitalcafe.com (order id: " + order.getId() + ")");
    }

    /** Ensures admin account hema@gmail.com exists (created on every startup if missing). Same password as others. */
    private void ensureHemaAdmin() {
        if (userRepository.existsByEmail("hema@gmail.com")) return;
        User hema = new User(
                "Hema",
                "hema@gmail.com",
                passwordEncoder.encode(DEFAULT_PASSWORD),
                null,
                null,
                RoleType.ADMIN
        );
        hema.setProfileImageUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&h=400&fit=crop");
        userRepository.save(hema);
        System.out.println("  Admin created: hema@gmail.com / " + DEFAULT_PASSWORD);
    }

    @Override
    public void run(String... args) {
        loadCafes();
        loadUsers();
        ensureHemaAdmin();
        loadMenuItems();
        loadTables();
        loadSampleOrders();
        ensureMenuItemsHaveValidCafe();
        System.out.println("  --- Test logins (all password: " + DEFAULT_PASSWORD + ") ---");
        System.out.println("    admin@digitalcafe.com, jane@digitalcafe.com, john@digitalcafe.com, mary@digitalcafe.com, chef@digitalcafe.com, waiter@digitalcafe.com, hema@gmail.com");
    }

    /**
     * Repair orphaned menu items: any item whose cafe_id is not in the cafes table
     * is reassigned to a cafe that does not already have an item with the same name (to avoid unique constraint on cafe_id+name).
     */
    private void ensureMenuItemsHaveValidCafe() {
        List<Cafe> cafes = cafeRepository.findAll().stream().sorted(Comparator.comparing(Cafe::getId)).toList();
        if (cafes.isEmpty()) return;
        Set<Long> validCafeIds = cafes.stream().map(Cafe::getId).collect(Collectors.toSet());
        List<MenuItem> allItems = menuItemRepository.findAll();
        int repaired = 0;
        for (MenuItem mi : allItems) {
            if (mi.getCafeId() == null || !validCafeIds.contains(mi.getCafeId())) {
                Long targetCafeId = null;
                for (Cafe cafe : cafes) {
                    if (!menuItemRepository.existsByCafeIdAndName(cafe.getId(), mi.getName())) {
                        targetCafeId = cafe.getId();
                        break;
                    }
                }
                if (targetCafeId != null) {
                    mi.setCafeId(targetCafeId);
                    menuItemRepository.save(mi);
                    repaired++;
                }
            }
        }
        if (repaired > 0) {
            System.out.println("  Repaired " + repaired + " menu item(s) with invalid cafe reference");
        }
    }
}
