import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// --- Core Data Models ---

class Policy {
    String id, name, category, subCategory;
    double premium;
    String status; // Pending, Active, Cancelled

    public Policy(String id, String name, String category, String subCategory, double premium) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.premium = premium;
        this.status = "Pending";
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s > %s | Premium: $%.2f | Status: %s", 
                              id, name, category, subCategory, premium, status);
    }
}

class User {
    String username, password;
    String role; // Admin or Customer
    List<Policy> myPolicies = new ArrayList<>();

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

// --- Main Application ---

public class InsuranceManagementApp {
    private static Scanner sc = new Scanner(System.in);
    private static List<User> users = new ArrayList<>();
    private static List<Policy> availablePolicies = new ArrayList<>();
    private static List<String> categories = new ArrayList<>(Arrays.asList("Health", "Life", "Motor"));
    private static Map<String, List<String>> subCategories = new HashMap<>();

    static {
        // Initializing subcategories
        subCategories.put("Health", new ArrayList<>(Arrays.asList("Individual", "Family Floater")));
        subCategories.put("Life", new ArrayList<>(Arrays.asList("Term Insurance", "Endowment")));
        subCategories.put("Motor", new ArrayList<>(Arrays.asList("Two Wheeler", "Four Wheeler")));
        
        // Default Admin
        users.add(new User("admin", "admin123", "Admin"));
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("   SECUREGUARD INSURANCE MANAGEMENT SYSTEM  ");
            System.out.println("==========================================");
            System.out.println("1. Login (Admin/Customer)");
            System.out.println("2. Register (Customer)");
            System.out.println("3. Exit");
            System.out.print("Select choice: ");

            String choice = sc.nextLine();
            if (choice.equals("1")) login();
            else if (choice.equals("2")) register();
            else if (choice.equals("3")) break;
            else System.out.println("Invalid input. Please try again.");
        }
    }

    private static void login() {
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();

        for (User user : users) {
            if (user.username.equals(u) && user.password.equals(p)) {
                if (user.role.equals("Admin")) adminMenu(user);
                else customerMenu(user);
                return;
            }
        }
        System.out.println("Invalid credentials!");
    }

    private static void register() {
        System.out.print("Create Username: "); String u = sc.nextLine();
        System.out.print("Create Password: "); String p = sc.nextLine();
        users.add(new User(u, p, "Customer"));
        System.out.println("Registration successful! You can now login.");
    }

    // --- Admin Module ---

    private static void adminMenu(User admin) {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. View Users\n2. Manage Categories\n3. Manage Policies\n4. View All Policy Requests\n5. Logout");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                users.forEach(u -> System.out.println("- " + u.username + " [" + u.role + "]"));
            } else if (choice.equals("2")) {
                System.out.println("Current Categories: " + categories);
                System.out.print("Add new category? (name/no): ");
                String nc = sc.nextLine();
                if (!nc.equalsIgnoreCase("no")) categories.add(nc);
            } else if (choice.equals("3")) {
                addPolicy();
            } else if (choice.equals("4")) {
                manageRequests();
            } else if (choice.equals("5")) break;
        }
    }

    private static void addPolicy() {
        System.out.print("Policy ID: "); String id = sc.nextLine();
        System.out.print("Policy Name: "); String name = sc.nextLine();
        System.out.println("Available Categories: " + categories);
        System.out.print("Select Category: "); String cat = sc.nextLine();
        System.out.print("Premium Amount: "); double prem = Double.parseDouble(sc.nextLine());
        
        availablePolicies.add(new Policy(id, name, cat, "General", prem));
        System.out.println("Policy added successfully.");
    }

    private static void manageRequests() {
        System.out.println("Listing all active customer holdings:");
        for (User u : users) {
            if (u.role.equals("Customer")) {
                for (Policy p : u.myPolicies) {
                    System.out.println("User: " + u.username + " | " + p);
                }
            }
        }
    }

    // --- Customer Module ---

    private static void customerMenu(User customer) {
        while (true) {
            System.out.println("\n--- CUSTOMER DASHBOARD (" + customer.username + ") ---");
            System.out.println("1. View Categories\n2. Apply for Policy\n3. View My Policies\n4. Logout");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                System.out.println("Categories: " + categories);
            } else if (choice.equals("2")) {
                if (availablePolicies.isEmpty()) {
                    System.out.println("No policies available yet.");
                    continue;
                }
                for (int i = 0; i < availablePolicies.size(); i++) {
                    System.out.println(i + ". " + availablePolicies.get(i));
                }
                System.out.print("Enter index to apply: ");
                int idx = Integer.parseInt(sc.nextLine());
                Policy selected = availablePolicies.get(idx);
                // Create a copy for the user
                customer.myPolicies.add(new Policy(selected.id, selected.name, selected.category, selected.subCategory, selected.premium));
                System.out.println("Applied! Your policy is currently 'Pending' admin approval.");
            } else if (choice.equals("3")) {
                if (customer.myPolicies.isEmpty()) System.out.println("You hold no policies.");
                else customer.myPolicies.forEach(System.out::println);
            } else if (choice.equals("4")) break;
        }
    }
}