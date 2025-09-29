package com.practise.JPA_Application;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
@NamedQueries({
    @NamedQuery(name = "Employee.findAll", 
                query = "SELECT e FROM Employee e ORDER BY e.id"),
    @NamedQuery(name = "Employee.findByDepartment", 
                query = "SELECT e FROM Employee e WHERE e.department = :department ORDER BY e.name"),
    @NamedQuery(name = "Employee.findBySalaryRange", 
                query = "SELECT e FROM Employee e WHERE e.salary BETWEEN :minSalary AND :maxSalary ORDER BY e.salary DESC"),
    @NamedQuery(name = "Employee.countByDepartment", 
                query = "SELECT e.department, COUNT(e) FROM Employee e GROUP BY e.department ORDER BY COUNT(e) DESC"),
    @NamedQuery(name = "Employee.count", 
                query = "SELECT COUNT(e) FROM Employee e")
})
public class EmployeeJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "department", nullable = false, length = 50)
    private String department;
    
    @Column(name = "salary", nullable = false, precision = 10)
    private double salary;

    // Default constructor (required by JPA)
    public EmployeeJPA() {}

    // Constructor without ID (for new employees)
    public EmployeeJPA(String name, String email, String department, double salary) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.salary = salary;
    }

    // Constructor with ID (for existing employees)
    public EmployeeJPA(int id, String name, String email, String department, double salary) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.salary = salary;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeJPA employee = (EmployeeJPA) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}