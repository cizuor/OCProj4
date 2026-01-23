package com.openclassrooms.mddapi.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class CommentService {

	private final CommentRepository commentRepository;
	
	private final UserRepository userRepository;
	
	private final PostRepository postRepository;
		
	
	public CommentService(CommentRepository commentRepository, UserRepository userRepository,
			PostRepository postRepository) {
		super();
		this.commentRepository = commentRepository;
		this.userRepository = userRepository;
		this.postRepository = postRepository;
	}

	public CommentDTO findByID(Long id){
		return CommentDTO.fromEntity(commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment non trouvé")));
	}
	
	public List<CommentDTO> findAll(){
		return commentRepository.findAll().stream()
				.map(CommentDTO::fromEntity).toList();
	}
	
	public List<CommentDTO> findByPostId(Long postId){
		return commentRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
				.map(CommentDTO::fromEntity).toList();
	}
	
	public CommentDTO create(CommentDTO commentDto, Long postId, Long userId) {
		
		
		Comment comment = new Comment();
		comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("utilisateur non trouvé")));
		comment.setContenu(commentDto.getContenu());
		comment.setPost(postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("utilisateur non trouvé")));
		return CommentDTO.fromEntity(commentRepository.save(comment));
	}
	
}
