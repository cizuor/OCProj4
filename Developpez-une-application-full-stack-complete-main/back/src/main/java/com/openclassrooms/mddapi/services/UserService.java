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
	    private TopicRepository topicRepository;

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
