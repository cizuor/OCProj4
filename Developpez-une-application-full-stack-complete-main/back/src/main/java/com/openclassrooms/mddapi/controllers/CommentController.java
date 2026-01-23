package com.openclassrooms.mddapi.controllers;

import java.util.List;

import jakarta.validation.Valid;

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
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.CommentService;

@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/commentaire")
public class CommentController {

	
	private final CommentService commentService;
	
	
	public CommentController( CommentService commentService) {
		this.commentService = commentService;
	}


	@GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getById(@PathVariable("id") Long id) {
            CommentDTO comment = this.commentService.findByID(id);         
            return ResponseEntity.ok().body(comment);
    
	}
	
	
	@GetMapping("/article/{id}")
	public ResponseEntity<List<CommentDTO>> getByPost(@PathVariable("id") Long postId){
		
		List<CommentDTO> lComment =  commentService.findByPostId(postId);
		return ResponseEntity.ok().body(lComment);
	}
	
	
	@PostMapping("/article/{id}")
	public ResponseEntity<CommentDTO> create(@Valid @RequestBody CommentDTO commentDto,@PathVariable("id") Long postId){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		
		return ResponseEntity.ok().body(commentService.create(commentDto, postId, userDetails.getId()));
	}

}
