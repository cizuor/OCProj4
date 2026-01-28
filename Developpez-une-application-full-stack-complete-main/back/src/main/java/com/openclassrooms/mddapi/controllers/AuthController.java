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


/**
 * Contrôleur REST gérant les opérations d'authentification.
 * <p>
 * Ce contrôleur fournit les points d'entrée pour l'inscription des nouveaux utilisateurs
 * et la connexion des utilisateurs existants via des jetons JWT.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	/**
     * Service gérant la logique métier liée à l'authentification.
     */
	private final AuthService authService;
	
	
	
	/**
     * Initialise le contrôleur avec ses dépendances.
     * 
     * @param authService Le service d'authentification injecté.
     */
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	/**
     * Authentifie un utilisateur et génère un jeton d'accès JWT.
     * <p>
     * Cette méthode valide les identifiants fournis, initialise le contexte de sécurité
     * et retourne les informations nécessaires à la session de l'utilisateur.
     * </p>
     *
     * @param loginRequest Objet contenant les identifiants de connexion (email/pseudo et mot de passe).
     * @return Une {@link ResponseEntity} contenant le jeton {@link JwtResponse} et les détails de l'utilisateur.
     */
	@PostMapping("/login")
	public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
	
	    Authentication authentication = authService.authenticate(loginRequest);
	    String jwt = authService.generateToken(authentication);
	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	
	    return ResponseEntity.ok(new JwtResponse(jwt, 
	            userDetails.getId(), 
	            userDetails.getUsername()));
	}
	
	/**
     * Enregistre un nouvel utilisateur dans le système.
     * <p>
     * Vérifie la validité des données fournies, s'assure de l'unicité du pseudo et de l'email,
     * puis procède au hachage du mot de passe avant la persistance.
     * </p>
     *
     * @param signUpRequest Objet contenant les informations d'inscription (pseudo, email, mot de passe).
     * @return Une {@link ResponseEntity} contenant un {@link MessageResponse} de succès ou d'erreur.
     */
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
