package com.cafe.digital_cafe.config;

import com.cafe.digital_cafe.entity.MenuCategory;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.entity.RoleType;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.MenuItemRepository;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, MenuItemRepository menuItemRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void loadUsers() {
        if (userRepository.count() > 0) {
            return; // already has data
        }

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
        User waiter = new User(
                "Alice Waiter",
                "waiter@digitalcafe.com",
                passwordEncoder.encode("password123"),
                "+1-555-0104",
                "321 Service Ave",
                RoleType.WAITER
        );

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

        menuItemRepository.save(new MenuItem("Espresso", "Rich Italian espresso shot", new BigDecimal("2.50"), MenuCategory.BEVERAGE));
        menuItemRepository.save(new MenuItem("Cappuccino", "Espresso with steamed milk foam", new BigDecimal("4.00"), MenuCategory.BEVERAGE));
        menuItemRepository.save(new MenuItem("Latte", "Espresso with steamed milk", new BigDecimal("4.50"), MenuCategory.BEVERAGE));
        menuItemRepository.save(new MenuItem("Americano", "Espresso with hot water", new BigDecimal("3.00"), MenuCategory.BEVERAGE));
        menuItemRepository.save(new MenuItem("Cold Brew", "Slow-steeped iced coffee", new BigDecimal("4.50"), MenuCategory.BEVERAGE));
        menuItemRepository.save(new MenuItem("Fresh Orange Juice", "Freshly squeezed orange juice", new BigDecimal("3.50"), MenuCategory.BEVERAGE));

        menuItemRepository.save(new MenuItem("Club Sandwich", "Chicken, bacon, lettuce, tomato on toast", new BigDecimal("8.50"), MenuCategory.FOOD));
        menuItemRepository.save(new MenuItem("Caesar Salad", "Romaine, parmesan, croutons, Caesar dressing", new BigDecimal("7.00"), MenuCategory.FOOD));
        menuItemRepository.save(new MenuItem("Margherita Pizza", "Tomato, mozzarella, basil", new BigDecimal("12.00"), MenuCategory.FOOD));
        menuItemRepository.save(new MenuItem("Bacon & Eggs", "Classic breakfast with toast", new BigDecimal("9.00"), MenuCategory.FOOD));

        menuItemRepository.save(new MenuItem("Tiramisu", "Classic Italian coffee-flavored dessert", new BigDecimal("6.00"), MenuCategory.DESSERT));
        menuItemRepository.save(new MenuItem("Chocolate Brownie", "Warm chocolate brownie with ice cream", new BigDecimal("5.50"), MenuCategory.DESSERT));
        menuItemRepository.save(new MenuItem("Cheesecake", "New York style cheesecake slice", new BigDecimal("5.00"), MenuCategory.DESSERT));

        menuItemRepository.save(new MenuItem("Croissant", "Buttery French croissant", new BigDecimal("3.50"), MenuCategory.SNACK));
        menuItemRepository.save(new MenuItem("Muffin", "Blueberry or chocolate chip muffin", new BigDecimal("3.00"), MenuCategory.SNACK));
        menuItemRepository.save(new MenuItem("Nachos", "Crispy tortilla chips with cheese and salsa", new BigDecimal("6.00"), MenuCategory.SNACK));

        System.out.println("  Sample menu items inserted (beverage, food, dessert, snack)");
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
        loadUsers();
        ensureHemaAdmin();
        loadMenuItems();
    }
}
