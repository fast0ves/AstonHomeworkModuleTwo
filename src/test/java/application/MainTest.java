package application;

import org.example.application.Main;
import org.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }


    @Test
    void checkNumber_correctNumber() {
        assertTrue(Main.checkNumber("123"));
        assertTrue(Main.checkNumber("0"));
        assertTrue(Main.checkNumber("-456"));
    }

    @Test
    void checkNumber_incorrectNumber() {
        assertFalse(Main.checkNumber("abc"));
        assertFalse(Main.checkNumber("123abc"));
        assertFalse(Main.checkNumber(""));
    }

    @Test
    void checkNumber_null() {
        assertFalse(Main.checkNumber(null));
    }


    @Test
    void createUserFromInputData_incorrectAge() {
        String input = "veve, veve@gmail.com, abc";
        Main.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));

        assertDoesNotThrow(Main::createUserFromInputData);

        assertTrue(outContent.toString().contains("Возраст должен быть числом!"));
    }

    @Test
    void createUserFromInputData_withoutAge() {
        String input = "fefe, fege@mail.ru";
        Main.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));

        assertDoesNotThrow(Main::createUserFromInputData);
    }

    @Test
    void searchUserById_incorrectId() {
        String input = "wvve";
        Main.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));

        User result = Main.searchUserById();

        assertNull(result);
        assertTrue(outContent.toString().contains("Некорректный id пользователя"));
    }

    @Test
    void testMenu() {
        Main.menu();
        String output = outContent.toString();
        assertTrue(output.contains("Добавить пользователя"));
        assertTrue(output.contains("Найти пользователя"));
        assertTrue(output.contains("Обновить пользователя"));
        assertTrue(output.contains("Удалить пользователя"));
        assertTrue(output.contains("ВЫХОД"));
    }

    @Test
    void mainTest() {
        String input = "1\n\n" +
                       "2\n\n" +
                       "3\n\n" +
                       "4\n\n" +
                       "0\n";

        Main.setScanner(new Scanner(new ByteArrayInputStream(input.getBytes())));

        Thread thread = new Thread(() -> {
            Main.main(new String[]{});
        });
        thread.start();

        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String output = outContent.toString();
        assertTrue(output.contains("Добавить пользователя"));
        assertTrue(output.contains("Найти пользователя"));
        assertTrue(output.contains("Обновить пользователя"));
        assertTrue(output.contains("Удалить пользователя"));
        assertTrue(output.contains("ВЫХОД"));
    }


}