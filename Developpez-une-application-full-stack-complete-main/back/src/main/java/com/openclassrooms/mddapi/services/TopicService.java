package com.openclassrooms.mddapi.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class TopicService {
	
	@Autowired
	private TopicRepository themeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	public TopicDTO findByID(Long id,Long userId){
		
		Collection<Long> likedIds = (userId != null) ? userRepository.findSubscribedTopicIdsByUserId(userId) : Collections.emptySet();
		
		return TopicDTO.fromEntity(themeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Theme non trouvé")),likedIds);
	}
	
	public TopicDTO create(TopicDTO themeDto) {
		Topic theme = new Topic();
		theme.setDescription(themeDto.getDescription());
		theme.setName(themeDto.getTitle());
		
		return TopicDTO.fromEntity(themeRepository.save(theme),Collections.emptySet());
	}
	
	public List<TopicDTO> getTopics(Long userId) {
        List<Topic> allThemes = themeRepository.findAll();

        Collection<Long> likedIds = (userId != null) ? userRepository.findSubscribedTopicIdsByUserId(userId) : Collections.emptySet();

        return allThemes.stream()
            .map(theme -> TopicDTO.fromEntity(theme, likedIds))
            .collect(Collectors.toList());
    }
	
	
	public List<TopicDTO> getUserSubscriptions(Long userId) {
		List<Topic> subscribedThemes = themeRepository.findByUsersId(userId);
	    
	    // On les transforme en DTO 
	    return subscribedThemes.stream()
	        .map(theme -> TopicDTO.fromEntity(theme, Collections.singletonList(theme.getId())))
	        .collect(Collectors.toList());
	}
}
