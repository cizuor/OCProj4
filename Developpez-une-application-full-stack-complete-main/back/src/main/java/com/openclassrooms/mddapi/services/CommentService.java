package com.openclassrooms.mddapi.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

/**
 * Service gérant la logique métier des commentaires.
 * <p>
 * Ce service assure la persistance des nouveaux commentaires et leur récupération 
 * par article, en effectuant la conversion entre les entités JPA et les DTO.
 * </p>
 */
@Service
public class CommentService {

	/** Repository pour l'accès aux données des commentaires. */
	private final CommentRepository commentRepository;
	
	/** Repository pour l'accès aux données des utilisateurs. */
	private final UserRepository userRepository;
	
	/** Repository pour l'accès aux données des articles. */
	private final PostRepository postRepository;
		
	
	/**
     * Constructeur pour l'injection de dépendances.
     * 
     * @param commentRepository Le répository des commentaires.
     * @param userRepository    Le répository des utilisateurs.
     * @param postRepository    Le répository des articles.
     */
	public CommentService(CommentRepository commentRepository, UserRepository userRepository,
			PostRepository postRepository) {
		super();
		this.commentRepository = commentRepository;
		this.userRepository = userRepository;
		this.postRepository = postRepository;
	}

	/**
     * Récupère un commentaire par son identifiant unique.
     * 
     * @param id L'identifiant du commentaire.
     * @return Le {@link CommentDTO} correspondant.
     * @throws EntityNotFoundException Si aucun commentaire n'existe avec cet ID.
     */
	public CommentDTO findByID(Long id){
		return CommentDTO.fromEntity(commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment non trouvé")));
	}
	
	/**
     * Récupère l'intégralité des commentaires stockés en base de données.
     * 
     * @return Une liste de tous les {@link CommentDTO}.
     */
	public List<CommentDTO> findAll(){
		return commentRepository.findAll().stream()
				.map(CommentDTO::fromEntity).toList();
	}
	
	/**
     * Récupère la liste des commentaires associés à un article spécifique.
     * <p>
     * Les résultats sont triés par date de création de manière décroissante 
     * (du plus récent au plus ancien).
     * </p>
     * 
     * @param postId L'identifiant de l'article (Post).
     * @return Une liste de {@link CommentDTO} liés à l'article.
     */
	public List<CommentDTO> findByPostId(Long postId){
		return commentRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
				.map(CommentDTO::fromEntity).toList();
	}
	
	/**
     * Crée et enregistre un nouveau commentaire.
     * <p>
     * Cette méthode récupère les entités Utilisateur et Article nécessaires, 
     * les associe au nouveau commentaire, puis sauvegarde l'ensemble en base de données.
     * </p>
     * 
     * @param commentDto Le DTO contenant le texte du commentaire.
     * @param postId     L'identifiant de l'article à commenter.
     * @param userId     L'identifiant de l'auteur du commentaire.
     * @return Le {@link CommentDTO} du commentaire nouvellement créé.
     * @throws EntityNotFoundException Si l'utilisateur ou l'article spécifié n'existe pas.
     */
	public CommentDTO create(CommentDTO commentDto, Long postId, Long userId) {
		
		
		Comment comment = new Comment();
		comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("utilisateur non trouvé")));
		comment.setContenu(commentDto.getContenu());
		comment.setPost(postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("utilisateur non trouvé")));
		return CommentDTO.fromEntity(commentRepository.save(comment));
	}
	
}
