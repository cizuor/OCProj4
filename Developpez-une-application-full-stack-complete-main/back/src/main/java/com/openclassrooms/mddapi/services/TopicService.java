package com.openclassrooms.mddapi.services;

import java.util.Collection;
import java.util.List;
import java.util.Collections;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;


/**
 * Service gérant la logique métier des thématiques (Topics).
 * <p>
 * Ce service permet de gérer le catalogue des thèmes et les interactions 
 * d'abonnement des utilisateurs. Il assure la transformation des entités 
 * en DTO incluant l'état de suivi (isLiked) personnalisé pour chaque utilisateur.
 * </p>
 */
@Service
public class TopicService {
	
	/** Repository pour l'accès aux données des thématiques. */
	private final TopicRepository themeRepository;
	
	/** Repository pour l'accès aux données des utilisateurs et de leurs abonnements. */
	private final UserRepository userRepository;
	
	/**
     * Constructeur pour l'injection de dépendances par constructeur.
     * 
     * @param themeRepository Le répository des thématiques.
     * @param userRepository  Le répository des utilisateurs.
     */
	public TopicService(TopicRepository themeRepository, UserRepository userRepository) {
		super();
		this.themeRepository = themeRepository;
		this.userRepository = userRepository;
	}

	/**
     * Récupère un thème spécifique et détermine si l'utilisateur y est abonné.
     * 
     * @param id     L'identifiant du thème.
     * @param userId L'identifiant de l'utilisateur effectuant la requête.
     * @return Le {@link TopicDTO} incluant le statut d'abonnement.
     * @throws EntityNotFoundException Si le thème n'existe pas.
     */
	public TopicDTO findByID(Long id,Long userId){
		
		Collection<Long> likedIds = (userId != null) ? userRepository.findSubscribedTopicIdsByUserId(userId) : Collections.emptySet();
		
		return TopicDTO.fromEntity(themeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Theme non trouvé")),likedIds);
	}
	
	/**
     * Crée une nouvelle thématique en base de données.
     * 
     * @param themeDto Le DTO contenant les informations du nouveau thème.
     * @return Le {@link TopicDTO} créé.
     */
	public TopicDTO create(TopicDTO themeDto) {
		Topic theme = new Topic();
		theme.setDescription(themeDto.getDescription());
		theme.setName(themeDto.getTitle());
		
		return TopicDTO.fromEntity(themeRepository.save(theme),Collections.emptySet());
	}
	
	/**
     * Récupère la liste de tous les thèmes avec le statut d'abonnement 
     * pour un utilisateur donné.
     * 
     * @param userId L'identifiant de l'utilisateur connecté.
     * @return Une liste de {@link TopicDTO} enrichis.
     */
	public List<TopicDTO> getTopics(Long userId) {
        List<Topic> allThemes = themeRepository.findAll();

        Collection<Long> likedIds = (userId != null) ? userRepository.findSubscribedTopicIdsByUserId(userId) : Collections.emptySet();

        return allThemes.stream()
            .map(theme -> TopicDTO.fromEntity(theme, likedIds)).toList();
    }
	
	/**
     * Récupère uniquement les thématiques suivies par un utilisateur spécifique.
     * <p>
     * Cette méthode filtre les résultats directement en base de données pour optimiser 
     * les performances. Le champ "liked" des DTO résultants sera systématiquement à "true".
     * </p>
     * 
     * @param userId L'identifiant de l'utilisateur dont on veut les abonnements.
     * @return Une liste de {@link TopicDTO} représentant les thèmes suivis.
     */
	public List<TopicDTO> getUserSubscriptions(Long userId) {
		List<Topic> subscribedThemes = themeRepository.findByUsersId(userId);
	    
	    // On les transforme en DTO 
	    return subscribedThemes.stream()
	        .map(theme -> TopicDTO.fromEntity(theme, Collections.singletonList(theme.getId()))).toList();
	}
}
