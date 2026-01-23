package com.openclassrooms.mddapi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.payload.request.LoginRequest;
import com.openclassrooms.mddapi.payload.request.SignUpRequest;
import com.openclassrooms.mddapi.repository.UserRepository;


@SpringBootTest
@Transactional
class AuthServiceTest {
	
    private final AuthService authService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    
    
    

    public AuthServiceTest(AuthService authService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		super();
		this.authService = authService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}


	@BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }
    
    
    @Test
    void register_ShouldSaveUserWithEncodedPassword() {
        // ARRANGE
        SignUpRequest request = new SignUpRequest();
        request.setPseudo("JeanMimi");
        request.setEmail("jean@test.com");
        request.setPassword("password123");

        // ACT
        authService.register(request);

        // ASSERT
        User savedUser = userRepository.findByEmailOrPseudo("jean@test.com","jean@test.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPseudo()).isEqualTo("JeanMimi");
        assertThat(savedUser.getPassword()).startsWith("$2a$"); 
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
    	// ARRANGE
        SignUpRequest request1 = new SignUpRequest();
        request1.setPseudo("User1");
        request1.setEmail("duplicate@test.com");
        request1.setPassword("pwd");
        authService.register(request1);

        SignUpRequest request2 = new SignUpRequest();
        request2.setPseudo("User2");
        request2.setEmail("duplicate@test.com");
        request2.setPassword("pwd");
        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> authService.register(request2));
    }

    @Test
    void authenticate_ShouldReturnAuthentication_WhenCredentialsAreCorrect() {
        // ARRANGE
        SignUpRequest reg = new SignUpRequest();
        reg.setPseudo("Tester");
        reg.setEmail("tester@test.com");
        reg.setPassword("secret");
        authService.register(reg);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("tester@test.com");
        loginRequest.setPassword("secret");

        // ACT
        Authentication auth = authService.authenticate(loginRequest);

        // ASSERT
        assertThat(auth.isAuthenticated()).isTrue();
        assertThat(auth.getName()).isEqualTo("tester@test.com");
    }

    @Test
    void authenticate_ShouldThrowException_WhenPasswordIsWrong() {
        // ARRANGE
        SignUpRequest reg = new SignUpRequest();
        reg.setPseudo("Tester");
        reg.setEmail("tester@test.com");
        reg.setPassword("secret");
        authService.register(reg);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("tester@test.com");
        loginRequest.setPassword("wrong_password");

        // ACT & ASSERT
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(loginRequest));
    }

    @Test
    void generateToken_ShouldReturnValidString() {
        // ARRANGE
        SignUpRequest reg = new SignUpRequest();
        reg.setPseudo("tokenUser");
        reg.setEmail("token@test.com");
        reg.setPassword("secret");
        authService.register(reg);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("token@test.com");
        loginRequest.setPassword("secret");
        Authentication auth = authService.authenticate(loginRequest);

        // ACT
        String token = authService.generateToken(auth);

        // ASSERT
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.").length).isEqualTo(3);
    }

}
