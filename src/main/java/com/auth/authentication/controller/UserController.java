package com.auth.authentication.controller;

import java.util.Collection;
// import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import com.auth.authentication.entity.User;
import com.auth.authentication.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/users") // only admin can view all users, hence i send role = "ADMIN" as param
    public ResponseEntity<?> getUsers(@RequestParam String role){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal.getUsername() == null || principal.getUsername().isEmpty()){
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        boolean isAdmin = authorities
            .stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_"+ role));
        
        System.out.println(authorities);
        
        if(!isAdmin){
            return ResponseEntity.status(403).body("Forbidden: You don't have the required role to access this resource");
        }

        return ResponseEntity.ok(userService.getAllUsers());
    }
}
