package com.openclassrooms.mddapi.dto;


import java.util.Collection;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

import com.openclassrooms.mddapi.models.Topic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {
	private Long id;
	@NotBlank(message = "Le titre ne peut pas être vide")
	private String title;
    private String description;
    private boolean isLiked;
    
    
    public static TopicDTO fromEntity(Topic theme, Collection<Long> subscribedThemeIds) {
    	if (theme == null) return null;
    	
    	return new TopicDTO(
    		theme.getId(),
    		theme.getName(),
    		theme.getDescription(),
    		subscribedThemeIds != null && subscribedThemeIds.contains(theme.getId())
    		);

    }

}
