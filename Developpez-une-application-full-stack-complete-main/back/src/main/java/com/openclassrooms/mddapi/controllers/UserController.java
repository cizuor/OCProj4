package com.openclassrooms.mddapi.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.payload.request.UpdateUserRequest;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.TopicService;
import com.openclassrooms.mddapi.services.UserService;


/**
 * Contrôleur REST gérant les opérations liées aux utilisateurs.
 * <p>
 * Ce contrôleur permet de gérer le profil de l'utilisateur connecté, 
 * de consulter les informations publiques d'autres utilisateurs 
 * et de gérer le système d'abonnement aux thématiques.
 * </p>
 */
@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/utilisateur")
public class UserController {
	
	/** Service gérant la logique métier des utilisateurs. */
	private final UserService userService;
	
	/** Service gérant la logique métier des thématiques. */
	private final TopicService topicService;
	
	
	/**
     * Constructeur pour l'injection de dépendances.
     * 
     * @param userService Le service utilisateur injecté.
     * @param topicService Le service de thématiques injecté.
     */
	public UserController(UserService userService, TopicService topicService) {
		this.userService = userService;
		this.topicService = topicService;
	}

	/**
     * Récupère les informations publiques d'un utilisateur par son identifiant.
     * <p>
     * Par mesure de sécurité et de confidentialité, l'adresse e-mail de l'utilisateur 
     * est systématiquement masquée dans la réponse.
     * </p>
     * 
     * @param id L'identifiant unique de l'utilisateur recherché.
     * @return Une {@link ResponseEntity} contenant le {@link UserDTO} avec l'email masqué.
     */
	@GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable("id") Long id) {
            UserDTO user = this.userService.getUserById(id);
            user.setEmail("hide@information.com");            
            return ResponseEntity.ok().body(user);
    }
	
	/**
     * Récupère les informations détaillées du profil de l'utilisateur actuellement connecté.
     * <p>
     * L'identité de l'utilisateur est déterminée via le jeton JWT présent dans le contexte de sécurité.
     * </p>
     * 
     * @return Une {@link ResponseEntity} contenant le {@link UserDTO} complet de l'utilisateur courant.
     */
	@GetMapping("/me")
	public ResponseEntity<UserDTO> getMyProfile() {
	    //On récupère l'identifiant (email/pseudo) depuis le contexte de sécurité
	    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
	                                     .getAuthentication().getPrincipal();
	    
	    UserDTO user = userService.getUserById(userDetails.getId());
	    
	    return ResponseEntity.ok(user);
	}
	
	/**
     * Abonne l'utilisateur courant à une thématique spécifique.
     * <p>
     * Après l'ajout de l'abonnement en base de données, la méthode retourne la liste complète 
     * des thèmes pour permettre une mise à jour immédiate de l'interface utilisateur.
     * </p>
     * 
     * @param id L'identifiant de la thématique (topicId).
     * @return Une {@link ResponseEntity} contenant la liste mise à jour des {@link TopicDTO}.
     */
	@PostMapping("/subscribe/{topicId}")
	public ResponseEntity<List<TopicDTO>> postSubTheme(@PathVariable("topicId") Long id) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		
		userService.subscribe(userDetails.getId(), id);
		
		List<TopicDTO> updatedThemes = topicService.getTopics(userDetails.getId());
		
		return ResponseEntity.ok(updatedThemes);
	}
	
	/**
     * Désabonne l'utilisateur courant d'une thématique spécifique.
     * <p>
     * Comme pour l'abonnement, cette méthode retourne la liste complète des thèmes mis à jour.
     * </p>
     * 
     * @param id L'identifiant de la thématique (topicId).
     * @return Une {@link ResponseEntity} contenant la liste mise à jour des {@link TopicDTO}.
     */
	@PostMapping("/unsubscribe/{topicId}")
	public ResponseEntity<List<TopicDTO>> unsubscribe(@PathVariable("topicId") Long id) {
	    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
	            .getAuthentication().getPrincipal();
	    
	    userService.unsubscribe(userDetails.getId(), id);
	    
	    return ResponseEntity.ok(topicService.getTopics(userDetails.getId()));
	}
	
	
	/**
     * Met à jour les informations personnelles de l'utilisateur connecté.
     * <p>
     * Permet de modifier le pseudo, l'adresse e-mail et éventuellement le mot de passe.
     * Les données sont validées avant traitement.
     * </p>
     * 
     * @param updateRequest Objet contenant les nouvelles informations de l'utilisateur.
     * @return Une {@link ResponseEntity} contenant le {@link UserDTO} mis à jour.
     */
	@PutMapping("/me")
	public ResponseEntity<UserDTO> updateMyProfile(@Valid @RequestBody UpdateUserRequest updateRequest) {
	    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
	                                     .getAuthentication().getPrincipal();
	    
	    
	    
	    UserDTO updatedUser = userService.update(userDetails.getId(), updateRequest);
	    
	    return ResponseEntity.ok(updatedUser);
	}
	
}
