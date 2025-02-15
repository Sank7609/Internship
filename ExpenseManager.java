package Task1;

import java.sql.*;
import java.util.Scanner;

public class ExpenseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ExpenseDB";
    private static final String DB_USER = "root";  
    private static final String DB_PASSWORD = "sank1234"; 

    private static Connection conn;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createTable();
            runApplication();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS expenses (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "category VARCHAR(100), " +
                     "amount DECIMAL(10,2), " +
                     "date DATE, " +
                     "description TEXT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void runApplication() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nExpense Manager");
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. Edit Expense");
            System.out.println("4. Delete Expense");
            System.out.println("5. Generate Expense Report");
            System.out.println("6. Perform Arithmetic Operation");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); 
            switch (choice) {
                case 1 -> addExpense(scanner);
                case 2 -> viewExpenses();
                case 3 -> editExpense(scanner);
                case 4 -> deleteExpense(scanner);
                case 5 -> generateExpenseReport();
                case 6 -> performArithmetic(scanner);
                case 7 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addExpense(Scanner scanner) {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        String sql = "INSERT INTO expenses (category, amount, date, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, date);
            pstmt.setString(4, description);
            pstmt.executeUpdate();
            System.out.println("Expense added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewExpenses() {
        String sql = "SELECT * FROM expenses";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nID | Category | Amount | Date | Description");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("category") + " | " +
                        rs.getDouble("amount") + " | " + rs.getDate("date") + " | " + rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void editExpense(Scanner scanner) {
        System.out.print("Enter expense ID to edit: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter new category: ");
        String category = scanner.nextLine();
        System.out.print("Enter new amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter new date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter new description: ");
        String description = scanner.nextLine();

        String sql = "UPDATE expenses SET category = ?, amount = ?, date = ?, description = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, date);
            pstmt.setString(4, description);
            pstmt.setInt(5, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Expense updated successfully.");
            } else {
                System.out.println("Expense not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteExpense(Scanner scanner) {
        System.out.print("Enter expense ID to delete: ");
        int id = scanner.nextInt();

        String sql = "DELETE FROM expenses WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Expense deleted successfully.");
            } else {
                System.out.println("Expense not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void generateExpenseReport() {
        String sql = "SELECT category, SUM(amount) as total FROM expenses GROUP BY category";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nExpense Report:");
            System.out.println("Category | Total Amount");
            while (rs.next()) {
                System.out.println(rs.getString("category") + " | " + rs.getDouble("total"));
            }

            sql = "SELECT MAX(amount) as maxExpense, MIN(amount) as minExpense, SUM(amount) as totalExpense FROM expenses";
            try (Statement stmt2 = conn.createStatement(); ResultSet rs2 = stmt2.executeQuery(sql)) {
                if (rs2.next()) {
                    System.out.println("\nHighest Expense: " + rs2.getDouble("maxExpense"));
                    System.out.println("Lowest Expense: " + rs2.getDouble("minExpense"));
                    System.out.println("Total Expenses: " + rs2.getDouble("totalExpense"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void performArithmetic(Scanner scanner) {
        System.out.print("Enter first number: ");
        double num1 = scanner.nextDouble();

        System.out.print("Enter second number: ");
        double num2 = scanner.nextDouble();

        System.out.println("Choose operation: ");
        System.out.println("1. Addition");
        System.out.println("2. Subtraction");
        System.out.println("3. Multiplication");
        System.out.println("4. Division");
        System.out.print("Enter your choice: ");
        int operation = scanner.nextInt();

        double result;
        switch (operation) {
            case 1 -> result = num1 + num2;
            case 2 -> result = num1 - num2;
            case 3 -> result = num1 * num2;
            case 4 -> {
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    System.out.println("Error: Division by zero is not allowed.");
                    return;
                }
            }
            default -> {
                System.out.println("Invalid choice! Please select a valid operation.");
                return;
            }
        }

        System.out.println("Result: " + result);
    }
}
