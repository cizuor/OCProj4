package com.openclassrooms.mddapi.controllers;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.PostService;
import com.openclassrooms.mddapi.services.TopicService;


/**
 * Contrôleur REST gérant les articles (Posts).
 * <p>
 * Ce contrôleur expose les points d'entrée pour la consultation, la création,
 * la mise à jour et la suppression des articles, ainsi que la gestion du fil d'actualités
 * personnalisé en fonction des abonnements de l'utilisateur.
 * </p>
 */
@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/articles")
public class PostController {
	
	/** Service gérant la logique métier des articles. */
	private final PostService postService;
	
	/** Service gérant la logique métier des thématiques. */
	private final TopicService themeService;
	
	


	/**
     * Constructeur pour l'injection des services requis.
     * 
     * @param postService Le service de gestion des articles.
     * @param themeService Le service de gestion des thèmes.
     */
	public PostController(PostService postService, TopicService themeService) {
		super();
		this.postService = postService;
		this.themeService = themeService;
	}

	/**
     * Récupère un article spécifique par son identifiant.
     * 
     * @param id L'identifiant unique de l'article.
     * @return Une {@link ResponseEntity} contenant le {@link PostDTO} de l'article.
     */
	@GetMapping("/{id}")
    public ResponseEntity<PostDTO> getById(@PathVariable("id") Long id) {
            PostDTO post = this.postService.findByID(id);         
            return ResponseEntity.ok().body(post);
    
	}
	
	/**
     * Récupère le fil d'actualités personnalisé de l'utilisateur connecté.
     * <p>
     * Le flux est filtré pour n'afficher que les articles appartenant aux thèmes 
     * auxquels l'utilisateur est abonné.
     * </p>
     * 
     * @param sort Sens du tri chronologique (valeur par défaut : "desc").
     * @return Une liste de {@link PostDTO} triée. Si aucun abonnement, retourne une liste vide.
     */
	@GetMapping("/feed")
	public ResponseEntity<List<PostDTO>> getFeed(@RequestParam(defaultValue = "desc") String sort){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		
		List<TopicDTO> lTopic =  themeService.getUserSubscriptions(userDetails.getId());
		
		if (lTopic == null || lTopic.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
		
		List<Long> lTopicId = lTopic.stream()
	            .map(TopicDTO::getId).toList();
		if(sort.equals("desc")) {		
			return ResponseEntity.ok().body(postService.findByThemeId(lTopicId));
		}else {
			return ResponseEntity.ok().body(postService.findByThemeIdOrderByCreateAtAsc(lTopicId));
		}
		
	}
	
	
	/**
     * Récupère la liste de tous les articles présents sur la plateforme.
     * 
     * @return Une {@link ResponseEntity} contenant la liste complète des {@link PostDTO}.
     */
	@GetMapping("")
	public ResponseEntity<List<PostDTO>> getAll(){
		return ResponseEntity.ok().body(postService.findAll());
	}
	
	
	/**
     * Publie un nouvel article.
     * <p>
     * L'identifiant de l'auteur est automatiquement injecté depuis le contexte de sécurité 
     * pour garantir l'intégrité de l'identité du créateur.
     * </p>
     * 
     * @param postDto Les données de l'article à créer.
     * @return Le {@link PostDTO} de l'article nouvellement créé.
     */
	@PostMapping()
	public ResponseEntity<PostDTO> create(@Valid @RequestBody PostDTO postDto ){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		postDto.setUserID(userDetails.getId()); // on ne crée pas un post au nom d'un autre
		return ResponseEntity.ok().body(postService.create(postDto));
	}
	
	/**
     * Met à jour un article existant.
     * <p>
     * Une vérification est effectuée par le service pour s'assurer que seul l'auteur original 
     * ou un utilisateur autorisé peut modifier le contenu.
     * </p>
     * 
     * @param id L'identifiant de l'article à modifier.
     * @param postDto Les nouvelles données de l'article.
     * @return Le {@link PostDTO} mis à jour.
     */
	@PutMapping("{id}")
	public ResponseEntity<PostDTO> update(@PathVariable("id") Long id,@Valid @RequestBody PostDTO postDto ){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		postDto.setUserID(userDetails.getId()); // on ne modifie pas un post au nom d'un autre
		return ResponseEntity.ok().body(postService.update(id,postDto,userDetails.getId()));
	}
	
	/**
     * Supprime un article de la plateforme.
     * 
     * @param id L'identifiant de l'article à supprimer.
     * @return Une réponse vide avec le statut HTTP 200 OK en cas de succès.
     */
	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		
		this.postService.delete(id,userDetails.getId());
		return ResponseEntity.ok().build();
	}
	
}
