package org.example.repository;

import org.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findById_ExistingUser_ReturnsUser() {
        User user = new User("John", "john@test.com", 25, LocalDateTime.now());
        User savedUser = entityManager.persistAndFlush(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getName());
        assertEquals("john@test.com", foundUser.get().getEmail());
        assertEquals(25, foundUser.get().getAge());
    }

    @Test
    void findById_NonExistingUser_ReturnsEmpty() {
        Optional<User> foundUser = userRepository.findById(999);
        assertFalse(foundUser.isPresent());
    }

    @Test
    void save_ValidUser_SavesUser() {
        User user = new User("John", "john@test.com", 25, LocalDateTime.now());

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("John", savedUser.getName());
        assertEquals("john@test.com", savedUser.getEmail());
        assertEquals(25, savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void delete_ExistingUser_DeletesUser() {
        User user = new User("John", "john@test.com", 25, LocalDateTime.now());
        User savedUser = entityManager.persistAndFlush(user);

        userRepository.deleteById(savedUser.getId());

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }
}