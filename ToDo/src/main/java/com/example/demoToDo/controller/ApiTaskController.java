package com.example.demoToDo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demoToDo.model.Task;
import com.example.demoToDo.model.TaskDTO;
import com.example.demoToDo.model.User;
import com.example.demoToDo.repository.TaskRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/todo")
public class ApiTaskController {
    
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping
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

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(403).build();
        }
        List<Task> tasks = taskRepository.findByUser(currentUser);
        List<TaskDTO> dtoList = tasks.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(403).build();
        }
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null || !task.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDto(task));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDto, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(403).build();
        }
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null || !task.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        if (taskDto.getTitle() !=null) {
            task.setTitle(taskDto.getTitle());
        }
        if (taskDto.getDescription() !=null) {
            task.setDescription(taskDto.getDescription());
        }
        if (taskDto.getCompleted() !=null) {
            task.setCompleted(taskDto.getCompleted());  
        }
        taskRepository.save(task);
        return ResponseEntity.ok(convertToDto(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(403).build();
        }
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null || !task.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }
    private TaskDTO convertToDto(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        return dto;
    } 
}
