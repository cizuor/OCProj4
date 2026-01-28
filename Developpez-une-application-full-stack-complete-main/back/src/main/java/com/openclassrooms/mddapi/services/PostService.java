package com.openclassrooms.mddapi.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

/**
 * Service gérant la logique métier des articles (Posts).
 * <p>
 * Ce service assure le cycle de vie des articles : création, consultation, 
 * mise à jour sécurisée et suppression. Il gère également le filtrage 
 * par thématiques pour le fil d'actualités.
 * </p>
 */
@Service
public class PostService {
	
	/** Repository pour l'accès aux données des articles. */
	private final PostRepository postRepository;
	
	/** Repository pour l'accès aux données des utilisateurs. */
    private final UserRepository userRepository;

    /** Repository pour l'accès aux données des thématiques. */
    private final TopicRepository themeRepository;
    
    /** Message d'erreur standard pour un article non trouvé. */
    private String strPostNotFound = "Post non trouvé";
    
    
    /**
     * Constructeur pour l'injection de dépendances par constructeur.
     * 
     * @param postRepository  Le répository des articles.
     * @param userRepository   Le répository des utilisateurs.
     * @param themeRepository  Le répository des thématiques.
     */
	public PostService(PostRepository postRepository, UserRepository userRepository, TopicRepository themeRepository) {
		super();
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.themeRepository = themeRepository;
	}

	/**
     * Récupère un article par son identifiant unique.
     * 
     * @param id L'identifiant de l'article.
     * @return Le {@link PostDTO} correspondant.
     * @throws EntityNotFoundException Si l'article n'existe pas.
     */
	public PostDTO findByID(Long id){
		Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(strPostNotFound));
		
		return PostDTO.fromEntity(post);
	}
	
	 /**
     * Récupère la liste exhaustive de tous les articles de la plateforme.
     * 
     * @return Une liste de {@link PostDTO}.
     */
	public List<PostDTO> findAll(){
		return postRepository.findAll().stream()
				.map(PostDTO::fromEntity).toList();
	}
	
	/**
     * Récupère les articles appartenant à une liste de thématiques.
     * <p>
     * Utilise le tri par défaut .
     * </p>
     * 
     * @param listThemesId Liste des identifiants des thèmes (Topics).
     * @return Une liste de {@link PostDTO} filtrée.
     */
	public List<PostDTO> findByThemeId(List<Long> listThemesId){
		return postRepository.findByTopicIdIn(listThemesId).stream()
				.map(PostDTO::fromEntity).toList();
	}
	
	/**
     * Récupère les articles filtrés par thématiques avec un tri chronologique ascendant.
     * 
     * @param listThemesId Liste des identifiants des thèmes (Topics).
     * @return Une liste de {@link PostDTO} triée du plus ancien au plus récent.
     */
	public List<PostDTO> findByThemeIdOrderByCreateAtAsc(List<Long> listThemesId){
		return postRepository.findByTopic_IdIn(listThemesId,Sort.by(Sort.Direction.ASC, "createdAt")).stream()
				.map(PostDTO::fromEntity).toList();
	}
	
	/**
     * Enregistre un nouvel article.
     * <p>
     * L'auteur et la thématique sont validés avant la création de l'entité.
     * </p>
     * 
     * @param postDto Le DTO contenant les informations de l'article.
     * @return Le {@link PostDTO} de l'article créé avec son identifiant généré.
     * @throws EntityNotFoundException Si l'auteur ou le thème n'existe pas.
     */
	public PostDTO create(PostDTO postDto) {
		
		User user = userRepository.findById(postDto.getUserID())
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé"));
        Topic theme = themeRepository.findById(postDto.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));
        
        Post post = new Post();
        post.setTitre(postDto.getTitre());
        post.setContenu(postDto.getContenu());
        post.setAuteur(user);
        post.setTopic(theme);
        
        Post savedPost = postRepository.save(post);
		
		return PostDTO.fromEntity(savedPost);
	}
	
	/**
     * Supprime un article après vérification des droits d'accès.
     * 
     * @param id      L'identifiant de l'article à supprimer.
     * @param userId  L'identifiant de l'utilisateur demandant la suppression.
     * @throws EntityNotFoundException Si l'article n'existe pas.
     * @throws AccessDeniedException   Si l'utilisateur n'est pas l'auteur de l'article.
     */
	public void delete(Long id,Long userId) {
		Post post = postRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException(strPostNotFound));
		
		if(!post.getAuteur().getId().equals(userId)) {
			throw new AccessDeniedException("Vous n'avez pas l'autorisation de modifier ce post");
		}
			postRepository.delete(post);
	}
	
	/**
     * Met à jour les informations d'un article existant.
     * <p>
     * Seuls le titre, le contenu et le thème peuvent être modifiés. 
     * Une vérification de l'identité de l'auteur est effectuée avant toute modification.
     * </p>
     * 
     * @param id       L'identifiant de l'article à modifier.
     * @param postDto  Le DTO contenant les nouvelles valeurs.
     * @param userId   L'identifiant de l'utilisateur effectuant la mise à jour.
     * @return Le {@link PostDTO} mis à jour.
     * @throws EntityNotFoundException Si l'article ou le nouveau thème n'existe pas.
     * @throws AccessDeniedException   Si l'utilisateur n'est pas l'auteur original.
     */
	public PostDTO update(Long id, PostDTO postDto, Long userId) {
		
		Post existingPost = postRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException(strPostNotFound));
			
		
		if (!existingPost.getAuteur().getId().equals(userId)) {
			 throw new AccessDeniedException("Vous n'avez pas l'autorisation de modifier ce post");
	    }
		
		
        existingPost.setTitre(postDto.getTitre());
        existingPost.setContenu(postDto.getContenu());
        
        
        if(!existingPost.getTopic().getId().equals(postDto.getTopicId())) {
            Topic theme = themeRepository.findById(postDto.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));
            existingPost.setTopic(theme);
        }
        return PostDTO.fromEntity(postRepository.save(existingPost));
	}

}
