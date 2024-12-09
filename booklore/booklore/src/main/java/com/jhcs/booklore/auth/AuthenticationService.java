package com.jhcs.booklore.auth;

import com.jhcs.booklore.email.EmailService;
import com.jhcs.booklore.email.EmailTemplateName;
import com.jhcs.booklore.entity.Role;
import com.jhcs.booklore.entity.Token;
import com.jhcs.booklore.entity.User;
import com.jhcs.booklore.repository.RoleRepository;
import com.jhcs.booklore.repository.TokenRepository;
import com.jhcs.booklore.repository.UserRepository;

import com.jhcs.booklore.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    private final String activationUrl = "http://localhost:4200/activate-account";

    public void activateAccount(String token)
            throws
            MessagingException {
        var tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (tokenEntity.getExpiresAt()
                .isBefore(LocalDateTime.now())) {
            senfValidationEmail(tokenEntity.getUser());
            throw new RuntimeException("Token expired");
        } var user = userRepository.findById(tokenEntity.getUser()
                        .getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found")); user.setEnabled(true);
        userRepository.save(user); tokenRepository.delete(tokenEntity);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),
                request.password()));

        var claims = new HashMap<String, Object>(); var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.fullName());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse register(RegisterRequest request)
            throws
            MessagingException {
        var userole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found")); var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userole))
                .build();


        userRepository.save(user); senfValidationEmail(user);


        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void senfValidationEmail(User user)
            throws
            MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(user.getEmail(), user.fullName(), EmailTemplateName.ACTIVATE_ACCOUNT, activationUrl,
                newToken, "Activate your account");

    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateAndSaveActivationCode(6);

        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now()
                        .plusMinutes(15))
                .user(user)
                .build(); tokenRepository.save(token); return generatedToken;

    }

    private String generateAndSaveActivationCode(int lenght) {
        String characters = "0123456789"; StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom(); for (int i = 0; i < lenght; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        } return codeBuilder.toString();
    }


}