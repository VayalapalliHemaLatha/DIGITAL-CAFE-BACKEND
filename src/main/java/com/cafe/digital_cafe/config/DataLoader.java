package com.cafe.digital_cafe.config;

import com.cafe.digital_cafe.entity.Cafe;
import com.cafe.digital_cafe.entity.MenuCategory;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.entity.RestaurantTable;
import com.cafe.digital_cafe.entity.RoleType;
import com.cafe.digital_cafe.entity.TableStatus;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.CafeRepository;
import com.cafe.digital_cafe.repository.MenuItemRepository;
import com.cafe.digital_cafe.repository.RestaurantTableRepository;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantTableRepository tableRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, CafeRepository cafeRepository,
                      MenuItemRepository menuItemRepository, RestaurantTableRepository tableRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cafeRepository = cafeRepository;
        this.menuItemRepository = menuItemRepository;
        this.tableRepository = tableRepository;
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

        User admin = new User(
                "Admin User",
                "admin@digitalcafe.com",
                passwordEncoder.encode("password123"),
                "+1-555-0000",
                "System",
                RoleType.ADMIN
        );
        User cafeOwner = new User(
                "Jane Smith",
                "jane@digitalcafe.com",
                passwordEncoder.encode("password123"),
                "+1-555-0102",
                "456 Oak Ave, Los Angeles, CA",
                RoleType.CAFE_OWNER
        );
        cafeOwner.setCafeId(cafeId);
        User customer = new User(
                "John Doe",
                "john@digitalcafe.com",
                passwordEncoder.encode("password123"),
                "+1-555-0101",
                "123 Main St, New York, NY",
                RoleType.CUSTOMER
        );
        User chef = new User(
                "Bob Chef",
                "chef@digitalcafe.com",
                passwordEncoder.encode("password123"),
                "+1-555-0103",
                "789 Kitchen Rd",
                RoleType.CHEF
        );
        chef.setCafeId(cafeId);
        User waiter = new User(
                "Alice Waiter",
                "waiter@digitalcafe.com",
                passwordEncoder.encode("password123"),
                "+1-555-0104",
                "321 Service Ave",
                RoleType.WAITER
        );
        waiter.setCafeId(cafeId);

        userRepository.save(admin);
        userRepository.save(cafeOwner);
        userRepository.save(customer);
        userRepository.save(chef);
        userRepository.save(waiter);

        System.out.println("  Sample users inserted:");
        System.out.println("    Admin: admin@digitalcafe.com / password123");
        System.out.println("    Cafe Owner: jane@digitalcafe.com / password123");
        System.out.println("    Customer: john@digitalcafe.com / password123");
    }

    private void loadMenuItems() {
        if (menuItemRepository.count() > 0) return;

        Long cafeId = cafeRepository.findAll().stream().findFirst().map(Cafe::getId).orElse(null);
        if (cafeId == null) return;

        MenuItem m;
        m = new MenuItem("Espresso", "Rich Italian espresso shot", new BigDecimal("2.50"), MenuCategory.BEVERAGE);
        m.setCafeId(cafeId);
        menuItemRepository.save(m);
        for (Object[] row : new Object[][]{
                {"Cappuccino", "Espresso with steamed milk foam", "4.00", MenuCategory.BEVERAGE},
                {"Latte", "Espresso with steamed milk", "4.50", MenuCategory.BEVERAGE},
                {"Americano", "Espresso with hot water", "3.00", MenuCategory.BEVERAGE},
                {"Cold Brew", "Slow-steeped iced coffee", "4.50", MenuCategory.BEVERAGE},
                {"Fresh Orange Juice", "Freshly squeezed orange juice", "3.50", MenuCategory.BEVERAGE},
                {"Club Sandwich", "Chicken, bacon, lettuce, tomato on toast", "8.50", MenuCategory.FOOD},
                {"Caesar Salad", "Romaine, parmesan, croutons, Caesar dressing", "7.00", MenuCategory.FOOD},
                {"Margherita Pizza", "Tomato, mozzarella, basil", "12.00", MenuCategory.FOOD},
                {"Bacon & Eggs", "Classic breakfast with toast", "9.00", MenuCategory.FOOD},
                {"Tiramisu", "Classic Italian coffee-flavored dessert", "6.00", MenuCategory.DESSERT},
                {"Chocolate Brownie", "Warm chocolate brownie with ice cream", "5.50", MenuCategory.DESSERT},
                {"Cheesecake", "New York style cheesecake slice", "5.00", MenuCategory.DESSERT},
                {"Croissant", "Buttery French croissant", "3.50", MenuCategory.SNACK},
                {"Muffin", "Blueberry or chocolate chip muffin", "3.00", MenuCategory.SNACK},
                {"Nachos", "Crispy tortilla chips with cheese and salsa", "6.00", MenuCategory.SNACK}
        }) {
            m = new MenuItem((String) row[0], (String) row[1], new BigDecimal((String) row[2]), (MenuCategory) row[3]);
            m.setCafeId(cafeId);
            menuItemRepository.save(m);
        }

        System.out.println("  Sample menu items inserted (beverage, food, dessert, snack)");
    }

    private void loadTables() {
        if (tableRepository.count() > 0) return;

        Long cafeId = cafeRepository.findAll().stream().findFirst().map(Cafe::getId).orElse(null);
        if (cafeId == null) return;

        tableRepository.save(new RestaurantTable(cafeId, "T1", 4, TableStatus.AVAILABLE));
        tableRepository.save(new RestaurantTable(cafeId, "T2", 4, TableStatus.AVAILABLE));
        tableRepository.save(new RestaurantTable(cafeId, "T3", 6, TableStatus.AVAILABLE));
        tableRepository.save(new RestaurantTable(cafeId, "T4", 2, TableStatus.BOOKED));
        tableRepository.save(new RestaurantTable(cafeId, "T5", 8, TableStatus.AVAILABLE));

        System.out.println("  Sample restaurant tables inserted");
    }

    /** Ensures admin account hema@gmail.com exists (created on every startup if missing). */
    private void ensureHemaAdmin() {
        if (userRepository.existsByEmail("hema@gmail.com")) return;
        User hema = new User(
                "Hema",
                "hema@gmail.com",
                passwordEncoder.encode("hema@123"),
                null,
                null,
                RoleType.ADMIN
        );
        userRepository.save(hema);
        System.out.println("  Admin account created: hema@gmail.com / hema@123");
    }

    @Override
    public void run(String... args) {
        loadCafes();
        loadUsers();
        ensureHemaAdmin();
        loadMenuItems();
        loadTables();
    }
}
