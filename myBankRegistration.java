import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class myBankRegistration {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mybankdb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "MySQL";
    private static Random random = new Random();

    // Load the MySQL JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Include it in your library path.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            connection.setAutoCommit(false); // Disable auto-commit mode
            createTableIfNotExists(connection);

            Scanner scanner = new Scanner(System.in);

            while (true) {

                System.out.println("Welcome to myBank!");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.print("Please enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        registerCustomer(connection, scanner);
                        break;
                    case 2:
                        System.out.println("The page is under construction.");
                        System.out.println();
                        System.out.println();
                        System.out.println();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        System.out.println();
                        System.out.println();
                        System.out.println();
                }
            }
        } catch (SQLException e) {
            System.err.println("Connection to database failed.");
            e.printStackTrace();
        }
    }

    private static void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS customers ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "first_name VARCHAR(50) NOT NULL, "
                + "last_name VARCHAR(50) NOT NULL, "
                + "mobile_number VARCHAR(15) NOT NULL, "
                + "email VARCHAR(100) NOT NULL UNIQUE, "
                + "username VARCHAR(50) NOT NULL UNIQUE, "
                + "password VARCHAR(100) NOT NULL, "
                + "account_number VARCHAR(10) NOT NULL UNIQUE, "
                + "account_balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00"
                + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        }
    }

    private static void registerCustomer(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter mobile number: ");
        String mobileNumber = scanner.nextLine();

        String email;
        while (true) {
            System.out.print("Enter email (format: username@gmail.com): ");
            email = scanner.nextLine();
            if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
                System.out.println("Invalid email format. Please try again.");
            } else if (!isUniqueEmail(connection, email)) {
                System.out.println("Email already taken. Please try again.");
            } else {
                break;
            }
        }

        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (isUniqueUsername(connection, username)) {
                break;
            } else {
                System.out.println("Username already taken. Please try again.");
            }
        }

        String password;
        while (true) {
            System.out.print("Enter password (must contain uppercase, lowercase, digit, and special character): ");
            password = scanner.nextLine();
            if (isValidPassword(password)) {
                break;
            } else {
                System.out.println("Invalid password. Please try again.");
            }
        } //u

        String accountNumber = generateUniqueAccountNumber(connection);
        double accountBalance = 0.0;

        Customer customer = new Customer(firstName, lastName, mobileNumber, email, username, password, accountNumber, accountBalance);

        try {
            saveToDatabase(connection, customer);
            connection.commit(); // Commit the transaction
            displayCustomerInfo(customer);
        } catch (SQLException e) {
            connection.rollback(); // Rollback in case of error
            e.printStackTrace();
        }
    }

    private static String generateUniqueAccountNumber(Connection connection) throws SQLException {
        String accountNumber;
        do {
            accountNumber = String.format("%010d", random.nextInt(1000000000));
        } while (!isUniqueAccountNumber(connection, accountNumber));
        return accountNumber;
    }

    private static boolean isUniqueAccountNumber(Connection connection, String accountNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM customers WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        }
        return false;
    }

    private static boolean isUniqueUsername(Connection connection, String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM customers WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        }
        return false;
    }

    private static boolean isUniqueEmail(Connection connection, String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM customers WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        }
        return false;
    }

    private static boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    private static void saveToDatabase(Connection connection, Customer customer) throws SQLException {
        String insertCustomerSQL = "INSERT INTO customers (first_name, last_name, mobile_number, email, username, password, account_number, account_balance) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCustomerSQL)) {
            preparedStatement.setString(1, customer.getFirstName());
            preparedStatement.setString(2, customer.getLastName());
            preparedStatement.setString(3, customer.getMobileNumber());
            preparedStatement.setString(4, customer.getEmail());
            preparedStatement.setString(5, customer.getUsername());
            preparedStatement.setString(6, customer.getPassword());
            preparedStatement.setString(7, customer.getAccountNumber());
            preparedStatement.setDouble(8, customer.getAccountBalance());

            preparedStatement.executeUpdate();
        }
    }

    private static void displayCustomerInfo(Customer customer) {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Customer Profile:");
        System.out.println("First Name: " + customer.getFirstName());
        System.out.println("Last Name: " + customer.getLastName());
        System.out.println("Mobile Number: " + customer.getMobileNumber());
        System.out.println("Account Number: " + customer.getAccountNumber());
        System.out.println("Account Balance: $" + customer.getAccountBalance());
        System.out.println("Customer profile is created.");
        System.out.println();
        System.out.println();
        System.out.println();

    }
}
