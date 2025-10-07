package com.example.demoToDo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoToDo.model.Task;
import com.example.demoToDo.model.TaskDTO;
import com.example.demoToDo.model.User;
import com.example.demoToDo.repository.TaskRepository;

import jakarta.servlet.http.HttpSession;

@RestController
public class ApiTaskController {
    
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/api/todo")
    public ResponseEntity<?> addTask(@RequestBody TaskDTO taskDto, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if(currentUser == null) {
            return ResponseEntity.status(403).build();
        }
        
        if (taskDto.getTitle() == null) {
            return ResponseEntity.badRequest().build();
        }
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setUser(currentUser);
        task.setCompleted(false);
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }
}
