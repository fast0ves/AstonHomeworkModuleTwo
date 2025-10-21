package entity;

import org.example.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
public class UserEntityTest {

    @Test
    void constructor_emptyConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void constructor_fullConstructor() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        assertEquals(new User("dcece", "23dew@mail.ru", 34, user.getCreatedAt()), user);
    }

    @Test
    void getter_getId() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        assertEquals(0, user.getId());
        assertNotEquals(32, user.getId());
    }

    @Test
    void getter_getName() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        assertEquals("dcece", user.getName());
        assertNotEquals("fefe", user.getName());
    }

    @Test
    void getter_getEmail() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        assertEquals("23dew@mail.ru", user.getEmail());
        assertNotEquals("wgvf@mail.ru", user.getName());
    }

    @Test
    void getter_getAge() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        assertEquals(34, user.getAge());
        assertNotEquals(55, user.getAge());
    }

    @Test
    void getter_getCreatedAt() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        assertEquals(user.getCreatedAt(), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    @Test
    void setter_setName() {
        String newName = "Nikita";
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        user.setName(newName);
        assertEquals(newName, user.getName());
        assertNotEquals("dcece", user.getName());
    }

    @Test
    void setter_setEmail() {
        String newEmail = "pol@mail.ru";
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        user.setEmail(newEmail);
        assertEquals(newEmail, user.getEmail());
        assertNotEquals("23dew@mail.ru", user.getEmail());
    }

    @Test
    void setter_setAge() {
        int newAge = 222;
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        user.setAge(newAge);
        assertEquals(newAge, user.getAge());
        assertNotEquals(34, user.getAge());
    }

    @Test
    void setter_setCreatedAt() {
        LocalDateTime newCreatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        user.setCreatedAt(newCreatedAt);
        assertEquals(newCreatedAt, user.getCreatedAt());
    }

    @Test
    void toString_test() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        String userToString = "Пользователь: " + '\n' +
                              "id: " + user.getId() + '\n' +
                              "Имя: " + user.getName() + '\n' +
                              "email: " + user.getEmail() + '\n' +
                              "Возраст: " + user.getAge() + '\n' +
                              "Создан: " + user.getCreatedAt();
        assertEquals(userToString, user.toString());
    }

    @Test
    void equals_nullUser() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        assertFalse(user.equals(null));
    }

    @Test
    void equals_differentClass() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        String string = "diffirent";
        assertFalse(user.equals(string));
    }

    @Test
    void equals_identityUser() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        User user1 = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        assertTrue(user.equals(user1));
    }

    @Test
    void equals_differentName() {
        User user = new User("nik", "23dew@mail.ru", 34, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        User user1 = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        assertFalse(user.equals(user1));
    }

    @Test
    void equals_differentEmail() {
        User user = new User("nik", "23@mail.ru", 34, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        User user1 = new User("nik", "23dew@mail.ru", 34, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        assertFalse(user.equals(user1));
    }

    @Test
    void equals_differentAge() {
        User user = new User("nik", "23dew@mail.ru", 34, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        User user1 = new User("nik", "23dew@mail.ru", 54, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        assertFalse(user.equals(user1));
    }

    @Test
    void equals_differentDate() {
        User user = new User("nik", "23dew@mail.ru", 34, LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
        User user1 = new User("nik", "23dew@mail.ru", 34, LocalDateTime.now());
        assertFalse(user.equals(user1));
    }

    @Test
    void hashCode_notEquals() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        User user1 = new User("435fg", "gregr@mail.ru", 34, LocalDateTime.now());
        assertNotEquals(user.hashCode(), user1.hashCode());
    }

    @Test
    void hashCode_equalsHash() {
        User user = new User("dcece", "23dew@mail.ru", 34, LocalDateTime.now());
        assertEquals(Objects.hash(user.getId(), user.getName(), user.getEmail(), user.getAge(),
                user.getCreatedAt()), user.hashCode());
    }
}