import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class VinylShopDB {

    static final String URL = "jdbc:sqlite:vinylshop.db";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Vinyl Shop Database Menu ---");
            System.out.println("1. Create tables");
            System.out.println("2. Insert sample data");
            System.out.println("3. View artists");
            System.out.println("4. View vinyl records");
            System.out.println("5. View customer orders");
            System.out.println("6. Update vinyl stock");
            System.out.println("7. Delete customer order");
            System.out.println("8. Add new vinyl");
            System.out.println("9. Search vinyl by title");
            System.out.println("10. Place customer order");
            System.out.println("11. Exit");
            System.out.print("Enter your choice: ");

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1:
                    createTables();
                    break;
                case 2:
                    insertSampleData();
                    break;
                case 3:
                    viewArtists();
                    break;
                case 4:
                    viewVinyl();
                    break;
                case 5:
                    viewOrders();
                    break;
                case 6:
                    updateVinylStock(input);
                    break;
                case 7:
                    deleteOrder(input);
                    break;
                case 8:
                    addNewVinyl(input);
                    break;
                case 9:
                    searchVinylByTitle(input);
                    break;
                case 10:
                    placeCustomerOrder(input);
                    break;
                case 11:
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 11);

        input.close();
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String artistTable = "CREATE TABLE IF NOT EXISTS Artist (" +
                    "artist_id INT PRIMARY KEY, " +
                    "artist_name VARCHAR(100) NOT NULL, " +
                    "country VARCHAR(50))";

            String vinylTable = "CREATE TABLE IF NOT EXISTS Vinyl (" +
                    "vinyl_id INT PRIMARY KEY, " +
                    "title VARCHAR(100) NOT NULL, " +
                    "genre VARCHAR(50), " +
                    "release_year INT, " +
                    "price DECIMAL(8,2), " +
                    "stock_quantity INT, " +
                    "artist_id INT, " +
                    "FOREIGN KEY (artist_id) REFERENCES Artist(artist_id))";

            String orderTable = "CREATE TABLE IF NOT EXISTS CustomerOrder (" +
                    "order_id INT PRIMARY KEY, " +
                    "customer_name VARCHAR(100), " +
                    "order_date TEXT, " +
                    "quantity INT, " +
                    "total_amount DECIMAL(8,2), " +
                    "vinyl_id INT, " +
                    "FOREIGN KEY (vinyl_id) REFERENCES Vinyl(vinyl_id))";

            stmt.execute(artistTable);
            stmt.execute(vinylTable);
            stmt.execute(orderTable);

            System.out.println("Tables created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void insertSampleData() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("INSERT OR IGNORE INTO Artist VALUES (1, 'The Weeknd', 'Canada')");
            stmt.executeUpdate("INSERT OR IGNORE INTO Artist VALUES (2, 'Lana Del Rey', 'USA')");
            stmt.executeUpdate("INSERT OR IGNORE INTO Artist VALUES (3, 'Fleetwood Mac', 'UK')");
		

            stmt.executeUpdate("INSERT INTO Vinyl VALUES (101, 'After Hours', 'R&B', 2020, 34.99, 5, 1)");
            stmt.executeUpdate("INSERT INTO Vinyl VALUES (102, 'Born To Die', 'Alternative', 2012, 29.99, 3, 2)");

            stmt.executeUpdate("INSERT INTO CustomerOrder VALUES (1001, 'Alice', '2026-04-06', 1, 34.99, 101)");
            stmt.executeUpdate("INSERT INTO CustomerOrder VALUES (1002, 'Bob', '2026-04-06', 2, 59.98, 102)");

            System.out.println("Sample data inserted.");

        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }

    public static void viewArtists() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Artist");

            System.out.println("\nArtists:");
            while (rs.next()) {
                System.out.println(rs.getInt("artist_id") + " - " +
                                   rs.getString("artist_name") + " - " +
                                   rs.getString("country"));
            }

        } catch (SQLException e) {
            System.out.println("Error viewing artists: " + e.getMessage());
        }
    }

    public static void viewVinyl() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                    "SELECT v.vinyl_id, v.title, v.genre, v.release_year, v.price, v.stock_quantity, a.artist_name " +
                    "FROM Vinyl v LEFT JOIN Artist a ON v.artist_id = a.artist_id");

            System.out.println("\nVinyl Records:");
            while (rs.next()) {
                System.out.println(rs.getInt("vinyl_id") + " - " +
                                   rs.getString("title") + " - " +
                                   rs.getString("genre") + " - " +
                                   rs.getInt("release_year") + " - $" +
                                   rs.getDouble("price") + " - Stock: " +
                                   rs.getInt("stock_quantity") + " - Artist: " +
                                   rs.getString("artist_name"));
            }

        } catch (SQLException e) {
            System.out.println("Error viewing vinyl: " + e.getMessage());
        }
    }

    public static void viewOrders() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                    "SELECT o.order_id, o.customer_name, v.title, o.quantity, o.total_amount, o.order_date " +
                    "FROM CustomerOrder o JOIN Vinyl v ON o.vinyl_id = v.vinyl_id");

            System.out.println("\nCustomer Orders:");
            while (rs.next()) {
                System.out.println("Order ID: " + rs.getInt("order_id") +
                                   ", Customer: " + rs.getString("customer_name") +
                                   ", Vinyl: " + rs.getString("title") +
                                   ", Quantity: " + rs.getInt("quantity") +
                                   ", Total: $" + rs.getDouble("total_amount") +
                                   ", Date: " + rs.getString("order_date"));
            }

        } catch (SQLException e) {
            System.out.println("Error viewing orders: " + e.getMessage());
        }
    }

    public static void updateVinylStock(Scanner input) {
        System.out.print("Enter vinyl ID: ");
        int vinylId = input.nextInt();

        System.out.print("Enter new stock quantity: ");
        int newStock = input.nextInt();
        input.nextLine();

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String sql = "UPDATE Vinyl SET stock_quantity = " + newStock +
                         " WHERE vinyl_id = " + vinylId;
            int rows = stmt.executeUpdate(sql);

            if (rows > 0) {
                System.out.println("Vinyl stock updated.");
            } else {
                System.out.println("Vinyl not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating stock: " + e.getMessage());
        }
    }

    public static void deleteOrder(Scanner input) {
        System.out.print("Enter order ID to delete: ");
        int orderId = input.nextInt();
        input.nextLine();

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM CustomerOrder WHERE order_id = " + orderId;
            int rows = stmt.executeUpdate(sql);

            if (rows > 0) {
                System.out.println("Order deleted.");
            } else {
                System.out.println("Order not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting order: " + e.getMessage());
        }
    }

    public static void addNewVinyl(Scanner input) {
        System.out.print("Enter vinyl ID: ");
        int vinylId = input.nextInt();
        input.nextLine();

        System.out.print("Enter title: ");
        String title = input.nextLine();

        System.out.print("Enter genre: ");
        String genre = input.nextLine();

        System.out.print("Enter release year: ");
        int releaseYear = input.nextInt();

        System.out.print("Enter price: ");
        double price = input.nextDouble();

        System.out.print("Enter stock quantity: ");
        int stockQuantity = input.nextInt();

        System.out.print("Enter artist ID: ");
        int artistId = input.nextInt();
        input.nextLine();

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String sql = "INSERT INTO Vinyl VALUES (" +
                    vinylId + ", '" +
                    title + "', '" +
                    genre + "', " +
                    releaseYear + ", " +
                    price + ", " +
                    stockQuantity + ", " +
                    artistId + ")";

            stmt.executeUpdate(sql);
            System.out.println("New vinyl added successfully.");

        } catch (SQLException e) {
            System.out.println("Error adding vinyl: " + e.getMessage());
        }
    }

    public static void searchVinylByTitle(Scanner input) {
        System.out.print("Enter vinyl title to search: ");
        String title = input.nextLine();

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String sql = "SELECT v.vinyl_id, v.title, v.genre, v.release_year, v.price, v.stock_quantity, a.artist_name " +
                         "FROM Vinyl v JOIN Artist a ON v.artist_id = a.artist_id " +
                         "WHERE v.title LIKE '%" + title + "%'";

            ResultSet rs = stmt.executeQuery(sql);

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(rs.getInt("vinyl_id") + " - " +
                                   rs.getString("title") + " - " +
                                   rs.getString("genre") + " - " +
                                   rs.getInt("release_year") + " - $" +
                                   rs.getDouble("price") + " - Stock: " +
                                   rs.getInt("stock_quantity") + " - Artist: " +
                                   rs.getString("artist_name"));
            }

            if (!found) {
                System.out.println("No vinyl found with that title.");
            }

        } catch (SQLException e) {
            System.out.println("Error searching vinyl: " + e.getMessage());
        }
    }

    public static void placeCustomerOrder(Scanner input) {
        System.out.print("Enter order ID: ");
        int orderId = input.nextInt();
        input.nextLine();

        System.out.print("Enter customer name: ");
        String customerName = input.nextLine();

        System.out.print("Enter order date (YYYY-MM-DD): ");
        String orderDate = input.nextLine();

        System.out.print("Enter vinyl ID: ");
        int vinylId = input.nextInt();

        System.out.print("Enter quantity: ");
        int quantity = input.nextInt();
        input.nextLine();

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            String getVinylSql = "SELECT price, stock_quantity FROM Vinyl WHERE vinyl_id = " + vinylId;
            ResultSet rs = stmt.executeQuery(getVinylSql);

            if (rs.next()) {
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock_quantity");

                if (quantity > stock) {
                    System.out.println("Not enough stock available.");
                    return;
                }

                double totalAmount = price * quantity;

                String insertOrderSql = "INSERT INTO CustomerOrder VALUES (" +
                        orderId + ", '" +
                        customerName + "', '" +
                        orderDate + "', " +
                        quantity + ", " +
                        totalAmount + ", " +
                        vinylId + ")";

                stmt.executeUpdate(insertOrderSql);

                String updateStockSql = "UPDATE Vinyl SET stock_quantity = " +
                        (stock - quantity) + " WHERE vinyl_id = " + vinylId;
                stmt.executeUpdate(updateStockSql);

                System.out.println("Customer order placed successfully.");
                System.out.println("Total amount: $" + totalAmount);

            } else {
                System.out.println("Vinyl ID not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error placing order: " + e.getMessage());
        }
    }
}