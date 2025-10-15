package com.auth.authentication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.authentication.entity.User;
import com.auth.authentication.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).get();
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username).get();
    }

    public boolean existsByEmail(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username){
        return userRepository.findByUsername(username).isPresent();
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
