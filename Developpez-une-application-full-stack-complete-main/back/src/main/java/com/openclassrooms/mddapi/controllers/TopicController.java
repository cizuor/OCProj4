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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.PostService;
import com.openclassrooms.mddapi.services.TopicService;

@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/topic")
public class TopicController {
	
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private TopicService topicService;

	
	
	@GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        TopicDTO theme = this.topicService.findByID(id,userDetails.getId());         
        return ResponseEntity.ok().body(theme);
	}
	
	@GetMapping()
    public ResponseEntity<List<TopicDTO>> getAll() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		List<TopicDTO> lTheme = this.topicService.getTopics(userDetails.getId());         
        return ResponseEntity.ok().body(lTheme);
	}
	
	@GetMapping("/suivie")
    public ResponseEntity<List<TopicDTO>> getAllLiked() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		List<TopicDTO> lTheme = this.topicService.getUserSubscriptions(userDetails.getId());         
        return ResponseEntity.ok().body(lTheme);
	}
	
	
	@PostMapping()
	public ResponseEntity<?> create(@Valid @RequestBody TopicDTO themeDto ){
		return ResponseEntity.ok().body(topicService.create(themeDto));
	}
	
	

}
