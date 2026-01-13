package com.openclassrooms.mddapi.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class SignUpRequest {
	
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String pseudo;
	
	@NotBlank
	@Size(min = 6, max = 40)
	private String password;

}
