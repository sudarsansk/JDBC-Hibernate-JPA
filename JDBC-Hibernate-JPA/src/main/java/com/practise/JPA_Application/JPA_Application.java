package com.practise.JPA_Application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.h2.tools.Server;

public class JPA_Application {

	public static JPAService jpaService;
	public static Scanner scanner;
	public static JPAUtil jpaUtil;
	
	public static void main(String[] args) throws SQLException {
		JPAUtil.getEntityManagerFactory();
		
		jpaService = new JPAService();
	    scanner = new Scanner(System.in);

	    // Add shutdown hook
	    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	        System.out.println("Shutting down application...");
	        JPAUtil.close();
	        if (scanner != null) {
	            scanner.close();
	        }
	        System.out.println("Application closed successfully.");
	    }));
	    
	    Connection con = null;
	    try {
		    // Initialize sample data
		    //jpaService.initializeSampleData();
		    showMenu();
			con = DriverManager.getConnection("jdbc:h2:./data/jpaemployeedb;AUTO_SERVER=TRUE");
			Server.startWebServer(con);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.close();
			}
		}


	}

	private static void showMenu() {
boolean running = true;
        
        while (running) {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║        JPA EMPLOYEE MANAGEMENT MENU            ║");
            System.out.println("╠════════════════════════════════════════════════╣");
            System.out.println("║  1.  View All Employees                        ║");
            System.out.println("║  2.  Find Employee by ID                       ║");
            System.out.println("║  3.  Find Employees by Department              ║");
            System.out.println("║  4.  Add New Employee                          ║");
            System.out.println("║  5.  Update Employee                           ║");
            System.out.println("║  6.  Delete Employee                           ║");
            System.out.println("║  7.  Show Random Sample Employee               ║");
            System.out.println("║  8.  Find Employees by Salary Range            ║");
            System.out.println("║  9.  Department-wise Employee Count            ║");
            System.out.println("║  10. Search with Criteria API                  ║");
            System.out.println("║  11. Average Salary by Department              ║");
            System.out.println("║  12. Exit                                      ║");
            System.out.println("╚════════════════════════════════════════════════╝");
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
                    searchWithCriteria();
                    break;
                case 11:
                    showAverageSalaryByDepartment();
                    break;
                case 12:
                    System.out.println("👋 Exiting application...");
                    running = false;
                    break;
                default:
                    System.out.println("❌ Invalid option. Please try again.");
            }
        }
	}
	
	private static void viewAllEmployees() {
        System.out.println("\n════════════════ ALL EMPLOYEES ════════════════");
        List<EmployeeJPA> employees = jpaService.getAllEmployees();
        
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.printf("%-5s %-20s %-30s %-15s %-10s%n", 
                "ID", "Name", "Email", "Department", "Salary");
            System.out.println("─".repeat(85));
            
            for (EmployeeJPA emp : employees) {
                System.out.printf("%-5d %-20s %-30s %-15s $%-9.2f%n",
                    emp.getId(), emp.getName(), emp.getEmail(), 
                    emp.getDepartment(), emp.getSalary());
            }
            System.out.println("─".repeat(85));
            System.out.println("Total employees: " + employees.size());
        }
    }

    private static void findEmployeeById() {
        System.out.print("\n🔍 Enter Employee ID: ");
        int id = getIntInput();
        
        EmployeeJPA employee = jpaService.findEmployeeById(id);
        
        if (employee != null) {
            System.out.println("\n════════════ EMPLOYEE DETAILS ════════════");
            displayEmployee(employee);
        } else {
            System.out.println("❌ Employee with ID " + id + " not found.");
        }
    }

    private static void findEmployeesByDepartment() {
        System.out.print("\n🏢 Enter Department: ");
        String department = scanner.nextLine();
        
        List<EmployeeJPA> employees = jpaService.findEmployeesByDepartment(department);
        
        if (employees.isEmpty()) {
            System.out.println("❌ No employees found in " + department + " department.");
        } else {
            System.out.println("\n═══════ EMPLOYEES IN " + department.toUpperCase() + " DEPARTMENT ═══════");
            System.out.printf("%-5s %-20s %-30s %-10s%n", 
                "ID", "Name", "Email", "Salary");
            System.out.println("─".repeat(70));
            
            for (EmployeeJPA emp : employees) {
                System.out.printf("%-5d %-20s %-30s $%-9.2f%n",
                    emp.getId(), emp.getName(), emp.getEmail(), emp.getSalary());
            }
            System.out.println("─".repeat(70));
            System.out.println("Total: " + employees.size() + " employees");
        }
    }

    private static void addNewEmployee() {
        System.out.println("\n════════════ ADD NEW EMPLOYEE ════════════");
        
        System.out.print("👤 Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("📧 Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("🏢 Enter Department: ");
        String department = scanner.nextLine();
        
        System.out.print("💰 Enter Salary: ");
        double salary = getDoubleInput();
        
        EmployeeJPA newEmployee = new EmployeeJPA(name, email, department, salary);
        
        if (jpaService.saveEmployee(newEmployee)) {
            System.out.println("✅ Employee added successfully with ID: " + newEmployee.getId());
        } else {
            System.out.println("❌ Failed to add employee. Email might already exist.");
        }
    }

    private static void updateEmployee() {
        System.out.println("\n════════════ UPDATE EMPLOYEE ════════════");
        System.out.print("🔍 Enter Employee ID to update: ");
        int id = getIntInput();
        
        EmployeeJPA employee = jpaService.findEmployeeById(id);
        if (employee == null) {
            System.out.println("❌ Employee with ID " + id + " not found.");
            return;
        }
        
        System.out.println("\n📋 Current details:");
        displayEmployee(employee);
        
        System.out.print("\n👤 Enter new Name (press Enter to keep current): ");
        String name = scanner.nextLine();
        if (!name.trim().isEmpty()) {
            employee.setName(name);
        }
        
        System.out.print("📧 Enter new Email (press Enter to keep current): ");
        String email = scanner.nextLine();
        if (!email.trim().isEmpty()) {
            employee.setEmail(email);
        }
        
        System.out.print("🏢 Enter new Department (press Enter to keep current): ");
        String department = scanner.nextLine();
        if (!department.trim().isEmpty()) {
            employee.setDepartment(department);
        }
        
        System.out.print("💰 Enter new Salary (enter 0 to keep current): ");
        double salary = getDoubleInput();
        if (salary > 0) {
            employee.setSalary(salary);
        }
        
        if (jpaService.updateEmployee(employee)) {
            System.out.println("✅ Employee updated successfully!");
        } else {
            System.out.println("❌ Failed to update employee.");
        }
    }

    private static void deleteEmployee() {
        System.out.println("\n════════════ DELETE EMPLOYEE ════════════");
        System.out.print("🔍 Enter Employee ID to delete: ");
        int id = getIntInput();
        
        EmployeeJPA employee = jpaService.findEmployeeById(id);
        if (employee == null) {
            System.out.println("❌ Employee with ID " + id + " not found.");
            return;
        }
        
        System.out.println("\n⚠️  Employee to be deleted:");
        displayEmployee(employee);
        
        System.out.print("\n❓ Are you sure you want to delete this employee? (yes/no): ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
            if (jpaService.deleteEmployee(id)) {
                System.out.println("✅ Employee deleted successfully!");
            } else {
                System.out.println("❌ Failed to delete employee.");
            }
        } else {
            System.out.println("🚫 Delete operation cancelled.");
        }
    }

    private static void showSampleData() {
        System.out.println("\n════════════ RANDOM SAMPLE EMPLOYEE ════════════");
        EmployeeJPA sampleEmployee = jpaService.showSampleData();
        if (sampleEmployee != null) {
            displayEmployee(sampleEmployee);
        }
    }

    private static void findEmployeesBySalaryRange() {
        System.out.println("\n════════════ FIND BY SALARY RANGE ════════════");
        
        System.out.print("💰 Enter minimum salary: ");
        double minSalary = getDoubleInput();
        
        System.out.print("💰 Enter maximum salary: ");
        double maxSalary = getDoubleInput();
        
        List<EmployeeJPA> employees = jpaService.findEmployeesBySalaryRange(minSalary, maxSalary);
        
        if (employees.isEmpty()) {
            System.out.println("❌ No employees found in salary range $" + minSalary + " - $" + maxSalary);
        } else {
            System.out.println("\n═══════ EMPLOYEES WITH SALARY $" + minSalary + " - $" + maxSalary + " ═══════");
            System.out.printf("%-5s %-20s %-15s %-10s%n", 
                "ID", "Name", "Department", "Salary");
            System.out.println("─".repeat(55));
            
            for (EmployeeJPA emp : employees) {
                System.out.printf("%-5d %-20s %-15s $%-9.2f%n",
                    emp.getId(), emp.getName(), emp.getDepartment(), emp.getSalary());
            }
            System.out.println("─".repeat(55));
            System.out.println("Total: " + employees.size() + " employees");
        }
    }

    private static void showDepartmentWiseCount() {
        System.out.println("\n════════════ DEPARTMENT-WISE COUNT ════════════");
        List<Object[]> results = jpaService.getDepartmentWiseCount();
        
        if (results.isEmpty()) {
            System.out.println("❌ No employees found.");
        } else {
            System.out.printf("%-20s %-10s%n", "Department", "Count");
            System.out.println("─".repeat(30));
            
            for (Object[] result : results) {
                System.out.printf("%-20s %-10s%n", result[0], result[1]);
            }
            System.out.println("─".repeat(30));
        }
    }

    private static void searchWithCriteria() {
        System.out.println("\n════════════ SEARCH WITH CRITERIA API ════════════");
        
        System.out.print("🏢 Enter Department (or press Enter to skip): ");
        String department = scanner.nextLine();
        if (department.trim().isEmpty()) {
            department = null;
        }
        
        System.out.print("💰 Enter Minimum Salary (or enter 0 to skip): ");
        double minSalary = getDoubleInput();
        Double minSal = (minSalary > 0) ? minSalary : null;
        
        List<EmployeeJPA> employees = jpaService.findEmployeesUsingCriteria(department, minSal);
        
        if (employees.isEmpty()) {
            System.out.println("❌ No employees found matching criteria.");
        } else {
            System.out.println("\n═══════ SEARCH RESULTS ═══════");
            System.out.printf("%-5s %-20s %-15s %-10s%n", 
                "ID", "Name", "Department", "Salary");
            System.out.println("─".repeat(55));
            
            for (EmployeeJPA emp : employees) {
                System.out.printf("%-5d %-20s %-15s $%-9.2f%n",
                    emp.getId(), emp.getName(), emp.getDepartment(), emp.getSalary());
            }
            System.out.println("─".repeat(55));
            System.out.println("Total: " + employees.size() + " employees");
        }
    }

    private static void showAverageSalaryByDepartment() {
        System.out.println("\n════════════ AVERAGE SALARY BY DEPARTMENT ════════════");
        List<Object[]> results = jpaService.getAverageSalaryByDepartment();
        
        if (results.isEmpty()) {
            System.out.println("❌ No employees found.");
        } else {
            System.out.printf("%-20s %-15s %-10s%n", "Department", "Avg Salary", "Count");
            System.out.println("─".repeat(50));
            
            for (Object[] result : results) {
                System.out.printf("%-20s $%-14.2f %-10s%n", result[0], result[1], result[2]);
            }
            System.out.println("─".repeat(50));
        }
    }

    private static void displayEmployee(EmployeeJPA employee) {
        System.out.println("━".repeat(40));
        System.out.println("ID:         " + employee.getId());
        System.out.println("Name:       " + employee.getName());
        System.out.println("Email:      " + employee.getEmail());
        System.out.println("Department: " + employee.getDepartment());
        System.out.println("Salary:     $" + String.format("%.2f", employee.getSalary()));
        System.out.println("━".repeat(40));
    }

    private static int getIntInput() {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input. Please enter a number: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                double input = Double.parseDouble(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input. Please enter a valid number: ");
            }
        }
    }
	
}
