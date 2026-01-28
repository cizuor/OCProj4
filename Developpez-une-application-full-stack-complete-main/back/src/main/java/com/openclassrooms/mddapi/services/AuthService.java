package com.openclassrooms.mddapi.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.exception.BadRequestException;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.payload.request.LoginRequest;
import com.openclassrooms.mddapi.payload.request.SignUpRequest;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.jwt.JwtUtils;

/**
 * Service gérant la logique métier liée à l'authentification et à la sécurité.
 * <p>
 * Ce service assure l'authentification des utilisateurs, la génération de jetons JWT
 * ainsi que l'enregistrement de nouveaux comptes avec validation des contraintes d'unicité.
 * </p>
 */
@Service
public class AuthService {
	
	/** Gestionnaire d'authentification de Spring Security. */
    private final AuthenticationManager authenticationManager;

    /** Répository pour l'accès aux données des utilisateurs. */
    private final UserRepository userRepository;

    /** Composant utilisé pour le hachage sécurisé des mots de passe (BCrypt). */
    private final PasswordEncoder passwordEncoder;

    /** Utilitaire pour la création et la validation des jetons JWT. */
    private final JwtUtils jwtUtils;
    
    
    
    
    /**
     * Constructeur pour l'injection de dépendances par constructeur.
     *
     * @param authenticationManager Le gestionnaire d'authentification Spring Security.
     * @param userRepository        Le répository pour la gestion des utilisateurs.
     * @param passwordEncoder       L'encodeur de mot de passe.
     * @param jwtUtils              L'utilitaire pour les jetons JWT.
     */
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
			PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
		super();
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
	}

    
    /**
     * Authentifie un utilisateur auprès du système.
     * <p>
     * Cette méthode utilise le {@link AuthenticationManager} pour vérifier les identifiants fournis.
     * En cas de succès, elle met à jour le {@link SecurityContextHolder} avec l'objet {@link Authentication}.
     * </p>
     *
     * @param loginRequest DTO contenant le login (email/pseudo) et le mot de passe.
     * @return L'objet {@link Authentication} validé.
     */
	public Authentication authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
	
	
	/**
     * Génère un jeton JWT à partir d'un objet d'authentification valide.
     *
     * @param authentication L'objet d'authentification de l'utilisateur connecté.
     * @return Une chaîne de caractères représentant le jeton JWT.
     */
    public String generateToken(Authentication authentication) {
        return jwtUtils.generateJwtToken(authentication);
    }

    
    /**
     * Enregistre un nouvel utilisateur dans le système.
     * <p>
     * Procède à la vérification de l'unicité du pseudo et de l'email avant la création.
     * Le mot de passe est systématiquement encodé via {@link PasswordEncoder} avant persistance.
     * </p>
     *
     * @param signUpRequest DTO contenant les informations d'inscription.
     * @throws BadRequestException Si le pseudo ou l'email est déjà utilisé par un autre compte.
     */
    public void register(SignUpRequest signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByPseudo(signUpRequest.getPseudo()))) {
            throw new BadRequestException("Error: Pseudo is already taken!");
        }
        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        User user = new User();
        user.setPseudo(signUpRequest.getPseudo());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);
    }
}
