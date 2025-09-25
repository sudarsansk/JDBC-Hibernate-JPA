package com.practise.Hibernate_Application;

import java.util.List;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class HiberanteService {

	// Save employee (Insert)
    public boolean saveEmployee(EmployeeHiber employee) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(employee);  // Hibernate 6.x method
            transaction.commit();
            System.out.println("Employee saved successfully with ID: " + employee.getId());
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error saving employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Find employee by ID
    public EmployeeHiber findEmployeeById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        	EmployeeHiber employee = session.get(EmployeeHiber.class, id);
            if (employee != null) {
                System.out.println("Found employee: " + employee);
            } else {
                System.out.println("No employee found with ID: " + id);
            }
            return employee;
        } catch (Exception e) {
            System.err.println("Error finding employee by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Get all employees
    public List<EmployeeHiber> getAllEmployees() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Using HQL (Hibernate Query Language)
            Query<EmployeeHiber> query = session.createQuery("FROM Employee ORDER BY id", EmployeeHiber.class);
            List<EmployeeHiber> employees = query.getResultList();
            System.out.println("Found " + employees.size() + " employees");
            return employees;
        } catch (Exception e) {
            System.err.println("Error getting all employees: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Return empty list
        }
    }

    // Find employees by department
    public List<EmployeeHiber> findEmployeesByDepartment(String department) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Using HQL with parameter
            String hql = "FROM Employee WHERE department = :dept ORDER BY name";
            Query<EmployeeHiber> query = session.createQuery(hql, EmployeeHiber.class);
            query.setParameter("dept", department);
            List<EmployeeHiber> employees = query.getResultList();
            System.out.println("Found " + employees.size() + " employees in " + department + " department");
            return employees;
        } catch (Exception e) {
            System.err.println("Error finding employees by department: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // Update employee
    public boolean updateEmployee(EmployeeHiber employee) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(employee);  // Hibernate 6.x method for update
            transaction.commit();
            System.out.println("Employee updated successfully: " + employee);
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete employee by ID
    public boolean deleteEmployee(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            EmployeeHiber employee = session.get(EmployeeHiber.class, id);
            if (employee != null) {
                session.remove(employee);  // Hibernate 6.x method for delete
                transaction.commit();
                System.out.println("Employee deleted successfully: " + employee);
                return true;
            } else {
                System.out.println("No employee found with ID: " + id);
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Show sample data - get a random employee
    public EmployeeHiber showSampleData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // First, get the count of employees
            Long count = session.createQuery("SELECT COUNT(e) FROM Employee e", Long.class)
                               .getSingleResult();
            
            if (count == 0) {
                System.out.println("No employees in database");
                return null;
            }

            // Generate random ID between 1 and count
            Random random = new Random();
            int randomId = random.nextInt(count.intValue()) + 1;
            
            // Get employee by the random ID
            EmployeeHiber employee = session.get(EmployeeHiber.class, randomId);
            
            if (employee != null) {
                System.out.println("Random sample employee: " + employee);
            } else {
                // Fallback: get first employee if random ID doesn't exist
                employee = session.createQuery("FROM Employee ORDER BY id", EmployeeHiber.class)
                                 .setMaxResults(1)
                                 .getSingleResult();
                System.out.println("Sample employee (first in database): " + employee);
            }
            
            return employee;
        } catch (Exception e) {
            System.err.println("Error getting sample data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Get employees with salary greater than specified amount
    public List<EmployeeHiber> findEmployeesBySalaryRange(double minSalary, double maxSalary) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Employee WHERE salary BETWEEN :minSal AND :maxSal ORDER BY salary DESC";
            Query<EmployeeHiber> query = session.createQuery(hql, EmployeeHiber.class);
            query.setParameter("minSal", minSalary);
            query.setParameter("maxSal", maxSalary);
            List<EmployeeHiber> employees = query.getResultList();
            System.out.println("Found " + employees.size() + " employees with salary between " 
                             + minSalary + " and " + maxSalary);
            return employees;
        } catch (Exception e) {
            System.err.println("Error finding employees by salary range: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // Get department-wise employee count
    public List<Object[]> getDepartmentWiseCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT department, COUNT(*) FROM Employee GROUP BY department ORDER BY COUNT(*) DESC";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            List<Object[]> results = query.getResultList();
            
            System.out.println("Department-wise employee count:");
            for (Object[] result : results) {
                System.out.println(result[0] + ": " + result[1] + " employees");
            }
            
            return results;
        } catch (Exception e) {
            System.err.println("Error getting department-wise count: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // Initialize sample data
    public void initializeSampleData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Check if data already exists
            Long count = session.createQuery("SELECT COUNT(e) FROM Employee e", Long.class)
                               .getSingleResult();
            
            if (count > 0) {
                System.out.println("Sample data already exists (" + count + " employees)");
                return;
            }

            Transaction transaction = session.beginTransaction();
            
            // Create sample employees
            EmployeeHiber[] employees = {
                new EmployeeHiber("John Doe", "john.doe@company.com", "Engineering", 75000.00),
                new EmployeeHiber("Jane Smith", "jane.smith@company.com", "Marketing", 65000.00),
                new EmployeeHiber("Mike Johnson", "mike.johnson@company.com", "Engineering", 80000.00),
                new EmployeeHiber("Sarah Williams", "sarah.williams@company.com", "HR", 55000.00),
                new EmployeeHiber("David Brown", "david.brown@company.com", "Finance", 70000.00),
                new EmployeeHiber("Lisa Davis", "lisa.davis@company.com", "Engineering", 78000.00),
                new EmployeeHiber("Tom Wilson", "tom.wilson@company.com", "Marketing", 62000.00),
                new EmployeeHiber("Emma Garcia", "emma.garcia@company.com", "HR", 58000.00)
            };

            for (EmployeeHiber emp : employees) {
                session.persist(emp);
            }

            transaction.commit();
            System.out.println("Sample data initialized successfully: " + employees.length + " employees added");
            
        } catch (Exception e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
