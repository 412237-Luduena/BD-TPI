package com.example.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.todolist.model.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
