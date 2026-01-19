package com.usermanagement.usermanagementbe.controller;

import com.usermanagement.usermanagementbe.dto.UserRequest;
import com.usermanagement.usermanagementbe.dto.UserResponse;
import com.usermanagement.usermanagementbe.entity.User;
import com.usermanagement.usermanagementbe.exception.BadRequestException;
import com.usermanagement.usermanagementbe.exception.ResourceNotFoundException;
import com.usermanagement.usermanagementbe.repository.UserRepository;
import com.usermanagement.usermanagementbe.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        if (userRequest.getPassword() == null || userRequest.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        UserResponse response = userService.createUser(userRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getUsername().equals(userRequest.getUsername()) &&
                userRepository.existsByUsername(userRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (!user.getEmail().equals(userRequest.getEmail()) &&
                userRepository.existsByEmail(userRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        UserResponse response = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
