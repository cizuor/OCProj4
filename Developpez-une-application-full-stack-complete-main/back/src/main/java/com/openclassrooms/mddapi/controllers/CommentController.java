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

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.CommentService;



/**
 * Contrôleur REST gérant les interactions liées aux commentaires.
 * <p>
 * Ce contrôleur permet aux utilisateurs de consulter les réactions sur les articles
 * et de publier leurs propres commentaires de manière sécurisée.
 * </p>
 */
@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/commentaire")
public class CommentController {

	/**
     * Service dédié à la manipulation des données de commentaires.
     */
	private final CommentService commentService;
	
	/**
     * Constructeur pour l'injection de dépendances.
     * 
     * @param commentService Le service gérant la logique métier des commentaires.
     */
	public CommentController( CommentService commentService) {
		this.commentService = commentService;
	}

	/**
     * Récupère un commentaire spécifique par son identifiant unique.
     * 
     * @param id Identifiant du commentaire recherché.
     * @return Une {@link ResponseEntity} contenant le {@link CommentDTO} correspondant.
     */
	@GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getById(@PathVariable("id") Long id) {
            CommentDTO comment = this.commentService.findByID(id);         
            return ResponseEntity.ok().body(comment);
    
	}
	
	/**
     * Récupère l'ensemble des commentaires associés à un article spécifique.
     * <p>
     * Les commentaires sont retournés par ordre chronologique décroissant.
     * </p>
     * 
     * @param postId Identifiant de l'article (Post) dont on veut les commentaires.
     * @return Une {@link ResponseEntity} contenant la liste des {@link CommentDTO}.
     */
	@GetMapping("/article/{id}")
	public ResponseEntity<List<CommentDTO>> getByPost(@PathVariable("id") Long postId){
		
		List<CommentDTO> lComment =  commentService.findByPostId(postId);
		return ResponseEntity.ok().body(lComment);
	}
	
	/**
     * Publie un nouveau commentaire sur un article.
     * <p>
     * L'identité de l'auteur est extraite du jeton JWT présent dans le contexte de sécurité, 
     * garantissant que l'utilisateur ne peut pas usurper l'identité d'un autre auteur.
     * </p>
     * 
     * @param commentDto Objet contenant le contenu textuel du commentaire.
     * @param postId Identifiant de l'article sur lequel porte le commentaire.
     * @return Une {@link ResponseEntity} contenant le {@link CommentDTO} créé.
     */
	@PostMapping("/article/{id}")
	public ResponseEntity<CommentDTO> create(@Valid @RequestBody CommentDTO commentDto,@PathVariable("id") Long postId){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		
		return ResponseEntity.ok().body(commentService.create(commentDto, postId, userDetails.getId()));
	}

}
