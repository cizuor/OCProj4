package com.openclassrooms.mddapi.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class PostService {
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;
	
	public PostDTO findByID(Long id){
		Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));
		
		return PostDTO.fromEntity(post);
	}
	
	public List<PostDTO> findAll(){
		return postRepository.findAll().stream()
				.map(PostDTO::fromEntity)
				.collect(Collectors.toList());
	}
	
	public List<PostDTO> findByThemeId(List<Long> listThemesId){
		return postRepository.findByThemeIdIn(listThemesId).stream()
				.map(PostDTO::fromEntity)
				.collect(Collectors.toList());
	}
	
	public List<PostDTO> findByThemeIdOrderByCreateAtAsc(List<Long> listThemesId){
		return postRepository.findByThemeIdIn(listThemesId,Sort.by(Sort.Direction.ASC, "createdAt")).stream()
				.map(PostDTO::fromEntity)
				.collect(Collectors.toList());
	}
	
	public PostDTO create(PostDTO postDto) {
		
		User user = userRepository.findById(postDto.getUserID())
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé"));
        Theme theme = themeRepository.findById(postDto.getThemeId())
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));
        
        Post post = new Post();
        post.setTitre(postDto.getTitre());
        post.setContenu(postDto.getContenu());
        post.setAuteur(user);
        post.setTheme(theme);
        
        Post savedPost = postRepository.save(post);
		
		return PostDTO.fromEntity(savedPost);
	}
	
	public void delete(Long id,Long userId) {
		Post post = postRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));
		
		if(!post.getAuteur().getId().equals(userId)) {
			throw new RuntimeException("Vous n'avez pas l'autorisation de modifier ce post");
		}
			postRepository.delete(post);
	}
	
	public PostDTO update(Long id, PostDTO postDto, Long userId) {
		
		Post existingPost = postRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));
			
		
		if (!existingPost.getAuteur().getId().equals(userId)) {
	        // Tu peux créer une exception personnalisée ou utiliser une standard
	        throw new RuntimeException("Vous n'avez pas l'autorisation de modifier ce post");
	    }
		
		
        existingPost.setTitre(postDto.getTitre());
        existingPost.setContenu(postDto.getContenu());
        
        
        if(!existingPost.getTheme().getId().equals(postDto.getThemeId())) {
            Theme theme = themeRepository.findById(postDto.getThemeId())
                .orElseThrow(() -> new EntityNotFoundException("Thème non trouvé"));
            existingPost.setTheme(theme);
        }
        return PostDTO.fromEntity(postRepository.save(existingPost));
	}

}
