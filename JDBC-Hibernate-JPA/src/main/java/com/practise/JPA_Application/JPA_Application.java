package com.practise.JPA_Application;

import java.util.Scanner;

public class JPA_Application {

	public static JPAService jpaService;
	public static Scanner scanner;
	public static JPAUtil jpaUtil;
	
	public static void main(String[] args) {
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

	    // Initialize sample data
	    //jpaService.initializeSampleData();

	    showMenu();
	}

	private static void showMenu() {
		
	}
	
	
}
