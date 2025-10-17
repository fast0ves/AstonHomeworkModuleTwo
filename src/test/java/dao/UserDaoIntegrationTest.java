package dao;

import com.shorokhov.dao.UserDao;
import com.shorokhov.entity.User;
import com.shorokhov.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Testcontainers
class UserDaoIntegrationTest {

    private UserDao userDao;
    private static SessionFactory defaultSessionFactory;

    private static void setSessionFactory(SessionFactory sessionFactory) {
        try {
            Field sessionFactoryField = HibernateUtil.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(null, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        defaultSessionFactory = HibernateUtil.getSessionFactory();

        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.format_sql", "true");

        configuration.addAnnotatedClass(com.shorokhov.entity.User.class);

        SessionFactory testSessionFactory = configuration.buildSessionFactory();
        setSessionFactory(testSessionFactory);
    }

    @AfterAll
    static void afterAll() {
        setSessionFactory(defaultSessionFactory);
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDao();
    }

    @Test
    void createUser_WithValidData_ShouldSaveUser() {
        User user = new User("Alice Smith", 28);
        User savedUser = userDao.create(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("Alice Smith", savedUser.getName());
        assertEquals(28, savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void createUser_WithNullUser_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> userDao.create(null));
    }

    @Test
    void findById_WithExistingUserId_ShouldReturnUser() {
        User user = userDao.create(new User("Bob Johnson", 35));
        Optional<User> foundUser = userDao.findById(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("Bob Johnson", foundUser.get().getName());
        assertEquals(35, foundUser.get().getAge());
    }

    @Test
    void findById_WithNonExistingUserId_ShouldReturnEmpty() {
        Optional<User> foundUser = userDao.findById(999L);
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findById_WithNullId_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> userDao.findById(null));
    }

    @Test
    void updateUser_UpdateAge_ShouldUpdateSuccessfully() {
        User user = userDao.create(new User("David Wilson", 30));
        user.setAge(45);
        User updatedUser = userDao.update(user);

        assertEquals(45, updatedUser.getAge());
        assertEquals("David Wilson", updatedUser.getName());

        Optional<User> foundUser = userDao.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(45, foundUser.get().getAge());
    }

    @Test
    void updateUser_UpdateName_ShouldUpdateSuccessfully() {
        User user = userDao.create(new User("Original Name", 25));
        user.setName("Updated Name");
        User updatedUser = userDao.update(user);

        assertEquals("Updated Name", updatedUser.getName());

        Optional<User> foundUser = userDao.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Updated Name", foundUser.get().getName());
    }

    @Test
    void updateUser_WithNullUser_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> userDao.update(null));
    }

    @Test
    void deleteUser_WithExistingUserId_ShouldDeleteSuccessfully() {
        User user = userDao.create(new User("User to delete", 25));

        Optional<User> foundBeforeDelete = userDao.findById(user.getId());
        assertTrue(foundBeforeDelete.isPresent());

        userDao.delete(user.getId());

        Optional<User> foundAfterDelete = userDao.findById(user.getId());
        assertFalse(foundAfterDelete.isPresent());
    }

    @Test
    void deleteUser_WithNonExistingUserId_ShouldNotThrowException() {
        assertDoesNotThrow(() -> userDao.delete(999L));
    }

    @Test
    void findAll_WithMultipleUsers_ShouldReturnAllUsers() {
        userDao.create(new User("Grace Lee", 29));
        userDao.create(new User("Henry Ford", 55));
        userDao.create(new User("Ivy Chen", 31));

        List<User> users = userDao.findAll();
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 3);
    }

    @Test
    void findAll_WithEmptyDatabase_ShouldReturnEmptyList() {
        List<User> existingUsers = userDao.findAll();
        for (User user : existingUsers) {
            userDao.delete(user.getId());
        }

        List<User> users = userDao.findAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void constructor_ShouldInitializeSession() {
        assertDoesNotThrow(() -> new UserDao());
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        User user = new User("Test User", 25);
        user.setId(1L);

        String toStringResult = user.toString();

        assertTrue(toStringResult.contains("User{id=1"));
        assertTrue(toStringResult.contains("name='Test User'"));
        assertTrue(toStringResult.contains("age=25"));
        assertTrue(toStringResult.contains("createdAt="));
    }

    @Test
    void create_ShouldThrowException_WhenUserIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userDao.create(null));
        assertTrue(exception.getMessage().contains("Failed to save user"));
    }

    @Test
    void update_ShouldThrowException_WhenUserIsNull() {
        assertThrows(RuntimeException.class, () -> userDao.update(null));
    }

    @Test
    void findById_ShouldThrowException_WhenIdIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userDao.findById(null));
        assertTrue(exception.getMessage().contains("Failed to find user by ID"));
    }

    @Test
    void update_ShouldThrowException_WhenUserNotExists() {
        User unsavedUser = new User("Non-existent User", 99);
        unsavedUser.setId(999L);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userDao.update(unsavedUser));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Failed to update user"));

        assertNotNull(exception.getCause());
    }

    @Test
    void delete_ShouldThrowException_WhenNullId() {
        assertThrows(RuntimeException.class, () -> userDao.delete(null));
    }

    @Test
    void findAll_ShouldThrowException_WhenSessionIsClosed() throws Exception {
        UserDao userDaoWithClosedSession = new UserDao();

        Field sessionField = UserDao.class.getDeclaredField("session");
        sessionField.setAccessible(true);
        Session session = (Session) sessionField.get(userDaoWithClosedSession);
        session.close();


        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userDaoWithClosedSession.findAll());

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Failed to find all users"));
    }
}