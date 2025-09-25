package com.practise.Hibernate_Application;

import java.util.List;
import java.util.Scanner;

public class Hibernate_Application {

	private static HiberanteService employeeDAO;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("=== Employee Management System with Hibernate ===");
        System.out.println("Using H2 Database with Hibernate ORM");
        System.out.println();

        try {
            // Initialize DAO and scanner
            employeeDAO = new HiberanteService();
            scanner = new Scanner(System.in);

            // Add shutdown hook to close Hibernate SessionFactory
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down application...");
                HibernateUtil.shutdown();
                if (scanner != null) {
                    scanner.close();
                }
            }));

            // Initialize sample data
            employeeDAO.initializeSampleData();

            // Start the application menu
            showMenu();

        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMenu() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n=== HIBERNATE EMPLOYEE MANAGEMENT MENU ===");
            System.out.println("1. View All Employees");
            System.out.println("2. Find Employee by ID");
            System.out.println("3. Find Employees by Department");
            System.out.println("4. Add New Employee");
            System.out.println("5. Update Employee");
            System.out.println("6. Delete Employee");
            System.out.println("7. Show Sample Data (Random Employee)");
            System.out.println("8. Find Employees by Salary Range");
            System.out.println("9. Department-wise Employee Count");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            
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
                    updateEmployee();
                    break;
                case 6:
                    deleteEmployee();
                    break;
                case 7:
                    showSampleData();
                    break;
                case 8:
                    findEmployeesBySalaryRange();
                    break;
                case 9:
                    showDepartmentWiseCount();
                    break;
                case 10:
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
        List<EmployeeHiber> employees = employeeDAO.getAllEmployees();
        
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.printf("%-5s %-20s %-30s %-15s %-10s%n", 
                "ID", "Name", "Email", "Department", "Salary");
            System.out.println("-".repeat(85));
            
            for (EmployeeHiber emp : employees) {
                System.out.printf("%-5d %-20s %-30s %-15s $%-9.2f%n",
                    emp.getId(), emp.getName(), emp.getEmail(), 
                    emp.getDepartment(), emp.getSalary());
            }
            System.out.println("\nTotal employees: " + employees.size());
        }
    }

    private static void findEmployeeById() {
        System.out.print("\nEnter Employee ID: ");
        int id = scanner.nextInt();
        
        EmployeeHiber employee = employeeDAO.findEmployeeById(id);
        
        if (employee != null) {
            System.out.println("\n=== EMPLOYEE DETAILS ===");
            displayEmployee(employee);
        } else {
            System.out.println("Employee with ID " + id + " not found.");
        }
    }

    private static void findEmployeesByDepartment() {
        System.out.print("\nEnter Department: ");
        String department = scanner.nextLine();
        
        List<EmployeeHiber> employees = employeeDAO.findEmployeesByDepartment(department);
        
        if (employees.isEmpty()) {
            System.out.println("No employees found in " + department + " department.");
        } else {
            System.out.println("\n=== EMPLOYEES IN " + department.toUpperCase() + " DEPARTMENT ===");
            System.out.printf("%-5s %-20s %-30s %-10s%n", 
                "ID", "Name", "Email", "Salary");
            System.out.println("-".repeat(70));
            
            for (EmployeeHiber emp : employees) {
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
        double salary = scanner.nextDouble();
        
        EmployeeHiber newEmployee = new EmployeeHiber(name, email, department, salary);
        
        if (employeeDAO.saveEmployee(newEmployee)) {
            System.out.println("Employee added successfully with ID: " + newEmployee.getId());
        } else {
            System.out.println("Failed to add employee. Email might already exist.");
        }
    }

    private static void updateEmployee() {
        System.out.println("\n=== UPDATE EMPLOYEE ===");
        System.out.print("Enter Employee ID to update: ");
        int id = scanner.nextInt();
        
        EmployeeHiber employee = employeeDAO.findEmployeeById(id);
        if (employee == null) {
            System.out.println("Employee with ID " + id + " not found.");
            return;
        }
        
        System.out.println("Current details:");
        displayEmployee(employee);
        
        System.out.print("Enter new Name (press Enter to keep current): ");
        String name = scanner.nextLine();
        if (!name.trim().isEmpty()) {
            employee.setName(name);
        }
        
        System.out.print("Enter new Email (press Enter to keep current): ");
        String email = scanner.nextLine();
        if (!email.trim().isEmpty()) {
            employee.setEmail(email);
        }
        
        System.out.print("Enter new Department (press Enter to keep current): ");
        String department = scanner.nextLine();
        if (!department.trim().isEmpty()) {
            employee.setDepartment(department);
        }
        
        System.out.print("Enter new Salary (enter 0 to keep current): ");
        double salary = scanner.nextDouble();
        if (salary > 0) {
            employee.setSalary(salary);
        }
        
        if (employeeDAO.updateEmployee(employee)) {
            System.out.println("Employee updated successfully!");
        } else {
            System.out.println("Failed to update employee.");
        }
    }

    private static void deleteEmployee() {
        System.out.println("\n=== DELETE EMPLOYEE ===");
        System.out.print("Enter Employee ID to delete: ");
        int id = scanner.nextInt();
        
        EmployeeHiber employee = employeeDAO.findEmployeeById(id);
        if (employee == null) {
            System.out.println("Employee with ID " + id + " not found.");
            return;
        }
        
        System.out.println("Employee to be deleted:");
        displayEmployee(employee);
        
        System.out.print("Are you sure you want to delete this employee? (yes/no): ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
            if (employeeDAO.deleteEmployee(id)) {
                System.out.println("Employee deleted successfully!");
            } else {
                System.out.println("Failed to delete employee.");
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    private static void showSampleData() {
        System.out.println("\n=== SAMPLE DATA ===");
        EmployeeHiber sampleEmployee = employeeDAO.showSampleData();
        if (sampleEmployee != null) {
            displayEmployee(sampleEmployee);
        }
    }

    private static void findEmployeesBySalaryRange() {
        System.out.println("\n=== FIND EMPLOYEES BY SALARY RANGE ===");
        
        System.out.print("Enter minimum salary: ");
        double minSalary = scanner.nextDouble();
        
        System.out.print("Enter maximum salary: ");
        double maxSalary = scanner.nextDouble();
        
        List<EmployeeHiber> employees = employeeDAO.findEmployeesBySalaryRange(minSalary, maxSalary);
        
        if (employees.isEmpty()) {
            System.out.println("No employees found in salary range $" + minSalary + " - $" + maxSalary);
        } else {
            System.out.println("\n=== EMPLOYEES WITH SALARY $" + minSalary + " - $" + maxSalary + " ===");
            System.out.printf("%-5s %-20s %-15s %-10s%n", 
                "ID", "Name", "Department", "Salary");
            System.out.println("-".repeat(55));
            
            for (EmployeeHiber emp : employees) {
                System.out.printf("%-5d %-20s %-15s $%-9.2f%n",
                    emp.getId(), emp.getName(), emp.getDepartment(), emp.getSalary());
            }
        }
    }

    private static void showDepartmentWiseCount() {
        System.out.println("\n=== DEPARTMENT-WISE EMPLOYEE COUNT ===");
        List<Object[]> results = employeeDAO.getDepartmentWiseCount();
        
        if (results.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.printf("%-15s %-10s%n", "Department", "Count");
            System.out.println("-".repeat(25));
            
            for (Object[] result : results) {
                System.out.printf("%-15s %-10s%n", result[0], result[1]);
            }
        }
    }

    private static void displayEmployee(EmployeeHiber employee) {
        System.out.println("ID: " + employee.getId());
        System.out.println("Name: " + employee.getName());
        System.out.println("Email: " + employee.getEmail());
        System.out.println("Department: " + employee.getDepartment());
        System.out.println("Salary: $" + employee.getSalary());
    }
}
