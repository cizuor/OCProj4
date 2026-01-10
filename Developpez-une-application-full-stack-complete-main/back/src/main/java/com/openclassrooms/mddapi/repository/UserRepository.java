package com.openclassrooms.mddapi.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	// on ne recup que les id des theme pour ne pas alourdir inutilement le chargement 
	@Query("SELECT t.id FROM User u JOIN u.abonnements t WHERE u.id = :userId")
	Set<Long> findSubscribedThemeIdsByUserId(@Param("userId") Long userId);
	
	
	 Optional<User> findByEmailOrPseudo(String email, String pseudo);
}
