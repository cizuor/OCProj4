package com.openclassrooms.mddapi.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
