package com.example.demoToDo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoToDo.Utils.JWTUtil;
import com.example.demoToDo.model.LoginDto;
import com.example.demoToDo.repository.UserRepository;

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
    public ResponseEntity<?> login(@RequestBody LoginDto logindto){
        var user = userRepository.findByEmail(logindto.getEmail());
        String pass = user.get().getPassword();
        if (logindto.getPassword().equals(pass)){
            // авторизация пройдена
            // создать JWT
            String jwt = util.generateToken(user.get().getEmail());
            return ResponseEntity.ok(jwt);
        }
        return ResponseEntity.badRequest().build();

        
    }
    
}
