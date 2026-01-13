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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.dto.ThemeDTO;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.PostService;
import com.openclassrooms.mddapi.services.ThemeService;

@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/theme")
public class ThemeController {
	
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private ThemeService themeService;

	
	
	@GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        ThemeDTO theme = this.themeService.findByID(id,userDetails.getId());         
        return ResponseEntity.ok().body(theme);
	}
	
	@GetMapping()
    public ResponseEntity<List<ThemeDTO>> getAll() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		List<ThemeDTO> lTheme = this.themeService.findAll(userDetails.getId());         
        return ResponseEntity.ok().body(lTheme);
	}
	
	@GetMapping("/suivie")
    public ResponseEntity<List<ThemeDTO>> getAllLiked() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		List<ThemeDTO> lTheme = this.themeService.getUserSubscriptions(userDetails.getId());         
        return ResponseEntity.ok().body(lTheme);
	}
	
	
	@PostMapping()
	public ResponseEntity<?> create(@Valid @RequestBody ThemeDTO themeDto ){
		return ResponseEntity.ok().body(themeService.create(themeDto));
	}
	
	

}
