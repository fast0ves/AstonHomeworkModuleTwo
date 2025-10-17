package service;

import com.shorokhov.dao.UserDao;
import com.shorokhov.entity.User;
import com.shorokhov.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;
    @Mock
    private UserDao userDao;
    private User testUser;

    @BeforeEach
    void setUp() {

        userService = new UserService(userDao);

        testUser = new User("John Doe", 25);
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void constructor_ShouldInitializeUserDao() {
        try (MockedConstruction<UserDao> mockedUserDao = mockConstruction(UserDao.class)) {

            UserService userService = new UserService();

            assertNotNull(userService);
            assertEquals(1, mockedUserDao.constructed().size());
        }
    }

    @Test
    void createUser_WithValidDataShouldReturnCreatedUser() {
        String name = "John Doe";
        Integer age = 25;

        User savedUser = new User(name, age);
        savedUser.setId(1L);

        when(userDao.create(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(name, age);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(name, result.getName());
        assertEquals(age, result.getAge());
        assertNotNull(result.getCreatedAt());

        verify(userDao, times(1)).create(any(User.class));
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() {
        Long userId = 1L;

        when(userDao.findById(userId)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());

        verify(userDao, times(1)).findById(userId);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldReturnEmpty() {
        Long userId = 999L;

        when(userDao.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertFalse(result.isPresent());

        verify(userDao, times(1)).findById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        List<User> users = List.of(testUser);
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userDao, times(1)).findAll();
    }

    @Test
    void updateUser_WithValidUserShouldReturnUpdatedUser() {
        User userToUpdate = new User("Updated Name", 30);
        userToUpdate.setId(1L);

        User updatedUser = new User("Updated Name", 30);
        updatedUser.setId(1L);

        when(userDao.update(userToUpdate)).thenReturn(updatedUser);

        User result = userService.updateUser(userToUpdate);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals(30, result.getAge());
        verify(userDao, times(1)).update(userToUpdate);
    }

    @Test
    void deleteUser_WithExistingIdShouldCallDaoDelete() {
        Long userId = 1L;
        doNothing().when(userDao).delete(userId);

        userService.deleteUser(userId);

        verify(userDao, times(1)).delete(userId);
    }
}
