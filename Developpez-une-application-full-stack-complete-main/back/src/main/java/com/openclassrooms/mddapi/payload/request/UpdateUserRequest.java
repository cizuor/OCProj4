package com.openclassrooms.mddapi.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UpdateUserRequest {
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String pseudo;

}
