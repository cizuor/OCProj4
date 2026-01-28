package com.openclassrooms.mddapi.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.TopicService;

/**
 * Contrôleur REST gérant les thématiques (Topics).
 * <p>
 * Ce contrôleur permet aux utilisateurs de consulter le catalogue des thèmes disponibles,
 * de voir leurs abonnements personnels et de créer de nouvelles thématiques.
 * Chaque retour de thématique inclut un indicateur permettant de savoir si l'utilisateur
 * courant y est abonné.
 * </p>
 */
@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/topic")
public class TopicController {
	
	/**
	* Service gérant la logique métier des thématiques.
	*/
	private final TopicService topicService;
	
	

	
	/**
     * Constructeur pour l'injection de dépendances.
     * 
     * @param topicService Le service de gestion des thèmes injecté.
     */
	public TopicController(TopicService topicService) {
		super();
		this.topicService = topicService;
	}
	
	/**
     * Récupère un thème spécifique par son identifiant.
     * <p>
     * L'ID de l'utilisateur connecté est extrait du contexte de sécurité pour
     * déterminer si ce thème fait partie de ses abonnements (champ isLiked).
     * </p>
     * 
     * @param id L'identifiant unique du thème.
     * @return Une {@link ResponseEntity} contenant le {@link TopicDTO}.
     */
	@GetMapping("/{id}")
    public ResponseEntity<TopicDTO> getById(@PathVariable("id") Long id) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        TopicDTO topic = this.topicService.findByID(id,userDetails.getId());         
        return ResponseEntity.ok().body(topic);
	}
	
	/**
     * Récupère la liste de tous les thèmes disponibles sur la plateforme.
     * <p>
     * Pour chaque thème, le statut d'abonnement de l'utilisateur connecté est calculé.
     * </p>
     * 
     * @return Une {@link ResponseEntity} contenant la liste des {@link TopicDTO}.
     */
	@GetMapping()
    public ResponseEntity<List<TopicDTO>> getAll() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		List<TopicDTO> lTheme = this.topicService.getTopics(userDetails.getId());         
        return ResponseEntity.ok().body(lTheme);
	}
	
	/**
     * Récupère uniquement les thèmes auxquels l'utilisateur courant est abonné.
     * 
     * @return Une {@link ResponseEntity} contenant la liste filtrée des {@link TopicDTO}.
     */
	@GetMapping("/suivie")
    public ResponseEntity<List<TopicDTO>> getAllLiked() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		List<TopicDTO> lTheme = this.topicService.getUserSubscriptions(userDetails.getId());         
        return ResponseEntity.ok().body(lTheme);
	}
	
	/**
     * Crée une nouvelle thématique sur la plateforme.
     * 
     * @param topicDto Les données du thème à créer (nom et description).
     * @return Une {@link ResponseEntity} contenant le {@link TopicDTO} nouvellement créé.
     */
	@PostMapping()
	public ResponseEntity<TopicDTO> create(@Valid @RequestBody TopicDTO topicDto ){
		return ResponseEntity.ok().body(topicService.create(topicDto));
	}
	
	

}
