package com.practise.JDBC_Application;

import java.util.List;
import java.util.Scanner;

public class JDBC_Application {

	private static JdbcService dbManager;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("=== Employee Management System ===");
        System.out.println("Using H2 Database");
        System.out.println();

        // Initialize database manager and scanner
        dbManager = new JdbcService();
        scanner = new Scanner(System.in);

        // Start the application menu
        showMenu();
        
        // Close scanner when done
        scanner.close();
    }

    private static void showMenu() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n=== MENU ===");
            System.out.println("1. View All Employees");
            System.out.println("2. Find Employee by ID");
            System.out.println("3. Find Employees by Department");
            System.out.println("4. Add New Employee");
            System.out.println("5. Show Sample Data");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    viewAllEmployees();
                    break;
                case 2:
                    findEmployeeById();
                    break;
                case 3:
                    findEmployeesByDepartment();
                    break;
                case 4:
                    addNewEmployee();
                    break;
                case 5:
                    showSampleData();
                    break;
                case 6:
                    System.out.println("Exiting application...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewAllEmployees() {
        System.out.println("\n=== ALL EMPLOYEES ===");
        List<EmployeeJDBC> employees = dbManager.getAllEmployees();
        
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.printf("%-5s %-20s %-30s %-15s %-10s%n", 
                "ID", "Name", "Email", "Department", "Salary");
            System.out.println("-".repeat(85));
            
            for (EmployeeJDBC emp : employees) {
                System.out.printf("%-5d %-20s %-30s %-15s $%-9.2f%n",
                    emp.getId(), emp.getName(), emp.getEmail(), 
                    emp.getDepartment(), emp.getSalary());
            }
            System.out.println("\nTotal employees: " + employees.size());
        }
    }

    private static void findEmployeeById() {
        System.out.print("\nEnter Employee ID: ");
        int id = getIntInput();
        
        EmployeeJDBC employee = dbManager.getEmployeeById(id);
        
        if (employee != null) {
            System.out.println("\n=== EMPLOYEE DETAILS ===");
            System.out.println("ID: " + employee.getId());
            System.out.println("Name: " + employee.getName());
            System.out.println("Email: " + employee.getEmail());
            System.out.println("Department: " + employee.getDepartment());
            System.out.println("Salary: $" + employee.getSalary());
        } else {
            System.out.println("Employee with ID " + id + " not found.");
        }
    }

    private static void findEmployeesByDepartment() {
        System.out.print("\nEnter Department: ");
        String department = scanner.nextLine();
        
        List<EmployeeJDBC> employees = dbManager.getEmployeesByDepartment(department);
        
        if (employees.isEmpty()) {
            System.out.println("No employees found in " + department + " department.");
        } else {
            System.out.println("\n=== EMPLOYEES IN " + department.toUpperCase() + " DEPARTMENT ===");
            System.out.printf("%-5s %-20s %-30s %-10s%n", 
                "ID", "Name", "Email", "Salary");
            System.out.println("-".repeat(70));
            
            for (EmployeeJDBC emp : employees) {
                System.out.printf("%-5d %-20s %-30s $%-9.2f%n",
                    emp.getId(), emp.getName(), emp.getEmail(), emp.getSalary());
            }
            System.out.println("\nTotal employees in " + department + ": " + employees.size());
        }
    }

    private static void addNewEmployee() {
        System.out.println("\n=== ADD NEW EMPLOYEE ===");
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Department: ");
        String department = scanner.nextLine();
        
        System.out.print("Enter Salary: ");
        double salary = getDoubleInput();
        
        EmployeeJDBC newEmployee = new EmployeeJDBC(name, email, department, salary);
        
        if (dbManager.addEmployee(newEmployee)) {
            System.out.println("Employee added successfully!");
        } else {
            System.out.println("Failed to add employee. Email might already exist.");
        }
    }

    private static void showSampleData() {
        System.out.println("\n=== SAMPLE DATA OVERVIEW ===");
        System.out.println("The database contains the following sample employees:");
        System.out.println("• Engineering Department: John Doe, Mike Johnson, Lisa Davis");
        System.out.println("• Marketing Department: Jane Smith, Tom Wilson");
        System.out.println("• HR Department: Sarah Williams, Emma Garcia");
        System.out.println("• Finance Department: David Brown");
        System.out.println("\nUse option 1 to view detailed information about all employees.");
    }

    private static int getIntInput() {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                double input = Double.parseDouble(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid salary: ");
            }
        }
    }
}
