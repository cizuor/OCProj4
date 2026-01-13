package com.openclassrooms.mddapi.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UpdateUserRequest {
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String pseudo;

}
