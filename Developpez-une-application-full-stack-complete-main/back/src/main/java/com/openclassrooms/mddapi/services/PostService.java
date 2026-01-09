package com.openclassrooms.mddapi.services;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.repository.PostRepository;

@Service
public class PostService {
	
	@Autowired
	private PostRepository postRepository;
	
	public Post findByID(Long id){
		return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));
	}
	
	public List<Post> findAll(){
		return postRepository.findAll();
	}
	
	public List<Post> findByThemeId(List<Long> listThemesId){
		return postRepository.findByThemeIdIn(listThemesId);
	}
	
	public List<Post> findByThemeIdOrderByCreateAtAsc(List<Long> listThemesId){
		return postRepository.findByThemeIdIn(listThemesId,Sort.by(Sort.Direction.ASC, "createdAt"));
	}
	
	public Post create(Post post) {
		return postRepository.save(post);
	}
	
	public void delete(Post post) {
		postRepository.delete(post);
	}
	
	public Post update(Long id, Post post) {
		post.setId(id);
		return this.postRepository.save(post);
	}

}
