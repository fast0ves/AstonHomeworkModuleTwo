package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto findUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(this::convertToResponseDto).orElse(null);
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (!userRequestDto.getName().isBlank() &&
            !userRequestDto.getEmail().isBlank() &&
            userRequestDto.getAge() > 0) {

            LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            User newUser = new User(
                    userRequestDto.getName(),
                    userRequestDto.getEmail(),
                    userRequestDto.getAge(),
                    createdAt
            );

            User savedUser = userRepository.save(newUser);

            return convertToResponseDto(savedUser);
        } else {
            throw new IllegalArgumentException("Неверно введены данные");
        }
    }

    public UserResponseDto updateUser(int id, UserRequestDto userRequestDto) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userRequestDto.getName());
            user.setEmail(userRequestDto.getEmail());
            user.setAge(userRequestDto.getAge());

            User updatedUser = userRepository.save(user);

            return convertToResponseDto(updatedUser);
        }

        return null;
    }

    public boolean deleteUser(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);

            return true;
        }

        return false;
    }

    private UserResponseDto convertToResponseDto(User user) {

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}