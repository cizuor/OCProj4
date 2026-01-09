package com.openclassrooms.mddapi.services;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.mddapi.dto.ThemeDTO;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@SpringBootTest
@Transactional
public class ThemeServiceTest {
	
	@Autowired
    private ThemeService themeService;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserRepository userRepository;
    
    
    private Long userId;
    private Long themeJavaId;
    private Long themeAngularId;
    
    @BeforeEach
    void setUp() {
        Theme java = new Theme();
        java.setTitle("learn Java");
        java.setDescription("Cours sur Java");
        java = themeRepository.save(java);
        themeJavaId = java.getId();

        Theme angular = new Theme();
        angular.setTitle("learn Angular");
        angular.setDescription("Cours sur Angular");
        angular = themeRepository.save(angular);
        themeAngularId = angular.getId();

        User user = new User();
        user.setEmail("user@test.com");
        user.setPseudo("UserTest");
        user.setPassword("password");
        
        user.addAbo(java);
        user = userRepository.save(user);
        userId = user.getId();
    }

    @Test
    void findByID_ShouldReturnThemeWithIsLikedTrue_WhenUserIsSubscribed() {
    	// ACT
        ThemeDTO result = themeService.findByID(themeJavaId, userId);

        // ASSERT
        assertThat(result.getTitle()).isEqualTo("learn Java");
        assertThat(result.isLiked()).isTrue();
    }

    @Test
    void findByID_ShouldReturnThemeWithIsLikedFalse_WhenUserIsNotSubscribed() {
    	// ACT
        ThemeDTO result = themeService.findByID(themeAngularId, userId);

        // ASSERT
        assertThat(result.getTitle()).isEqualTo("learn Angular");
        assertThat(result.isLiked()).isFalse();
    }

    @Test
    void findByID_ShouldThrowException_WhenThemeNotFound() {
    	// ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> {
            themeService.findByID(999L, userId);
        });
    }

    @Test
    void create_ShouldSaveAndReturnDTO() {
    	// ARRANGE
        Theme newTheme = new Theme();
        newTheme.setTitle("Spring Boot");
        newTheme.setDescription("Framework Java");

        // ACT
        ThemeDTO created = themeService.create(newTheme);

        // ASSERT
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Spring Boot");
        assertThat(created.isLiked()).isFalse();
    }

    @Test
    void findAll_ShouldReturnAllThemesWithCorrectLikeStatus() {
        // ACT
        List<ThemeDTO> results = themeService.findAll(userId);

        // ASSERT
        assertThat(results).hasSize(6); // 2 + 4 du data.sql
        
        ThemeDTO javaDto = results.stream().filter(t -> t.getTitle().equals("learn Java")).findFirst().get();
        ThemeDTO angularDto = results.stream().filter(t -> t.getTitle().equals("learn Angular")).findFirst().get();

        assertThat(javaDto.isLiked()).isTrue();
        assertThat(angularDto.isLiked()).isFalse();
    }

    @Test
    void findAll_ShouldReturnAllThemesWithLikedFalse_WhenUserIdIsNull() {
        // ACT
        List<ThemeDTO> results = themeService.findAll(null);

        // ASSERT
        assertThat(results).hasSize(6); // 2 + 4 du data.sql
        assertThat(results).allMatch(t -> !t.isLiked());
    }
    
    @Test
    void getUserSubscriptions_ShouldReturnOnlySubscribedThemes() {
        // ACT
        List<ThemeDTO> results = themeService.getUserSubscriptions(userId);

        // ASSERT
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("learn Java");
        assertThat(results.get(0).isLiked()).isTrue();
    }

    @Test
    void getUserSubscriptions_ShouldReturnEmptyList_WhenUserHasNoSubscriptions() {
        // ARRANGE
        User newUser = new User();
        newUser.setEmail("lonely@test.com");
        newUser.setPseudo("NoAbo");
        newUser.setPassword("pwd");
        newUser = userRepository.save(newUser);

        // ACT
        List<ThemeDTO> results = themeService.getUserSubscriptions(newUser.getId());

        // ASSERT
        assertThat(results).isEmpty();
    }

}
