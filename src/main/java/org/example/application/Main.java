package org.example.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.UserDaoImpl;
import org.example.entity.User;
import org.example.service.UserService;

import java.util.Scanner;


public class Main {

    private static final Logger logger = LogManager.getLogger();
    private static Scanner SCANNER = new Scanner(System.in);
    private static UserService userService = new UserService(new UserDaoImpl());

    //Для тестов
    public static void setScanner(Scanner customScanner) {
        SCANNER = customScanner;
    }

    public static void menu() {
        System.out.println("""
                1.Добавить пользователя
                2.Найти пользователя
                3.Обновить пользователя
                4.Удалить пользователя
                0.ВЫХОД
                """);
    }

    public static void main(String[] args) {
        boolean runFlag = true;
        while (runFlag) {
            menu();
            if (SCANNER.hasNextInt()) {
                int inputValue = SCANNER.nextInt();
                SCANNER.nextLine();
                switch (inputValue) {
                    case 1 -> createUserFromInputData();
                    case 2 -> searchUserById();
                    case 3 -> updateUserById();
                    case 4 -> deleteUserById();
                    case 0 -> runFlag = false;
                }
            } else {
                System.out.println("Введите корректный пункт меню");
                SCANNER.next();
            }
        }

    }

    public static boolean checkNumber(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void createUserFromInputData() {
        System.out.println("Введите последовательно Имя, email и возраст через запятую c пробелом");
        String input = SCANNER.nextLine();
        try {
            String[] userValues = input.split(", ");
            if (checkNumber(userValues[2])) {
                userService.createUser(userValues[0], userValues[1], Integer.parseInt(userValues[2]));
                System.out.println("Пользователь создан");
            } else {
                System.out.println("Возраст должен быть числом!");
                logger.info("Введен некорректный возраст");
            }
        } catch (IndexOutOfBoundsException e) {
            logger.error(e);
        }
    }

    public static User searchUserById() {
        User user = null;
        System.out.println("Введите id Пользователя");
        String input = SCANNER.nextLine();
        if (checkNumber(input)) {
            user = userService.findUser(Integer.parseInt(input));
            if (user != null) {
                System.out.println(user);
                return user;
            } else System.out.println("Пользователь не найден!");
        } else {
            System.out.println("Некорректный id пользователя");
            logger.info("Введен некорректный id");
        }
        return null;
    }

    public static void updateUserById() {
        User user = searchUserById();
        if (user != null) {
            System.out.println("Введите последовательно новые - Имя, email и возраст через запятую c пробелом");
            String input = SCANNER.nextLine();
            try {
                String[] newUserValues = input.split(", ");
                if (checkNumber(newUserValues[2])) {
                    user.setName(newUserValues[0]);
                    user.setEmail(newUserValues[1]);
                    user.setAge(Integer.parseInt(newUserValues[2]));
                    userService.updateUser(user);
                    System.out.println("Пользователь обновлен!");
                } else {
                    System.out.println("Возраст должен быть числом!");
                    logger.info("Введен некорректный возраст");
                }
            } catch (IndexOutOfBoundsException e) {
                logger.error(e);
            }
        }
    }

    public static void deleteUserById() {
        User user = searchUserById();
        if (user != null) {
            userService.deleteUser(user);
            System.out.println("Пользователь удален");
        }
    }
}