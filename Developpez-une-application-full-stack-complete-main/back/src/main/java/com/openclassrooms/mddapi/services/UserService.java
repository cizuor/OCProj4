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

@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
	
    private final UserRepository userRepository;

    private final TopicRepository topicRepository;
	    
	    
	    

    public UserService(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository,
				TopicRepository topicRepository) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.topicRepository = topicRepository;
	}

    public UserDTO getUserById(Long id) {
        User user = findEntityById(id);
        return UserDTO.fromEntity(user);
    }

    
    public User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'id : " + id));
    }

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


    @Transactional
    public void subscribe(Long userId, Long topicId) {
        User user = findEntityById(userId);
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));

        user.addAbo(topic);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void unsubscribe(Long userId, Long topicId) {
        User user = findEntityById(userId);
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));

        user.removeAbo(topic);
        userRepository.saveAndFlush(user);
    }
}
