package com.example.demoToDo;

import com.example.demoToDo.controller.ApiTaskController;
import com.example.demoToDo.model.Task;
import com.example.demoToDo.model.User;
import com.example.demoToDo.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any; // Импортируйте any
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiTaskController.class) // Убираем всю конфигурацию исключения
class ApiTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");

        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setCompleted(false);
        task1.setUser(testUser);

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setCompleted(true);
        task2.setUser(testUser);
    }

    @Test
    // Используем аннотацию для имитации аутентифицированного пользователя
    // или можно использовать .with( user("testuser").roles("USER") ) в perform()
    void getAllTasks_shouldReturnListOfTasksForAuthenticatedUser() throws Exception {
        // Теперь контроллер должен получать пользователя через @AuthenticationPrincipal
        // или SecurityContextHolder. Мокаем репозиторий, чтобы он реагировал на любого пользователя.
        when(taskRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].completed").value(true));
    }

    @Test
    void getAllTasks_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // Если безопасность настроена на перенаправление на логин, статус будет 302
        // Если это REST API и нет страницы логина, статус будет 401 Unauthorized
        // 403 Forbidden обычно для аутентифицированного, но не авторизованного пользователя.
        // Для анонимуса правильнее ожидать 401.
        mockMvc.perform(get("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON))
                // Ожидаем 401 Unauthorized, так как пользователь не аутентифицирован
                .andExpect(status().isUnauthorized()); 
    }
}