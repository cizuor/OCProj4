package com.openclassrooms.mddapi.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	public CommentDTO findByID(Long id){
		return CommentDTO.fromEntity(commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment non trouvé")));
	}
	
	public List<CommentDTO> findAll(){
		return commentRepository.findAll().stream()
				.map(CommentDTO::fromEntity)
				.collect(Collectors.toList());
	}
	
	public List<CommentDTO> findByPostId(Long postId){
		return commentRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
				.map(CommentDTO::fromEntity)
				.collect(Collectors.toList());
	}
	
	public CommentDTO create(CommentDTO commentDto, Long postId, Long userId) {
		
		
		Comment comment = new Comment();
		comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("utilisateur non trouvé")));
		comment.setContenu(commentDto.getContenu());
		comment.setPost(postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("utilisateur non trouvé")));
		return CommentDTO.fromEntity(commentRepository.save(comment));
	}
	
}
