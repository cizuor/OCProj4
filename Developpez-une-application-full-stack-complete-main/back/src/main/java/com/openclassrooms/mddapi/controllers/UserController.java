package com.openclassrooms.mddapi.controllers;

import java.util.List;

import javax.validation.Valid;

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

import com.openclassrooms.mddapi.dto.ThemeDTO;
import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.payload.request.UpdateUserRequest;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.ThemeService;
import com.openclassrooms.mddapi.services.UserService;

@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/utilisateur")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ThemeService themeService;
	
	@GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
			// todo faire un userpublicDTO pour ne pas laisser la possibilité d'obtenir les mail des utilisateur
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
	
	@PostMapping("/subscribe/{themeId}")
	public ResponseEntity<List<ThemeDTO>> PostSubTheme(@PathVariable("themeId") Long id) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		
		userService.subscribe(userDetails.getId(), id);
		
		List<ThemeDTO> updatedThemes = themeService.findAll(userDetails.getId());
		
		return ResponseEntity.ok(updatedThemes);
	}
	
	
	@PostMapping("/unsubscribe/{themeId}")
	public ResponseEntity<List<ThemeDTO>> unsubscribe(@PathVariable("themeId") Long id) {
	    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
	            .getAuthentication().getPrincipal();
	    
	    userService.unsubscribe(userDetails.getId(), id);
	    
	    return ResponseEntity.ok(themeService.findAll(userDetails.getId()));
	}
	
	@PutMapping("/me")
	public ResponseEntity<UserDTO> updateMyProfile(@Valid @RequestBody UpdateUserRequest updateRequest) {
	    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
	                                     .getAuthentication().getPrincipal();
	    
	    
	    UserDTO userDto = new UserDTO();
	    userDto.setPseudo(updateRequest.getPseudo());
	    userDto.setEmail(updateRequest.getEmail());
	    
	    UserDTO updatedUser = userService.update(userDetails.getId(), userDto);
	    
	    return ResponseEntity.ok(updatedUser);
	}
	
	
	
	

}
