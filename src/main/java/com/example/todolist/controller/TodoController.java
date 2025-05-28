package com.example.todolist.controller;

import com.example.todolist.dto.todo.TodoRequest;
import com.example.todolist.dto.todo.TodoResponse;
import com.example.todolist.model.Todo;
import com.example.todolist.model.User;
import com.example.todolist.service.TodoServiceImpl;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    @Autowired
    private TodoServiceImpl todoService;
    @Autowired
    private UserRepository userRepository;

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        String username = getCurrentUsername();
        if (username == null) return ResponseEntity.status(401).build();
        Optional<User> userOpt = userRepository.findAll().stream().filter(u -> u.getUsername().equals(username)).findFirst();
        if (userOpt.isEmpty()) return ResponseEntity.status(404).build();
        List<TodoResponse> todos = todoService.getAllTodosByUser(userOpt.get().getId())
                .stream()
                .map(todo -> new TodoResponse(todo.getId(), todo.getTitle(), todo.getDescription(), todo.isCompleted(), username))
                .collect(Collectors.toList());
        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@RequestBody TodoRequest request) {
        String username = getCurrentUsername();
        if (username == null) return ResponseEntity.status(401).build();
        Optional<User> userOpt = userRepository.findAll().stream().filter(u -> u.getUsername().equals(username)).findFirst();
        if (userOpt.isEmpty()) return ResponseEntity.status(404).build();
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.isCompleted());
        Todo saved = todoService.createTodo(userOpt.get().getId(), todo);
        return ResponseEntity.ok(new TodoResponse(saved.getId(), saved.getTitle(), saved.getDescription(), saved.isCompleted(), username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @RequestBody TodoRequest request) {
        String username = getCurrentUsername();
        if (username == null) return ResponseEntity.status(401).build();
        Todo updated = new Todo();
        updated.setTitle(request.getTitle());
        updated.setDescription(request.getDescription());
        updated.setCompleted(request.isCompleted());
        Todo saved = todoService.updateTodo(id, updated);
        return ResponseEntity.ok(new TodoResponse(saved.getId(), saved.getTitle(), saved.getDescription(), saved.isCompleted(), username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
