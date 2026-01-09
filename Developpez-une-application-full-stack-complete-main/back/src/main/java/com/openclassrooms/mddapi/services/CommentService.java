package com.openclassrooms.mddapi.services;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	public Comment findByID(Long id){
		return commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment non trouvé"));
	}
	
	public List<Comment> findAll(){
		return commentRepository.findAll();
	}
	
	public List<Comment> findByPostId(Long postId){
		return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
	}
	
	public Comment create(Comment comment) {
		return commentRepository.save(comment);
	}
	
}
