package com.openclassrooms.mddapi.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.models.Topic;

@Repository
public interface TopicRepository  extends JpaRepository<Topic, Long>{
	
	
	//sans la commande il cherche la colonne userID dans la table theme
	@Query("SELECT t FROM User u JOIN u.abonnements t WHERE u.id = :userId")
	List<Topic> findByUsersId(Long userId);
	
	Optional<Topic> findByTitle(String title);
}
