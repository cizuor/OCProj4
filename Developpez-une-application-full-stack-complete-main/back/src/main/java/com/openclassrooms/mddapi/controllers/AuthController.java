package com.openclassrooms.mddapi.controllers;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.mddapi.payload.request.LoginRequest;
import com.openclassrooms.mddapi.payload.request.SignUpRequest;
import com.openclassrooms.mddapi.payload.response.JwtResponse;
import com.openclassrooms.mddapi.payload.response.MessageResponse;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	    private final AuthService authService;
	 	
	 	


	    public AuthController(AuthService authService) {
			this.authService = authService;
		}

		@PostMapping("/login")
	    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

	        Authentication authentication = authService.authenticate(loginRequest);
	        String jwt = authService.generateToken(authentication);
	        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

	        return ResponseEntity.ok(new JwtResponse(jwt, 
                    userDetails.getId(), 
                    userDetails.getUsername()));
	    }

	    @PostMapping("/register")
	    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
	        try {
	            authService.register(signUpRequest);
	            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	        } catch (RuntimeException e) {
	            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
	        }
	    }
}
