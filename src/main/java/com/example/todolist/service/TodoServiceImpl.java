package com.example.todolist.service;

import com.example.todolist.model.Todo;
import com.example.todolist.model.User;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl {
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Todo> getAllTodosByUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> todoRepository.findAll().stream()
                .filter(todo -> todo.getUser().getId().equals(userId))
                .toList())
                .orElse(List.of());
    }

    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    public Todo createTodo(Long userId, Todo todo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long id, Todo updatedTodo) {
        return todoRepository.findById(id).map(todo -> {
            todo.setTitle(updatedTodo.getTitle());
            todo.setDescription(updatedTodo.getDescription());
            todo.setCompleted(updatedTodo.isCompleted());
            return todoRepository.save(todo);
        }).orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
    }

    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }
}
