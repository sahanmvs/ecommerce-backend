package com.mvs.user_service.controller;

import com.mvs.user_service.dto.UserDto;
import com.mvs.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Validated UserDto userDto) {
        return ResponseEntity.ok(UserDto.init(userService.registerUser(userDto)));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.login(userDto));
    }

    @GetMapping
    public ResponseEntity<UserDto> getUserProfile() {
        return ResponseEntity.ok(UserDto.init(userService.getUserProfile()));
    }
}
