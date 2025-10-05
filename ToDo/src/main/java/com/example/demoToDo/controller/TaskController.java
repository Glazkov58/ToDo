package com.example.demoToDo.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import com.example.demoToDo.model.Task;
import com.example.demoToDo.model.User;
import com.example.demoToDo.repository.TaskRepository;

import jakarta.servlet.http.HttpSession;


@Controller
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/")
    public String getAllTasks(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if(currentUser == null) {
            // Пользователь не вошёл — покажем index.html, но без задач
            model.addAttribute("tasks", new ArrayList<Task>());
            model.addAttribute("task",new Task());
            model.addAttribute("showLoginModal", true); // ← флаг для открытия модалки
            return "index";
        }
        model.addAttribute("tasks",taskRepository.findAll());
        model.addAttribute("task",new Task());
        model.addAttribute("user", currentUser);
        return "index";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if(currentUser == null) {
            return "redirect:/login";
        }
        task.setUser(currentUser);
        task.setCompleted(false);
        taskRepository.save(task);
        return "redirect:/";
    }

    @GetMapping("/toggle/{id}")
    public String toggleTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid task ID: " + id));
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/";
    }
}
