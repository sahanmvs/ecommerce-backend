package com.mvs.user_service.controller;

import com.mvs.user_service.dto.LoginRequest;
import com.mvs.user_service.dto.UserDeleteRequest;
import com.mvs.user_service.dto.UserDto;
import com.mvs.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Validated(UserDto.Registration.class) UserDto userDto) {
        return ResponseEntity.ok(UserDto.init(userService.registerUser(userDto)));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserProfile(@Parameter(hidden = true) @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(UserDto.init(userService.getUserProfile(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateUser(
            @RequestBody @Validated(UserDto.Update.class) UserDto userDto,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(UserDto.init(userService.updateUserProfile(userDto, userId)));
    }

    @DeleteMapping("/me")
    public ResponseEntity<UserDto> deleteUser(
            @RequestBody UserDeleteRequest request,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(UserDto.init(userService.deleteUserProfile(request, userId)));
    }
}
