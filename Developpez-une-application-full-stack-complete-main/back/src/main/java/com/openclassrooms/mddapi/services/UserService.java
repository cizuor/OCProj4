package com.openclassrooms.mddapi.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.exception.BadRequestException;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.payload.request.UpdateUserRequest;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

/**
 * Service gérant la logique métier liée aux utilisateurs.
 * <p>
 * Ce service permet de gérer les informations de profil (consultation et mise à jour)
 * ainsi que le système d'abonnement aux thématiques.
 * </p>
 */
@Service
public class UserService {

	/** Encodeur utilisé pour sécuriser les mots de passe avant persistance. */
    private final BCryptPasswordEncoder passwordEncoder;
	
    /** Repository pour l'accès aux données utilisateurs. */
    private final UserRepository userRepository;

    /** Repository pour l'accès aux données des thématiques. */
    private final TopicRepository topicRepository;
	    
	    
	    
    /**
     * Constructeur pour l'injection de dépendances par constructeur.
     * 
     * @param passwordEncoder L'encodeur de mot de passe.
     * @param userRepository  Le répository utilisateur.
     * @param topicRepository Le répository des thématiques.
     */
    public UserService(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository,
				TopicRepository topicRepository) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.topicRepository = topicRepository;
	}

    /**
     * Récupère un utilisateur et le convertit en DTO pour l'exposition API.
     * 
     * @param id L'identifiant de l'utilisateur.
     * @return Le {@link UserDTO} correspondant.
     * @throws EntityNotFoundException Si l'utilisateur n'est pas trouvé.
     */
    public UserDTO getUserById(Long id) {
        User user = findEntityById(id);
        return UserDTO.fromEntity(user);
    }

    /**
     * Recherche l'entité JPA d'un utilisateur.
     * <p>
     * Cette méthode est principalement utilisée en interne par d'autres services 
     * nécessitant l'objet {@link User} complet pour des opérations relationnelles.
     * </p>
     * 
     * @param id L'identifiant de l'utilisateur.
     * @return L'entité {@link User}.
     * @throws EntityNotFoundException Si l'identifiant ne correspond à aucun utilisateur.
     */
    public User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'id : " + id));
    }
    
    /**
     * Met à jour les informations de profil de l'utilisateur.
     * <p>
     * Cette méthode permet de modifier le pseudo et l'email. Si un nouveau mot de passe 
     * est fourni, il est validé (8 caractères min.) et haché avant sauvegarde.
     * </p>
     * 
     * @param id  L'identifiant de l'utilisateur à modifier.
     * @param req Objet {@link UpdateUserRequest} contenant les nouvelles données.
     * @return Le {@link UserDTO} mis à jour.
     * @throws BadRequestException Si le mot de passe fourni est trop court.
     * @throws EntityNotFoundException Si l'utilisateur n'existe pas.
     */
    @Transactional
    public UserDTO update(Long id, UpdateUserRequest req) {
        User user = findEntityById(id);
        
        user.setPseudo(req.getPseudo());
        user.setEmail(req.getEmail());
        
        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
        	if (req.getPassword().length() < 8) {
                throw new BadRequestException("Le mot de passe doit faire au moins 8 caractères");
            }
        	user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
       
        
        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }

    /**
     * Abonne un utilisateur à une thématique.
     * <p>
     * Utilise {@code saveAndFlush} pour garantir que la table de jointure est mise à jour 
     * immédiatement, permettant une lecture cohérente dans la foulée.
     * </p>
     * 
     * @param userId  L'identifiant de l'utilisateur.
     * @param topicId L'identifiant de la thématique (Topic).
     * @throws EntityNotFoundException Si l'utilisateur ou le thème n'existe pas.
     */
    @Transactional
    public void subscribe(Long userId, Long topicId) {
        User user = findEntityById(userId);
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));

        user.addAbo(topic);
        userRepository.saveAndFlush(user);
    }

    /**
     * Désabonne un utilisateur d'une thématique.
     * 
     * @param userId  L'identifiant de l'utilisateur.
     * @param topicId L'identifiant de la thématique (Topic).
     * @throws EntityNotFoundException Si l'utilisateur ou le thème n'existe pas.
     */
    @Transactional
    public void unsubscribe(Long userId, Long topicId) {
        User user = findEntityById(userId);
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));

        user.removeAbo(topic);
        userRepository.saveAndFlush(user);
    }
}
