package com.openclassrooms.mddapi.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.openclassrooms.mddapi.models.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

	private Long id;
	
	@NotBlank(message = "Le contenu ne peut pas être vide")
    @Size(max = 500)
	private String contenu;
	private String authorName;
	private Long authorId;
	private Long postId;
	private LocalDateTime createdAt;
	
	// factory de creation de DTO
	public static CommentDTO fromEntity(Comment comment) {
		if(comment == null) return null;
		
		return new CommentDTO(
				comment.getId(),
				comment.getContenu(),
				comment.getAuthor() != null ? comment.getAuthor().getPseudo() : null, 
				comment.getAuthor() != null ? comment.getAuthor().getId() : null, 
				comment.getPost() != null ? comment.getPost().getId() : null,
				comment.getCreatedAt()
				);
	}
}
