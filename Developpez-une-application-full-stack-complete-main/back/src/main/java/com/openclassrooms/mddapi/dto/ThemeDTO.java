package com.openclassrooms.mddapi.dto;


import java.util.Collection;
import java.util.Set;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import com.openclassrooms.mddapi.models.Theme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeDTO {
	private Long id;
	@NotBlank(message = "Le titre ne peut pas être vide")
	private String title;
    private String description;
    private boolean isLiked;
    
    
    public static ThemeDTO fromEntity(Theme theme, Collection<Long> subscribedThemeIds) {
    	if (theme == null) return null;
    	
    	return new ThemeDTO(
    		theme.getId(),
    		theme.getTitle(),
    		theme.getDescription(),
    		subscribedThemeIds != null && subscribedThemeIds.contains(theme.getId())
    		);

    }

}
