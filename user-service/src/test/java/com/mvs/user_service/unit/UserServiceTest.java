package com.mvs.user_service.unit;

import com.mvs.user_service.dto.LoginRequest;
import com.mvs.user_service.dto.UserDeleteRequest;
import com.mvs.user_service.dto.UserDto;
import com.mvs.user_service.exception.exs.ConflictException;
import com.mvs.user_service.exception.exs.NotFoundException;
import com.mvs.user_service.exception.exs.UnauthorizedException;
import com.mvs.user_service.model.User;
import com.mvs.user_service.repository.UserRepository;
import com.mvs.user_service.security.JwtUtil;
import com.mvs.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtil jwtUtil;
    @InjectMocks
    UserService userService;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Test
    void shouldRegisterUserSuccessfully() {
        UserDto dto = createUserDto();

        Mockito.when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashed");

        userService.registerUser(dto);

        Mockito.verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo("hashed");
        assertThat(savedUser.getRole()).isEqualTo(User.UserRoles.USER);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserDto dto = createUserDto();
        User existingUser = User.builder()
                .email(dto.getEmail())
                .build();
        Mockito.when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.registerUser(dto)).isInstanceOf(ConflictException.class).hasMessageContaining("User already exists");

        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("hashed");

        Mockito.when(userRepository.findByEmailAndStatus(user.getEmail(), User.UserStatus.ACTIVE)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);
        Mockito.when(jwtUtil.generateToken(Mockito.any())).thenReturn("token");

        LoginRequest dto = new LoginRequest();
        dto.setEmail(user.getEmail());
        dto.setPassword("raw");

        UserDto result = userService.login(dto);

        assertThat(result.getToken()).isEqualTo("token");

        Mockito.verify(passwordEncoder).matches("raw", "hashed");
        Mockito.verify(jwtUtil).generateToken(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailNotExists() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("hashed");

        Mockito.when(userRepository.findByEmailAndStatus(user.getEmail(), User.UserStatus.ACTIVE)).thenReturn(Optional.empty());
        LoginRequest dto = new LoginRequest();
        dto.setEmail(user.getEmail());
        dto.setPassword("raw");

        assertThatThrownBy(() -> userService.login(dto)).isInstanceOf(NotFoundException.class).hasMessageContaining("User does not exist");

        Mockito.verify(jwtUtil,  Mockito.never()).generateToken(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenPasswordNotMatch() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("hashed");

        Mockito.when(userRepository.findByEmailAndStatus(user.getEmail(), User.UserStatus.ACTIVE)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("password", "hashed")).thenReturn(false);

        LoginRequest dto = new LoginRequest();
        dto.setEmail(user.getEmail());
        dto.setPassword("password");

        assertThatThrownBy(() -> userService.login(dto)).isInstanceOf(UnauthorizedException.class).hasMessageContaining("Invalid email or password");

        Mockito.verify(jwtUtil,  Mockito.never()).generateToken(Mockito.any());
    }

    @Test
    void shouldUpdateProfileSuccessfully() {
        String userId = "u0001";
        User user = new User();
        user.setId(userId);
        user.setEmail("john@gmail.com");
        user.setPassword("hashed");

        UserDto dto = createUserDto();
        dto.setPassword(null);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.updateUserProfile(dto, userId);

        Mockito.verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertThat(saved.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingEmail() {
        String userId = "u0001";
        User user = new User();
        user.setId(userId);
        user.setEmail("john@gmail.com");
        user.setPassword("hashed");

        UserDto dto = createUserDto();
        dto.setPassword(null);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));
        assertThatThrownBy(() -> userService.updateUserProfile(dto, userId)).isInstanceOf(ConflictException.class).hasMessageContaining("User already exists");
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldDeleteProfileSuccessfully() {
        String userId = "u0001";
        User user = new User();
        user.setId(userId);
        user.setEmail("john@gmail.com");
        user.setPassword("hashed");

        UserDeleteRequest dto = new UserDeleteRequest();
        dto.setPassword("raw");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);

        userService.deleteUserProfile(dto, userId);

        Mockito.verify(passwordEncoder).matches("raw", "hashed");
        Mockito.verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertThat(saved.getStatus().equals(User.UserStatus.DEACTIVATED)).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenDeletingUserProfileWithIncorrectPassword() {
        String userId = "u0001";
        User user = new User();
        user.setId(userId);
        user.setEmail("john@gmail.com");
        user.setPassword("hashed");

        UserDeleteRequest dto = new UserDeleteRequest();
        dto.setPassword("raw");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("raw", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUserProfile(dto, userId)).isInstanceOf(UnauthorizedException.class).hasMessageContaining("Invalid password");
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    public UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setEmail("test@gmail.com");
        dto.setPassword("password");
        dto.setFirstName("firstName");
        dto.setLastName("lastName");
        dto.setPhone("917775671");
        return dto;
    }
}
