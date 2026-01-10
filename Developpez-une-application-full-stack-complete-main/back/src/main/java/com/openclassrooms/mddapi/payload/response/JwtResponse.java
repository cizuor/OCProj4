package com.openclassrooms.mddapi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class JwtResponse {
	private String token;
	private String pseudo;
	private Long id;
	private String type = "Bearer";
	
	public JwtResponse(String accessToken, Long id, String username) {
        this.token = accessToken;
        this.id = id;
        this.pseudo = username;
    }
	
}
