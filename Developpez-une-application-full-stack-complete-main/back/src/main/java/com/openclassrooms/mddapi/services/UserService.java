package com.openclassrooms.mddapi.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class UserService {
	
	 	@Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private TopicRepository themeRepository;

	    public UserDTO getUserById(Long id) {
	        User user = findEntityById(id);
	        return UserDTO.fromEntity(user);
	    }

	    
	    public User findEntityById(Long id) {
	        return userRepository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'id : " + id));
	    }

	    @Transactional
	    public UserDTO update(Long id, UserDTO userDTO) {
	        User user = findEntityById(id);
	        
	        user.setPseudo(userDTO.getPseudo());
	        user.setEmail(userDTO.getEmail());
	       
	        
	        User updatedUser = userRepository.save(user);
	        return UserDTO.fromEntity(updatedUser);
	    }


	    @Transactional
	    public void subscribe(Long userId, Long themeId) {
	        User user = findEntityById(userId);
	        Topic theme = themeRepository.findById(themeId)
	                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));

	        user.addAbo(theme);
	        userRepository.save(user);
	    }

	    @Transactional
	    public void unsubscribe(Long userId, Long themeId) {
	        User user = findEntityById(userId);
	        Topic theme = themeRepository.findById(themeId)
	                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));

	        user.removeAbo(theme);
	        userRepository.save(user);
	    }
}
