package com.openclassrooms.mddapi.repository;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.models.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	
	List<Post> findByThemeIdIn(Collection<Long> themeIds, Sort sort);
	
	//pour ne pas obliger a utiliser le param sort
	default List<Post> findByThemeIdIn(Collection<Long> themeIds) {
        return findByThemeIdIn(themeIds, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

}
