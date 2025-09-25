package com.practise.JDBC_Application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcService {

	// Simple H2 file database - no configuration needed
    private static final String DB_URL = "jdbc:h2:./data/employeedb";
    
    public JdbcService() {
        try {
            // Initialize database and sample data
            initializeDatabase();
            insertSampleData();
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS employees (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                department VARCHAR(50) NOT NULL,
                salary DECIMAL(10,2) NOT NULL
            )
            """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Employee table ready!");
        }
    }

    private void insertSampleData() throws SQLException {
        // Check if data already exists
        String checkSQL = "SELECT COUNT(*) FROM employees";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSQL)) {
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("Sample data already exists.");
                return;
            }
        }

        // Insert sample data
        String insertSQL = """
            INSERT INTO employees (name, email, department, salary) VALUES
            ('John Doe', 'john.doe@company.com', 'Engineering', 75000.00),
            ('Jane Smith', 'jane.smith@company.com', 'Marketing', 65000.00),
            ('Mike Johnson', 'mike.johnson@company.com', 'Engineering', 80000.00),
            ('Sarah Williams', 'sarah.williams@company.com', 'HR', 55000.00),
            ('David Brown', 'david.brown@company.com', 'Finance', 70000.00),
            ('Lisa Davis', 'lisa.davis@company.com', 'Engineering', 78000.00),
            ('Tom Wilson', 'tom.wilson@company.com', 'Marketing', 62000.00),
            ('Emma Garcia', 'emma.garcia@company.com', 'HR', 58000.00)
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(insertSQL);
            System.out.println("Sample data inserted successfully!");
        }
    }

    public List<EmployeeJDBC> getAllEmployees() {
        List<EmployeeJDBC> employees = new ArrayList<>();
        String selectSQL = "SELECT * FROM employees ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            
            while (rs.next()) {
            	EmployeeJDBC emp = new EmployeeJDBC(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department"),
                    rs.getDouble("salary")
                );
                employees.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
        
        return employees;
    }

    public EmployeeJDBC getEmployeeById(int id) {
        String selectSQL = "SELECT * FROM employees WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new EmployeeJDBC(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department"),
                    rs.getDouble("salary")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee: " + e.getMessage());
        }
        
        return null;
    }

    public List<EmployeeJDBC> getEmployeesByDepartment(String department) {
        List<EmployeeJDBC> employees = new ArrayList<>();
        String selectSQL = "SELECT * FROM employees WHERE department = ? ORDER BY name";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, department);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	EmployeeJDBC emp = new EmployeeJDBC(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department"),
                    rs.getDouble("salary")
                );
                employees.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees by department: " + e.getMessage());
        }
        
        return employees;
    }

    public boolean addEmployee(EmployeeJDBC employee) {
        String insertSQL = "INSERT INTO employees (name, email, department, salary) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getDepartment());
            pstmt.setDouble(4, employee.getSalary());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }
}
