package com.openclassrooms.mddapi.repository;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.models.Theme;

@Repository
public interface ThemeRepository  extends JpaRepository<Theme, Long>{
	List<Theme> findByUsersId(Long userId);
}
