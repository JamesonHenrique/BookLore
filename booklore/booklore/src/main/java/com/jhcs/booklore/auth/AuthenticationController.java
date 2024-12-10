package com.jhcs.booklore.auth;

import com.jhcs.booklore.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request
                                                          )
            throws
            MessagingException {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
                                                              ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }



    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
                       ) throws MessagingException {
        authenticationService.activateAccount(token);
    }
}
