package com.mvs.user_service.service;

import com.mvs.user_service.dto.UserDto;
import com.mvs.user_service.model.User;
import com.mvs.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(UserDto userDto) {
        log.info("registering user email = {}", userDto.getEmail());
        Optional<User> opUser = this.userRepository.findByEmail(userDto.getEmail());
        if (opUser.isPresent()) throw new RuntimeException("User already exists");

        User user = User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(User.UserRoles.USER)
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .phone(userDto.getPhone())
                .build();
        return this.userRepository.save(user);
    }
}
