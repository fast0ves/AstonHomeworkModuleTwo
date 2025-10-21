package dao;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.entity.User;
import org.example.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoIntegrationTest {

    private UserDao userDao;
    private static SessionFactory defaultSessionFactory = SessionFactoryUtil.getSessionFactory();
    private static SessionFactory testSessionFactory;

    private static void setSessionFactory(SessionFactory sessionFactory) {
        try {
            Field sessionFactoryField = SessionFactoryUtil.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(null, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("testDB")
            .withUsername("testName")
            .withPassword("testPass");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true");

        configuration.addAnnotatedClass(User.class);

        testSessionFactory = configuration.buildSessionFactory();

        setSessionFactory(testSessionFactory);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        setSessionFactory(defaultSessionFactory);
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl();
    }

    @Test
    void create_correctUser() {
        User actualUser = new User("Nik", "mail.ru", 10, LocalDateTime.now());
        User user = userDao.create(actualUser);
        assertEquals(user, actualUser);
    }

    @Test
    void create_nullUser() {
        assertThrows(IllegalArgumentException.class, () -> userDao.create(null));
    }

    @Test
    void find_existUser() {
        User actualUser = userDao.create(new User("Nik", "mail.ru", 10,
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));
        User expectedUser = userDao.findById(actualUser.getId());
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void find_notExistUser() {
        userDao.create(new User("Nik", "mail.ru", 10, LocalDateTime.now()));
        User actualUser = userDao.findById(99);
        assertNull(actualUser);
    }

    @Test
    void find_incorrectId() {
        assertThrows(NumberFormatException.class, () -> userDao.findById(Integer.parseInt("fcwefw")));
    }

    @Test
    void find_nullId() {
        assertThrows(NullPointerException.class, () -> userDao.findById((Integer) null));
    }

    @Test
    void update_updateAgeUser() {
        User user = userDao.create(new User("Nik", "mail.ru", 10, LocalDateTime.now()));
        User actualUser = userDao.findById(user.getId());
        actualUser.setAge(22);
        assertEquals(userDao.update(actualUser), actualUser);
    }

    @Test
    void update_updateAllUser() {
        User user = userDao.create(new User("Nik", "mail.ru", 10, LocalDateTime.now()));
        User actualUser = userDao.findById(user.getId());
        actualUser.setAge(22);
        actualUser.setName("lolol");
        actualUser.setName("errt@mail.ru");
        assertEquals(userDao.update(actualUser), actualUser);
    }

    @Test
    void update_nullUser() {
        assertThrows(IllegalArgumentException.class, () -> userDao.update(null));
    }

    @Test
    void delete_existUser() {
        User user = userDao.create(new User("Nik", "mail.ru", 10, LocalDateTime.now()));
        userDao.delete(user);
        Assertions.assertNull(userDao.findById(user.getId()));
    }

    @Test
    void delete_notExistUser() {
        assertThrows(IllegalArgumentException.class, () -> userDao.delete(null));
    }

    @Test
    void delete_connectionLost() {
        User user = userDao.create(new User("Nik", "mail.ru", 10, LocalDateTime.now()));
        postgres.stop();
        assertThrows(RuntimeException.class, () -> userDao.delete(user));
        beforeAll();
    }

    @Test
    void create_connectionLost() {
        postgres.stop();
        assertThrows(RuntimeException.class, () -> userDao.create(new User("Nik", "mail.ru", 10, LocalDateTime.now())));
        beforeAll();
    }

    @Test
    void update_connectionLost() {
        User user = userDao.create(new User("Nik", "mail.ru", 10, LocalDateTime.now()));
        user.setName("aaaaaa");
        postgres.stop();
        assertThrows(RuntimeException.class, () -> userDao.update(user));
        beforeAll();
    }

    @Test
    void find_connectionLost() {
        User user = userDao.create(new User("Nik", "mail.ru", 10, LocalDateTime.now()));
        postgres.stop();
        assertThrows(RuntimeException.class, () -> userDao.findById(user.getId()));
        beforeAll();
    }
}
