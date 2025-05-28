package com.example.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.todolist.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
