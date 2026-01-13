package com.openclassrooms.mddapi.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;
import com.openclassrooms.mddapi.services.PostService;
import com.openclassrooms.mddapi.services.TopicService;

@CrossOrigin // pour autoriser les requette venant de 4200 a atteindre 8080
@RestController
@RequestMapping("/api/article")
public class PostController {
	
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private TopicService themeService;


	
	@GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
            PostDTO post = this.postService.findByID(id);         
            return ResponseEntity.ok().body(post);
    
	}
	
	
	@GetMapping("/feed")
	public ResponseEntity<List<PostDTO>> getFeed(@RequestParam(defaultValue = "desc") String sort){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		
		List<TopicDTO> lTheme =  themeService.getUserSubscriptions(userDetails.getId());
		
		if (lTheme.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
		
		List<Long> lThemeId = lTheme.stream()
	            .map(TopicDTO::getId) 
	            .collect(Collectors.toList());
		if(sort.equals("desc")) {		
			return ResponseEntity.ok().body(postService.findByThemeId(lThemeId));
		}else {
			return ResponseEntity.ok().body(postService.findByThemeIdOrderByCreateAtAsc(lThemeId));
		}
		
	}
	
	
	@GetMapping("")
	public ResponseEntity<List<PostDTO>> getAll(){
		return ResponseEntity.ok().body(postService.findAll());
	}
	
	
	
	@PostMapping()
	public ResponseEntity<?> create(@Valid @RequestBody PostDTO postDto ){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		postDto.setUserID(userDetails.getId()); // on ne crée pas un post au nom d'un autre
		return ResponseEntity.ok().body(postService.create(postDto));
	}
	
	
	@PutMapping("{id}")
	public ResponseEntity<?> create(@PathVariable("id") Long id,@Valid @RequestBody PostDTO postDto ){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		postDto.setUserID(userDetails.getId()); // on ne modifie pas un post au nom d'un autre
		return ResponseEntity.ok().body(postService.update(id,postDto,userDetails.getId()));
	}
	
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
		
		this.postService.delete(id,userDetails.getId());
		return ResponseEntity.ok().build();
	}
	
}
