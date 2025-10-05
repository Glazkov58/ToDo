package com.example.demoToDo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demoToDo.model.User;
import com.example.demoToDo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String password,
        Model model) {
            if(userRepository.findByEmail(email).isPresent()) {
                model.addAttribute("error", "Пользователь с таким email уже существует");
                return "register";
            }
            User user = new User(name, email, password);
            userRepository.save(user);
            return "redirect:/?error";
        }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false)String error, @RequestParam(required = false)String registered, Model model) {
        if (error !=null) model.addAttribute("error", "Неверный email или пароль");
        if (registered !=null) model.addAttribute("message", "Регистрация прошла успешно!");
        return "login";
    }

    @PostMapping("/login")
    public String login(
        @RequestParam String username, //email
        @RequestParam String password,
        HttpSession session, Model model){
            var userOpt = userRepository.findByEmail(username);
            if(userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
                // Успешный вход — сохраняем пользователя в сессии
                session.setAttribute("currentUser", userOpt.get());
                return "redirect:/";
            }else{
                model.addAttribute("error", "Неверный email или пароль");
                return "redirect:/?error";
            } 
        }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    
} 
    
