package com.dtnexus.crm.controller;

import com.dtnexus.crm.model.User;
import com.dtnexus.crm.repository.UserRepository;
import com.dtnexus.crm.security.JwtService;
import com.dtnexus.crm.service.TrialAccessService;
import com.dtnexus.crm.web.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TrialAccessService trialAccessService;
    private final boolean secureCookies;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TrialAccessService trialAccessService,
                          @Value("${security.secure-cookies:false}") boolean secureCookies) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.trialAccessService = trialAccessService;
        this.secureCookies = secureCookies;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByUsernameAndTenant(request.username(), request.tenant()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
        if (!trialAccessService.hasAccess(user.getUsername(), user.getTenant())) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Período de teste expirado");
        }
        String accessToken = jwtService.issue(user);
        ResponseCookie cookie = ResponseCookie.from("DT_NEXUS_ACCESS_TOKEN", accessToken)
            .httpOnly(true)
            .secure(secureCookies)
            .sameSite("Lax")
            .path("/")
            .maxAge(java.time.Duration.ofMinutes(15).toSeconds()) // Corrigido: Passando o valor convertido para long segundos
            .build();
            
        return ResponseEntity.ok()
            .header("Set-Cookie", cookie.toString())
            .body(new LoginResponse(
                accessToken,
                user.getTenant(),
                user.getAccountStatus(), // Corrigido: Removida a vírgula que quebrava a sintaxe aqui
                trialAccessService.remainingDays(user)));
    }

    private record LoginResponse(String accessToken, String tenant, String accountStatus, long trialDaysRemaining) {
    }
}
