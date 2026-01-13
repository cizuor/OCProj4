package com.openclassrooms.mddapi.dto;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.openclassrooms.mddapi.models.Post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
	private Long id;
	
	private String topicName;
	@NotNull(message = "Il doit y avoir un theme")
	private Long topicId;
	
	private String userName;
	private Long userID;
	
	@NotBlank(message = "Il doit y avoir un Titre")
	private String titre;
	
	@NotBlank(message = "Le contenu ne peut pas être vide")
	private String contenu;
	
	private LocalDateTime createdAt;
	
	
	// factory de creation de DTO
	public static PostDTO fromEntity(Post post) {
		if(post == null) return null;
		
		return new PostDTO(
				post.getId(),
				post.getTopic() != null ? post.getTopic().getName() : null,
				post.getTopic() != null ? post.getTopic().getId() : null,
				post.getAuteur() != null ? post.getAuteur().getPseudo() : null,
				post.getAuteur() != null ? post.getAuteur().getId() : null,
				post.getTitre(),
				post.getContenu(),
				post.getCreatedAt()
				);
	}
}
