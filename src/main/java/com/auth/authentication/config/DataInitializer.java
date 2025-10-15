// package com.auth.authentication.config;

// import java.util.Arrays;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

// import com.auth.authentication.entity.User;
// import com.auth.authentication.repository.UserRepository;

// @Component
// public class DataInitializer implements CommandLineRunner {

//     private final UserRepository userRepository;  // BEAN from UserRepository
//     private final PasswordEncoder passwordEncoder;  // BEAN from SecurityConfig

//     // Constructor Injection
//     public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//         this.userRepository = userRepository;
//         this.passwordEncoder = passwordEncoder;
//     }

//     @Override
//     public void run(String... args) throws Exception {
//         // Initialize default users or roles if necessary
//         if(userRepository.findByUsername("admin").isEmpty()){
//             User admin = new User();
//             admin.setUsername("admin");
//             admin.setEmail("admin@gmail.com");
//             admin.setPassword(passwordEncoder.encode("admin123"));
//             admin.setRoles(Arrays.asList("ADMIN"));
//             userRepository.save(admin);

//             System.out.println("DEFAULT ADMIN CREATED: admin/admin123");
//             System.out.println("USERNAME: admin");
//             System.out.println("PASSWORD: admin123");
//         }
// }
// }