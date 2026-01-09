package com.openclassrooms.mddapi.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.ThemeDTO;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class ThemeService {
	
	@Autowired
	private ThemeRepository themeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	public ThemeDTO findByID(Long id,Long userId){
		
		Collection<Long> likedIds = (userId != null) ? userRepository.findSubscribedThemeIdsByUserId(userId) : Collections.emptySet();
		
		return ThemeDTO.fromEntity(themeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Theme non trouvé")),likedIds);
	}
	
	public ThemeDTO create(Theme theme) {
		return ThemeDTO.fromEntity(themeRepository.save(theme),Collections.emptySet());
	}
	
	public List<ThemeDTO> findAll(Long userId) {
        List<Theme> allThemes = themeRepository.findAll();

        Collection<Long> likedIds = (userId != null) ? userRepository.findSubscribedThemeIdsByUserId(userId) : Collections.emptySet();

        return allThemes.stream()
            .map(theme -> ThemeDTO.fromEntity(theme, likedIds))
            .collect(Collectors.toList());
    }
	
	
	public List<ThemeDTO> getUserSubscriptions(Long userId) {
		List<Theme> themes = themeRepository.findAll();
	    // On récupère les ID des theme suivie par utilisateur
	    Set<Long> subscribedThemes = userRepository.findSubscribedThemeIdsByUserId(userId); 
	    
	    // On les transforme en DTO 
	    return themes.stream()
	        .map(theme -> ThemeDTO.fromEntity(theme, subscribedThemes))
	        .collect(Collectors.toList());
	}
	
	

}
