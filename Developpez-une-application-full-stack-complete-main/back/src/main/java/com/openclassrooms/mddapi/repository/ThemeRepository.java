package com.openclassrooms.mddapi.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.models.Theme;

@Repository
public interface ThemeRepository  extends JpaRepository<Theme, Long>{
	
	
	//sans la commande il cherche la colonne userID dans la table theme
	@Query("SELECT t FROM User u JOIN u.abonnements t WHERE u.id = :userId")
	List<Theme> findByUsersId(Long userId);
	
	Optional<Theme> findByTitle(String title);
}
