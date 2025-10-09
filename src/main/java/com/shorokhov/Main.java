package com.shorokhov;

import com.shorokhov.entity.User;
import com.shorokhov.service.UserService;
import com.shorokhov.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final UserService userService = new UserService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("Starting User Service application");

        try {
            while (true) {
                showMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        createUser();
                        break;
                    case "2":
                        getUserById();
                        break;
                    case "3":
                        getAllUsers();
                        break;
                    case "4":
                        updateUser();
                        break;
                    case "5":
                        deleteUser();
                        break;
                    case "0":
                        shutdown();
                        return;
                    default:
                        System.out.println("Invalid command. Please try again");
                }

                System.out.println("\nPress Enter for continue");
                scanner.nextLine();
            }
        } catch (Exception e) {
            logger.error("Critical error in application", e);
            System.out.println("Happened critical error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void showMenu() {
        System.out.println("\n===User Service===");
        System.out.println("1. Create User");
        System.out.println("2. Find User by ID");
        System.out.println("3. Show all Users");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");
        System.out.println("0. Exit");
        System.out.print("Choice action: ");
    }

    private static void createUser() {
        System.out.println("\n--- Creating User ---");

        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter age: ");
            String ageInput = scanner.nextLine().trim();
            Integer age = ageInput.isEmpty() ? null : Integer.parseInt(ageInput);

            if (name.isEmpty()) {
                System.out.println("Error: Name is required");
                return;
            }

            Long userId = userService.createUser(name, age);
            System.out.println("User successfully created with ID: " + userId);

        } catch (NumberFormatException e) {
            System.out.println("Error: age must be a number");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            logger.error("Error creating user", e);
        }
    }

    private static void getUserById() {
        System.out.println("\n--- Find User by ID ---");

        try {
            System.out.print("Enter User ID: ");
            String idInput = scanner.nextLine().trim();

            if (idInput.isEmpty()) {
                System.out.println("Error: ID cannot be empty");
                return;
            }

            Long id = Long.parseLong(idInput);
            Optional<User> user = userService.getUserById(id);

            if (user.isPresent()) {
                System.out.println("User found: " + user.get());
            } else {
                System.out.println("User with ID " + id + " not found");
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be number");
        } catch (Exception e) {
            System.out.println("Error when searching for a user: " + e.getMessage());
            logger.error("Error finding user by ID", e);
        }
    }

    private static void getAllUsers() {
        System.out.println("\n--- List of all users ---");

        try {
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                System.out.println("Users not found");
            } else {
                for (User user : users) {
                    System.out.println(user);
                }
                System.out.println("List of all users: " + users.size());
            }

        } catch (Exception e) {
            System.out.println("Error when getting the list of users: " + e.getMessage());
            logger.error("Error getting all users", e);
        }
    }

    private static void updateUser() {
        System.out.println("\n--- Update User ---");

        try {
            System.out.print("Enter the user ID for the update: ");
            String idInput = scanner.nextLine().trim();

            if (idInput.isEmpty()) {
                System.out.println("Error: ID cannot be empty");
                return;
            }

            Long id = Long.parseLong(idInput);
            Optional<User> userOpt = userService.getUserById(id);

            if (userOpt.isEmpty()) {
                System.out.println("User with ID " + id + " not found");
                return;
            }

            User user = userOpt.get();
            System.out.println("Current data: " + user);

            System.out.print("Enter new name User: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                user.setName(name);
            }

            System.out.print("Enter new age User: ");
            String ageInput = scanner.nextLine().trim();
            if (!ageInput.isEmpty()) {
                user.setAge(Integer.parseInt(ageInput));
            }

            userService.updateUser(user);
            System.out.println("The user has been successfully updated");

        } catch (NumberFormatException e) {
            System.out.println("Error: ID and age must be numbers");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error when updating the user: " + e.getMessage());
            logger.error("Error updating user", e);
        }
    }

    private static void deleteUser() {
        System.out.println("\n--- Delete User ---");

        try {
            System.out.print("Enter ID User for delete: ");
            String idInput = scanner.nextLine().trim();

            if (idInput.isEmpty()) {
                System.out.println("Error: ID cannot be empty");
                return;
            }

            Long id = Long.parseLong(idInput);

            Optional<User> user = userService.getUserById(id);
            if (user.isEmpty()) {
                System.out.println("User with ID " + id + " not found");
                return;
            }

            System.out.println("Are you sure you want to delete the user?: " + user.get() + "? (y/n)");
            String confirmation = scanner.nextLine().trim();

            if ("y".equalsIgnoreCase(confirmation)) {
                userService.deleteUser(id);
                System.out.println("The user has been successfully deleted");
            } else {
                System.out.println("Deletion cancelled");
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be number");
        } catch (Exception e) {
            System.out.println("Error when deleting a user: " + e.getMessage());
            logger.error("Error deleting user", e);
        }
    }

    private static void shutdown() {
        System.out.println("Shutting down the application");
        HibernateUtil.shutdown();
        logger.info("Application shutdown completed");
    }
}
