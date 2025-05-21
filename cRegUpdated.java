import java.util.*;
import java.util.regex.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

public class cRegUpdated {

    private static final String TODO_DIR = "receipts";
    private static final Scanner sc = new Scanner(System.in);

    public static void writer(String fileName, String receipt){
        try(BufferedWriter pen = new BufferedWriter(new FileWriter (fileName, true))){
            pen.write(receipt);
            pen.newLine();
            pen.flush();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ArrayList<String> users = new ArrayList<>();
        authMenu(users, sc);
        sc.close();
    }

    public static void authMenu(ArrayList<String> users, Scanner sc) {
        while (true) {
            System.out.println("\n<<Welcome. Please log in or register to continue.>>");
            System.out.print("1. Log In\n2. Register\n3. View Users\n4. Exit\nChoose an option (1-4): ");
            String input = sc.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println("Input cannot be empty. Please enter a number between 1 and 4.");
                continue;
            }
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                continue;
            }
            switch (choice) {
                case 1:
                    String loggedInUser = Access(users, sc);
                    if (loggedInUser != null) {
                        ArrayList<Product> productList = new ArrayList<>();
                        cashRegisterMenu(productList, sc, loggedInUser);
                    }
                    break;
                case 2:
                    Reg(users, sc);
                    break;
                case 3:
                    if (users.isEmpty()) {
                        System.out.println("No users registered.");
                    } else {
                        System.out.println("\nRegistered users:");
                        for (int i = 0; i < users.size(); i += 2) {
                            System.out.println("Username: " + users.get(i));
                        }
                    }
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                    break;
            }
        }
    }

    public static void cashRegisterMenu(ArrayList<Product> productList, Scanner sc, String loggedInUser) {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("<< Cash Register >>");
            System.out.println("\n1. Add Product\n2. Print Receipt\n3. Pay\n4. Return to Login Menu");
            System.out.println("==============================");
            System.out.print("Choose an option (1-4): ");
            String input = sc.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println("Input cannot be empty. Please enter a number between 1 and 4.");
                continue;
            }
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                continue;
            }

            switch (choice) {
                case 1:
                    addProducts(productList, sc);
                    break;
                case 2:
                    display(productList, sc);
                    break;
                case 3:
                    double totalAmount = 0;
                    for (Product product : productList) {
                        totalAmount += product.getTotalPrice();
                    }
                    payBill(totalAmount, productList, sc, loggedInUser);
                    break;
                case 4:
                    System.out.println("Returning to login menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                    break;
            }
        }
    }

    public static String Access(ArrayList<String> users, Scanner sc) {
        System.out.println("Enter your username:");
        String username = sc.nextLine();
        System.out.println("Enter your password:");
        String password = sc.nextLine();

        if (login(users, username, password)) {
            System.out.println("Login successful! Welcome, " + username + "!");
            return username;
        } else {
            System.out.println("Login failed. Please try again.");
            return null;
        }
    }

    public static void Reg(ArrayList<String> users, Scanner sc) {
        System.out.println("\n<<Register>>");
        System.out.print("Enter username (5-15 characters, letters/numbers/underscore): ");
        String username = sc.nextLine();
        System.out.print("Enter password (8-20 characters, at least one digit): ");
        String password = sc.nextLine();

        if (addUser(users, username, password)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed.");
        }
    }

    public static boolean addUser(ArrayList<String> users, String username, String password) {
        Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_]{5,15}$");
        if (!usernamePattern.matcher(username).matches()) {
            System.out.println("Invalid username. Must be 5-15 characters long and can only contain letters, numbers, and underscores.");
            return false;
        }

        Pattern passwordPattern = Pattern.compile("^(?=.*\\d).{8,20}$");
        if (!passwordPattern.matcher(password).matches()) {
            System.out.println("Invalid password. Must be 8-20 characters long and contain at least one digit.");
            return false;
        }

        for (int i = 0; i < users.size(); i += 2) {
            if (users.get(i).equals(username)) {
                System.out.println("Username already exists. Please choose a different username.");
                return false;
            }
        }

        users.add(username);
        users.add(password);
        return true;
    }

    public static boolean login(ArrayList<String> users, String username, String password) {
        for (int i = 0; i < users.size(); i += 2) {
            if (users.get(i).equals(username) && users.get(i + 1).equals(password)) {
                return true;
            }
        }
        return false;
    }

    private static class Product {
        private String name;
        private double price;
        private int quantity;

        public Product(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public double getTotalPrice() {
            return price * quantity;
        }

        @Override
        public String toString() {
            return String.format("%s - %dx -- P%.2f = P%.2f", name, quantity, price, getTotalPrice());
        }
    }

    public static void addProducts(ArrayList<Product> productList, Scanner sc) {
        while (true) {
            System.out.println();
            System.out.print("Enter product name: ");
            String prodName = sc.nextLine();

            double prodPrice;
            while (true) {
                System.out.print("Enter price: ");
                try {
                    prodPrice = Double.parseDouble(sc.nextLine());
                    if (prodPrice >= 0) break;
                    System.out.println("Price cannot be negative.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price. Please enter a valid number.");
                }
            }

            int quant;
            while (true) {
                System.out.print("Enter quantity: ");
                try {
                    quant = Integer.parseInt(sc.nextLine());
                    if (quant >= 0) break;
                    System.out.println("Quantity cannot be negative.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity. Please enter a valid number.");
                }
            }

            productList.add(new Product(prodName, prodPrice, quant));
            System.out.println("<< Added to cart! >>");
            System.out.println();

            while (true) {
                System.out.print("Add another? [Y/N]: ");
                String choice = sc.nextLine();
                if (choice.equalsIgnoreCase("y")) {
                    break;
                } else if (choice.equalsIgnoreCase("n")) {
                    return;
                } else {
                    System.out.println("Invalid input. Enter Y or N.");
                }
            }
        }
    }

    public static void display(ArrayList<Product> productList, Scanner sc) {
        if (productList.isEmpty()) {
            System.out.println("==============================");
            System.out.println("Cart is empty.");
            System.out.println("==============================");
        } else {
            System.out.println();
            System.out.println("=========== RECEIPT =========");
            double totalAmount = 0;
            for (Product product : productList) {
                System.out.println(product);
                totalAmount += product.getTotalPrice();
            }
            System.out.printf("Total: P%.2f%n", totalAmount);
            System.out.println("==============================");

            while (true) {
                System.out.print("Pay? [Y/N]: ");
                String choice = sc.nextLine();
                if (choice.equalsIgnoreCase("y")) {
                    payBill(totalAmount, productList, sc, "Unknown");
                    return;
                } else if (choice.equalsIgnoreCase("n")) {
                    return;
                } else {
                    System.out.println("Invalid input. Enter Y or N.");
                }
            }
        }
    }

    public static void payBill(double totalAmount, ArrayList<Product> productList, Scanner sc, String loggedInUser) {
        if (totalAmount <= 0) {
            System.out.println("No payment due. Cart is empty or invalid.");
            return;
        }

        System.out.printf("Total amount due: P%.2f%n", totalAmount);
        double payment;
        while (true) {
            System.out.print("Enter amount to pay: ");
            try {
                payment = Double.parseDouble(sc.nextLine());
                if (payment >= 0) break;
                System.out.println("Payment cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid payment. Please enter a valid number.");
            }
        }

        if (payment >= totalAmount) {
            while (true) {
                System.out.print("Confirm payment and print receipt? [Y/N]: ");
                String confirm = sc.nextLine();
                if (confirm.equalsIgnoreCase("y")) {
                    double change = payment - totalAmount;

                    StringBuilder receipt = new StringBuilder();
                    receipt.append("=========== RECEIPT =========\n");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy @ HH:mm:ss");
                    receipt.append("Date: ").append(LocalDateTime.now().format(formatter)).append("\n");
                    receipt.append("Cashier: ").append(loggedInUser).append("\n\n");

                    for (Product product : productList) {
                        receipt.append(product).append("\n");
                    }

                    receipt.append(String.format("\nTotal: P%.2f", totalAmount));
                    receipt.append(String.format("\nPaid: P%.2f", payment));
                    receipt.append(String.format("\nChange: P%.2f", change));
                    receipt.append("\n=============================\n\n");

                    File dir = new File(TODO_DIR);
                    if (!dir.exists()) dir.mkdirs();

                    System.out.print("Enter receipt file name: ");
                    String fileName = sc.nextLine();
                    if (!fileName.toLowerCase().endsWith(".txt")) {
                        fileName += ".txt";
                    }
                    String filePath = TODO_DIR + File.separator + fileName;

                    writer(filePath, receipt.toString());

                    System.out.println("Receipt saved to: " + filePath);
                    productList.clear();
                    return;
                } else if (confirm.equalsIgnoreCase("n")) {
                    System.out.println("Payment canceled.");
                    return;
                } else {
                    System.out.println("Invalid input. Enter Y or N.");
                }
            }
        } else {
            System.out.println("Insufficient funds. Payment failed.");
            while (true) {
                System.out.print("Try again? [Y/N]: ");
                String retry = sc.nextLine();
                if (retry.equalsIgnoreCase("y")) {
                    payBill(totalAmount, productList, sc, loggedInUser);
                    return;
                } else if (retry.equalsIgnoreCase("n")) {
                    return;
                } else {
                    System.out.println("Invalid input. Enter Y or N.");
                }
            }
        }
    }
}
