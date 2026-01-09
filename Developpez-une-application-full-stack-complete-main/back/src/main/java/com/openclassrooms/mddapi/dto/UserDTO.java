package com.openclassrooms.mddapi.dto;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import com.openclassrooms.mddapi.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private Long id;
	
	@NotBlank(message = "L'email ne peut pas être vide")
	private String email;
	
	@NotBlank(message = "le pseudo ne peut pas être vide")
	private String pseudo;
	
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	
	public static UserDTO fromEntity(User user) {	
		if (user == null) return null;
		return new UserDTO(
			user.getId(),
			user.getEmail(),
			user.getPseudo(),
			user.getCreatedAt(),
			user.getUpdatedAt()
			);
	}
	
	
}
