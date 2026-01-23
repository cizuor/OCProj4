package com.openclassrooms.mddapi.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class PostService {
	
	private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final TopicRepository themeRepository;
    
    private String strPostNotFound = "Post non trouvé";
    
    
	
	public PostService(PostRepository postRepository, UserRepository userRepository, TopicRepository themeRepository) {
		super();
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.themeRepository = themeRepository;
	}

	public PostDTO findByID(Long id){
		Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(strPostNotFound));
		
		return PostDTO.fromEntity(post);
	}
	
	public List<PostDTO> findAll(){
		return postRepository.findAll().stream()
				.map(PostDTO::fromEntity).toList();
	}
	
	public List<PostDTO> findByThemeId(List<Long> listThemesId){
		return postRepository.findByTopicIdIn(listThemesId).stream()
				.map(PostDTO::fromEntity).toList();
	}
	
	public List<PostDTO> findByThemeIdOrderByCreateAtAsc(List<Long> listThemesId){
		return postRepository.findByTopic_IdIn(listThemesId,Sort.by(Sort.Direction.ASC, "createdAt")).stream()
				.map(PostDTO::fromEntity).toList();
	}
	
	public PostDTO create(PostDTO postDto) {
		
		User user = userRepository.findById(postDto.getUserID())
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé"));
        Topic theme = themeRepository.findById(postDto.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));
        
        Post post = new Post();
        post.setTitre(postDto.getTitre());
        post.setContenu(postDto.getContenu());
        post.setAuteur(user);
        post.setTopic(theme);
        
        Post savedPost = postRepository.save(post);
		
		return PostDTO.fromEntity(savedPost);
	}
	
	public void delete(Long id,Long userId) {
		Post post = postRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException(strPostNotFound));
		
		if(!post.getAuteur().getId().equals(userId)) {
			throw new AccessDeniedException("Vous n'avez pas l'autorisation de modifier ce post");
		}
			postRepository.delete(post);
	}
	
	public PostDTO update(Long id, PostDTO postDto, Long userId) {
		
		Post existingPost = postRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException(strPostNotFound));
			
		
		if (!existingPost.getAuteur().getId().equals(userId)) {
			 throw new AccessDeniedException("Vous n'avez pas l'autorisation de modifier ce post");
	    }
		
		
        existingPost.setTitre(postDto.getTitre());
        existingPost.setContenu(postDto.getContenu());
        
        
        if(!existingPost.getTopic().getId().equals(postDto.getTopicId())) {
            Topic theme = themeRepository.findById(postDto.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));
            existingPost.setTopic(theme);
        }
        return PostDTO.fromEntity(postRepository.save(existingPost));
	}

}
