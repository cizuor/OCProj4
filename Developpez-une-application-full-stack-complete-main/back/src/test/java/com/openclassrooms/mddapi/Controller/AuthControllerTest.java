package com.openclassrooms.mddapi.Controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.payload.request.LoginRequest;
import com.openclassrooms.mddapi.payload.request.SignUpRequest;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.services.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    void registerUser_ShouldReturn200_WhenValidRequest() throws Exception {
    	// ARRANGE
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setPseudo("NewUser");
        signUpRequest.setEmail("new@test.com");
        signUpRequest.setPassword("password123");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void registerUser_ShouldReturn400_WhenEmailAlreadyExists() throws Exception {
        // ARRANGE
        SignUpRequest firstUser = new SignUpRequest();
        firstUser.setPseudo("First");
        firstUser.setEmail("same@test.com");
        firstUser.setPassword("password");
        authService.register(firstUser);

        SignUpRequest duplicateEmailRequest = new SignUpRequest();
        duplicateEmailRequest.setPseudo("Second");
        duplicateEmailRequest.setEmail("same@test.com");
        duplicateEmailRequest.setPassword("password");

        
        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreCorrect() throws Exception {
    	// ARRANGE
        SignUpRequest register = new SignUpRequest();
        register.setPseudo("LoginUser");
        register.setEmail("login@test.com");
        register.setPassword("secret123");
        authService.register(register);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("login@test.com");
        loginRequest.setPassword("secret123");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.pseudo").value("login@test.com"));
    }

    @Test
    void login_ShouldReturn401_WhenPasswordIsWrong() throws Exception {
    	// ARRANGE
        SignUpRequest register = new SignUpRequest();
        register.setPseudo("WrongPwdUser");
        
        register.setEmail("wrong@test.com");
        register.setPassword("correct_password");
        authService.register(register);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("wrong@test.com");
        loginRequest.setPassword("bad_password");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // 401 géré par Spring Security
    }
}
