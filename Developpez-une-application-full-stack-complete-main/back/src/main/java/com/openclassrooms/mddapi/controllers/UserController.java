package com.openclassrooms.mddapi.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.payload.request.UpdateUserRequest;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.TopicService;
import com.openclassrooms.mddapi.services.UserService;

@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/utilisateur")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TopicService topicService;
	
	@GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
            UserDTO user = this.userService.getUserById(id);
            user.setEmail("hide@information.com");            
            return ResponseEntity.ok().body(user);
    }
	
	@GetMapping("/me")
	public ResponseEntity<UserDTO> getMyProfile() {
	    //On récupère l'identifiant (email/pseudo) depuis le contexte de sécurité
	    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
	                                     .getAuthentication().getPrincipal();
	    
	    UserDTO user = userService.getUserById(userDetails.getId());
	    
	    return ResponseEntity.ok(user);
	}
	
	@PostMapping("/subscribe/{topicId}")
	public ResponseEntity<List<TopicDTO>> PostSubTheme(@PathVariable("topicId") Long id) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		
		userService.subscribe(userDetails.getId(), id);
		
		List<TopicDTO> updatedThemes = topicService.getTopics(userDetails.getId());
		
		return ResponseEntity.ok(updatedThemes);
	}
	
	
	@PostMapping("/unsubscribe/{topicId}")
	public ResponseEntity<List<TopicDTO>> unsubscribe(@PathVariable("topicId") Long id) {
	    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
	            .getAuthentication().getPrincipal();
	    
	    userService.unsubscribe(userDetails.getId(), id);
	    
	    return ResponseEntity.ok(topicService.getTopics(userDetails.getId()));
	}
	
	@PutMapping("/me")
	public ResponseEntity<UserDTO> updateMyProfile(@Valid @RequestBody UpdateUserRequest updateRequest) {
	    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
	                                     .getAuthentication().getPrincipal();
	    
	    
	    
	    UserDTO updatedUser = userService.update(userDetails.getId(), updateRequest);
	    
	    return ResponseEntity.ok(updatedUser);
	}
	
	
	
	

}
