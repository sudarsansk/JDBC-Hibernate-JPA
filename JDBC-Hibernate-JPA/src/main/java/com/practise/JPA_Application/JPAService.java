package com.practise.JPA_Application;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import java.util.List;
import java.util.Random;

public class JPAService {

    // Save employee (Insert)
    public boolean saveEmployee(EmployeeJPA employee) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = null;
        
        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(employee);
            transaction.commit();
            System.out.println("✓ Employee saved successfully with ID: " + employee.getId());
            return true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("✗ Error saving employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // Find employee by ID
    public EmployeeJPA findEmployeeById(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        
        try {
        	EmployeeJPA employee = em.find(EmployeeJPA.class, id);
            if (employee != null) {
                System.out.println("✓ Found employee: " + employee);
            } else {
                System.out.println("✗ No employee found with ID: " + id);
            }
            return employee;
        } catch (Exception e) {
            System.err.println("✗ Error finding employee by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    // Get all employees using JPQL
    public List<EmployeeJPA> getAllEmployees() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            String jpql = "SELECT e FROM Employee e ORDER BY e.id";
            TypedQuery<EmployeeJPA> query = em.createQuery(jpql, EmployeeJPA.class);
            List<EmployeeJPA> employees = query.getResultList();
            System.out.println("✓ Found " + employees.size() + " employees");
            return employees;
        } catch (Exception e) {
            System.err.println("✗ Error getting all employees: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    // Find employees by department using JPQL
    public List<EmployeeJPA> findEmployeesByDepartment(String department) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            String jpql = "SELECT e FROM Employee e WHERE e.department = :dept ORDER BY e.name";
            TypedQuery<EmployeeJPA> query = em.createQuery(jpql, EmployeeJPA.class);
            query.setParameter("dept", department);
            List<EmployeeJPA> employees = query.getResultList();
            System.out.println("✓ Found " + employees.size() + " employees in " + department + " department");
            return employees;
        } catch (Exception e) {
            System.err.println("✗ Error finding employees by department: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    // Update employee
    public boolean updateEmployee(EmployeeJPA employee) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = null;
        
        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(employee);
            transaction.commit();
            System.out.println("✓ Employee updated successfully: " + employee);
            return true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("✗ Error updating employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // Delete employee by ID
    public boolean deleteEmployee(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = null;
        
        try {
            transaction = em.getTransaction();
            transaction.begin();
            EmployeeJPA employee = em.find(EmployeeJPA.class, id);
            if (employee != null) {
                em.remove(employee);
                transaction.commit();
                System.out.println("✓ Employee deleted successfully: " + employee);
                return true;
            } else {
                System.out.println("✗ No employee found with ID: " + id);
                return false;
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("✗ Error deleting employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // Show sample data - get a random employee
    public EmployeeJPA showSampleData() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            // Get count using JPQL
            String jpql = "SELECT COUNT(e) FROM Employee e";
            Long count = em.createQuery(jpql, Long.class).getSingleResult();
            
            if (count == 0) {
                System.out.println("✗ No employees in database");
                return null;
            }

            // Generate random offset
            Random random = new Random();
            int randomOffset = random.nextInt(count.intValue());
            
            // Get random employee using offset
            String selectJpql = "SELECT e FROM Employee e ORDER BY e.id";
            TypedQuery<EmployeeJPA> query = em.createQuery(selectJpql, EmployeeJPA.class);
            query.setFirstResult(randomOffset);
            query.setMaxResults(1);
            
            EmployeeJPA employee = query.getSingleResult();
            System.out.println("✓ Random sample employee: " + employee);
            
            return employee;
        } catch (Exception e) {
            System.err.println("✗ Error getting sample data: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    // Get employees with salary in specified range
    public List<EmployeeJPA> findEmployeesBySalaryRange(double minSalary, double maxSalary) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            String jpql = "SELECT e FROM Employee e WHERE e.salary BETWEEN :minSal AND :maxSal ORDER BY e.salary DESC";
            TypedQuery<EmployeeJPA> query = em.createQuery(jpql, EmployeeJPA.class);
            query.setParameter("minSal", minSalary);
            query.setParameter("maxSal", maxSalary);
            List<EmployeeJPA> employees = query.getResultList();
            System.out.println("✓ Found " + employees.size() + " employees with salary between $" 
                             + minSalary + " and $" + maxSalary);
            return employees;
        } catch (Exception e) {
            System.err.println("✗ Error finding employees by salary range: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    // Get department-wise employee count
    public List<Object[]> getDepartmentWiseCount() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            String jpql = "SELECT e.department, COUNT(e) FROM Employee e GROUP BY e.department ORDER BY COUNT(e) DESC";
            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            List<Object[]> results = query.getResultList();
            
            System.out.println("✓ Department-wise employee count:");
            for (Object[] result : results) {
                System.out.println("  " + result[0] + ": " + result[1] + " employees");
            }
            
            return results;
        } catch (Exception e) {
            System.err.println("✗ Error getting department-wise count: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    // Find employees using Criteria API (Type-safe queries)
    public List<EmployeeJPA> findEmployeesUsingCriteria(String department, Double minSalary) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EmployeeJPA> cq = cb.createQuery(EmployeeJPA.class);
            Root<EmployeeJPA> employee = cq.from(EmployeeJPA.class);
            
            // Build predicates dynamically
            Predicate predicate = cb.conjunction();
            
            if (department != null && !department.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(employee.get("department"), department));
            }
            
            if (minSalary != null && minSalary > 0) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(employee.get("salary"), minSalary));
            }
            
            cq.select(employee).where(predicate).orderBy(cb.asc(employee.get("name")));
            
            List<EmployeeJPA> employees = em.createQuery(cq).getResultList();
            System.out.println("✓ Found " + employees.size() + " employees using Criteria API");
            return employees;
        } catch (Exception e) {
            System.err.println("✗ Error finding employees using Criteria API: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    // Get average salary by department using Criteria API
    public List<Object[]> getAverageSalaryByDepartment() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<EmployeeJPA> employee = cq.from(EmployeeJPA.class);
            
            cq.multiselect(
                employee.get("department"),
                cb.avg(employee.get("salary")),
                cb.count(employee)
            ).groupBy(employee.get("department"))
             .orderBy(cb.desc(cb.avg(employee.get("salary"))));
            
            List<Object[]> results = em.createQuery(cq).getResultList();
            
            System.out.println("✓ Average salary by department:");
            for (Object[] result : results) {
                System.out.printf("  %s: $%.2f (Count: %d)%n", result[0], result[1], result[2]);
            }
            
            return results;
        } catch (Exception e) {
            System.err.println("✗ Error getting average salary by department: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    // Initialize sample data
    public void initializeSampleData() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = null;
        
        try {
            // Check if data already exists
            String jpql = "SELECT COUNT(e) FROM Employee e";
            Long count = em.createQuery(jpql, Long.class).getSingleResult();
            
            if (count > 0) {
                System.out.println("ℹ Sample data already exists (" + count + " employees)");
                return;
            }

            transaction = em.getTransaction();
            transaction.begin();
            
            // Create sample employees
            EmployeeJPA[] employees = {
                new EmployeeJPA("John Doe", "john.doe@company.com", "Engineering", 75000.00),
                new EmployeeJPA("Jane Smith", "jane.smith@company.com", "Marketing", 65000.00),
                new EmployeeJPA("Mike Johnson", "mike.johnson@company.com", "Engineering", 80000.00),
                new EmployeeJPA("Sarah Williams", "sarah.williams@company.com", "HR", 55000.00),
                new EmployeeJPA("David Brown", "david.brown@company.com", "Finance", 70000.00),
                new EmployeeJPA("Lisa Davis", "lisa.davis@company.com", "Engineering", 78000.00),
                new EmployeeJPA("Tom Wilson", "tom.wilson@company.com", "Marketing", 62000.00),
                new EmployeeJPA("Emma Garcia", "emma.garcia@company.com", "HR", 58000.00)
            };

            for (EmployeeJPA emp : employees) {
                em.persist(emp);
            }

            transaction.commit();
            System.out.println("✓ Sample data initialized: " + employees.length + " employees added");
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("✗ Error initializing sample data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}