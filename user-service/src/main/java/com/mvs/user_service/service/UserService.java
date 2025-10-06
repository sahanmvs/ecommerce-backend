package com.mvs.user_service.service;

import com.mvs.user_service.dto.UserDto;
import com.mvs.user_service.exception.ExType;
import com.mvs.user_service.exception.exs.BadRequestException;
import com.mvs.user_service.exception.exs.ConflictException;
import com.mvs.user_service.exception.exs.NotFoundException;
import com.mvs.user_service.exception.exs.UnauthorizedException;
import com.mvs.user_service.model.User;
import com.mvs.user_service.repository.UserRepository;
import com.mvs.user_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User registerUser(UserDto userDto) {
        log.info("registering user email = {}", userDto.getEmail());
        Optional<User> opUser = this.userRepository.findByEmail(userDto.getEmail());
        if (opUser.isPresent()) throw new ConflictException(ExType.USER_ALREADY_EXISTS, "User already exists");

        User user = User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(User.UserRoles.USER)
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .phone(userDto.getPhone())
                .status(User.UserStatus.ACTIVE)
                .build();
        return this.userRepository.save(user);
    }

    public UserDto login(UserDto userDto) {
        log.info("login user email = {}", userDto.getEmail());
        if (StringUtils.isBlank(userDto.getEmail()) || StringUtils.isBlank(userDto.getPassword())) {
            throw new BadRequestException(ExType.INVALID_CREDENTIALS, "Login failed email or password empty");
        }
        Optional<User> opUser = this.userRepository.findByEmailAndStatus(userDto.getEmail(), User.UserStatus.ACTIVE);
        if (opUser.isEmpty()) throw new NotFoundException(ExType.USER_NOT_FOUND, "User does not exist");
        if (!passwordEncoder.matches(userDto.getPassword(), opUser.get().getPassword())) {
            throw new UnauthorizedException(ExType.UNAUTHORIZED, "Invalid email or password");
        }
        String token = jwtUtil.generateToken(opUser.get());
        return UserDto.initWithToken(opUser.get(), token);
    }

    public User getUserById(String id) {
        log.info("get user by id = {}", id);
        Optional<User> opUser = this.userRepository.findById(id);
        if (opUser.isEmpty()) throw new NotFoundException(ExType.USER_NOT_FOUND, "User does not exist");
        return opUser.get();
    }

    public User getUserProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        log.info("get user profile id = {}", user.getId());
        return this.getUserById(user.getId());
    }
}
