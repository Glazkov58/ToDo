package com.example.demoToDo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoToDo.Utils.JWTUtil;
import com.example.demoToDo.model.LoginDto;
import com.example.demoToDo.model.User;
import com.example.demoToDo.repository.UserRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    private UserRepository userRepository;
    private JWTUtil util;

    public ApiAuthController(UserRepository userRepository, JWTUtil util){
        this.userRepository = userRepository;
        this.util = util;
    }

    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto logindto){
        var userOpt = userRepository.findByEmail(logindto.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String pass = userOpt.get().getPassword();
        if (logindto.getPassword().equals(pass)){
            // авторизация пройдена
            // создать JWT
            String jwt = util.generateToken(userOpt.get().getEmail());
            return ResponseEntity.ok(jwt);
        }
        return ResponseEntity.badRequest().build(); 
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email уже используется");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName()); // временно
        user.setPassword(dto.getPassword());
        userRepository.save(user);
        return ResponseEntity.ok("Регистрация успешна");
        }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
    User currentUser = (User) request.getAttribute("currentUser");
    if (currentUser == null) {
        return ResponseEntity.status(403).build();
    }
    LoginDto resp = new LoginDto();
    resp.setName(currentUser.getName());
    resp.setEmail(currentUser.getEmail());
    return ResponseEntity.ok(resp);    
    }
}
