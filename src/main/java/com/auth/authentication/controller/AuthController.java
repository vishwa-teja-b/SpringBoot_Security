package com.auth.authentication.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// import org.hibernate.engine.jdbc.env.internal.LobCreationLogging_.logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;
import org.slf4j.*;
import com.auth.authentication.dto.LoginDTO;
import com.auth.authentication.dto.PasswordResetDTO;
import com.auth.authentication.dto.UserRegistrationDTO;
import com.auth.authentication.entity.PasswordResetToken;
import com.auth.authentication.entity.User;
import com.auth.authentication.repository.PasswordResetTokenRepository;
import com.auth.authentication.service.EmailService;
import com.auth.authentication.service.UserService;

// import ch.qos.logback.core.subst.Token;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    // private final Map<String, String> passwordResetPins = new HashMap<>(); // In-memory store for PINs
    private PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, PasswordResetTokenRepository passwordResetTokenRepository, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.tokenRepository = passwordResetTokenRepository;
    }

    @PostMapping("/public/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userDTO){

        if(userService.existsByUsername(userDTO.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already in use");
        }

        if(userService.existsByEmail(userDTO.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already in use");
        }
       
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRoles(userDTO.getRoles());
        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }


    @PostMapping("/public/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        try{
            // a spring security class that holds the username and password

            UsernamePasswordAuthenticationToken credentialsToken = 
                new UsernamePasswordAuthenticationToken(loginDTO.getUsernameOrEmail(),
                                                    loginDTO.getPassword());
            
            // we create an auth token using the username and password from client

            Authentication authentication = authenticationManager.authenticate(credentialsToken);

            // once authentication is successful, we set the authentication in the security coontext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // for form based authentication DAOAuthenticationProvider returns UserDetails object
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // JSON response map
            Map<String, Object> response = new HashMap<>();
            response.put("username", userDetails.getUsername());
            response.put("roles", userDetails.getAuthorities()
                                .stream()
                                .map(authority -> authority.getAuthority())
                                .collect(Collectors.toList()));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch(AuthenticationException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){
        try{
            User user = userService.findByEmail(email);

            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with the provided email");
            }

            // Generate a PIN
            String pin = String.format("%06d", (int)(Math.random() * 100000)); // 6-digit PIN                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
            // passwordResetPins.put(email, pin); // Store the PIN associated with the email
            PasswordResetToken token = new PasswordResetToken();
            token.setEmail(email);
            token.setPin(pin);
            token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
            token.setUsed(false);
            tokenRepository.save(token);

            // SEND EMAIL WITH PIN
            emailService.sendResetPasswordEmail(email, pin);

            return ResponseEntity.ok("Password reset PIN sent to email (expires in 15 minutes)");

        }catch(Exception ex){
            logger.error("Error processing forgot password request for email {}: {}", email, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing request: " + ex.getMessage());
        }
    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO){
        try{
            // Validate the PIN
            Optional<PasswordResetToken> token = tokenRepository.findByEmailAndPinAndUsedFalse(passwordResetDTO.getEmail(), passwordResetDTO.getPin());

            if(token.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID TOKEN");
            }

            PasswordResetToken resetToken = token.get();

            if(resetToken.isExpired()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PIN has expired");
            }

            // UPDATE THE PASSWORD
            User user = userService.findByEmail(passwordResetDTO.getEmail());

            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with the provided email");
            }

            user.setPassword(passwordResetDTO.getNewPassword());
            userService.saveUser(user);

            // remove the used PIN
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);
            // passwordResetPins.remove(passwordResetDTO.getEmail());

            return ResponseEntity.ok("Password reset successfully");
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing request: " + ex.getMessage());
        }
    }


    @PutMapping("/private/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordChangeRequest){
        try{
            String newPassword = passwordChangeRequest.get("password");
            if(newPassword == null || newPassword.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password must be provided");
            }

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByUsername(username);

            if(user != null){
                user.setPassword(newPassword);
                userService.saveUser(user);
                return ResponseEntity.ok("Password changed successfully");
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        catch(Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error changing password: " + ex.getMessage());
        }
    }
    
}
