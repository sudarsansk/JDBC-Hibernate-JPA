package com.practise.JPA_Application;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

	private static EntityManagerFactory entityManagerFactory;

	private static EntityManagerFactory createEntityManagerFactory() {
		Map<String, Object> properties = new HashMap<>();

		// Database connection properties
		properties.put(PersistenceUnitProperties.JDBC_DRIVER, "org.h2.Driver");
		properties.put(PersistenceUnitProperties.JDBC_URL, "jdbc:h2:./data/employeedb");
		entityManagerFactory  = Persistence.createEntityManagerFactory("employeePU", properties);
		return entityManagerFactory;
	}

	public static EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
			return createEntityManagerFactory();
		}
		return entityManagerFactory;
	}
	
	public static void close() {
		if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
			System.out.println("✓ EntityManagerFactory closed successfully.");
		}
	}
}
