package service;

import org.example.dao.UserDaoImpl;
import org.example.entity.User;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceMockTest {

    @Mock
    private UserDaoImpl userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void findUser_withExistId() {
        User expectedUser = new User("cece", "cece@mail.ru", 25,
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        when(userDao.findById(expectedUser.getId())).thenReturn(expectedUser);

        User actualUser = userService.findUser(expectedUser.getId());

        assertEquals(expectedUser, actualUser);
        verify(userDao).findById(expectedUser.getId());
    }

    @Test
    void findUser_withNonExist() {
        int userId = 999;
        when(userDao.findById(userId)).thenReturn(null);

        User actualUser = userService.findUser(userId);

        assertNull(actualUser);
        verify(userDao).findById(userId);
    }

    @Test
    void createUser_withCorrectData() {
        String name = "cece";
        String email = "cecece@mail.ru";
        int age = 25;

        userService.createUser(name, email, age);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).create(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals(name, capturedUser.getName());
        assertEquals(email, capturedUser.getEmail());
        assertEquals(age, capturedUser.getAge());
        assertNotNull(capturedUser.getCreatedAt());
        assertEquals(0, capturedUser.getCreatedAt().getSecond());
        assertEquals(0, capturedUser.getCreatedAt().getNano());
    }

    @Test
    void createUser_withBlankName() {
        String name = "";
        String email = "cecece@mail.ru";
        int age = 25;

        userService.createUser(name, email, age);

        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void createUser_withBlankEmail() {
        String email = "";
        String name = "cece";
        int age = 25;

        userService.createUser(name, email, age);

        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void createUser_withNegativeAge() {
        String name = "cece";
        String email = "cecece@mail.ru";
        int age = -5;

        userService.createUser(name, email, age);

        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void createUser_withZeroAge() {
        String name = "cece";
        String email = "cecece@mail.ru";
        int age = 0;

        userService.createUser(name, email, age);

        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void createUser_withSpaceName() {
        String name = "   ";
        String email = "cece@test.com";
        int age = 25;

        userService.createUser(name, email, age);

        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void createUser_withSpaceEmail() {
        String name = "cece";
        String email = "   ";
        int age = 25;

        userService.createUser(name, email, age);

        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void updateUser_withCorrectUser() {
        User user = new User("cece", "cece@mail.ru", 25,
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        userService.updateUser(user);

        verify(userDao).update(user);
    }

    @Test
    void deleteUser_withCorrectUser() {
        User user = new User("cece", "cece@mail.ru", 25,
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        userService.deleteUser(user);

        verify(userDao).delete(user);
    }
}