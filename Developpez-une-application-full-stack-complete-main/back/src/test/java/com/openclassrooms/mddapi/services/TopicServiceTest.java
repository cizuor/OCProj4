package com.openclassrooms.mddapi.services;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@SpringBootTest
@Transactional
class TopicServiceTest {
	
    private final TopicService themeService;

    private final TopicRepository themeRepository;

    private final UserRepository userRepository;
    
    
    
    
    @Autowired
    public TopicServiceTest(TopicService themeService, TopicRepository themeRepository, UserRepository userRepository) {
		super();
		this.themeService = themeService;
		this.themeRepository = themeRepository;
		this.userRepository = userRepository;
	}

	private Long userId;
    private Long themeJavaId;
    private Long themeAngularId;
    
    @BeforeEach
    void setUp() {
        Topic java = new Topic();
        java.setName("learn Java");
        java.setDescription("Cours sur Java");
        java = themeRepository.save(java);
        themeJavaId = java.getId();

        Topic angular = new Topic();
        angular.setName("learn Angular");
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
        TopicDTO result = themeService.findByID(themeJavaId, userId);

        // ASSERT
        assertThat(result.getTitle()).isEqualTo("learn Java");
        assertThat(result.isLiked()).isTrue();
    }

    @Test
    void findByID_ShouldReturnThemeWithIsLikedFalse_WhenUserIsNotSubscribed() {
    	// ACT
        TopicDTO result = themeService.findByID(themeAngularId, userId);

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
        Topic newTheme = new Topic();
        newTheme.setName("Spring Boot");
        newTheme.setDescription("Framework Java");

        // ACT
        TopicDTO created = themeService.create(TopicDTO.fromEntity(newTheme, Collections.emptyList()));

        // ASSERT
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Spring Boot");
        assertThat(created.isLiked()).isFalse();
    }

    @Test
    void findAll_ShouldReturnAllThemesWithCorrectLikeStatus() {
        // ACT
        List<TopicDTO> results = themeService.getTopics(userId);

        // ASSERT
        assertThat(results).hasSize(6); // 2 + 4 du data.sql
        
        TopicDTO javaDto = results.stream().filter(t -> t.getTitle().equals("learn Java")).findFirst().get();
        TopicDTO angularDto = results.stream().filter(t -> t.getTitle().equals("learn Angular")).findFirst().get();

        assertThat(javaDto.isLiked()).isTrue();
        assertThat(angularDto.isLiked()).isFalse();
    }

    @Test
    void findAll_ShouldReturnAllThemesWithLikedFalse_WhenUserIdIsNull() {
        // ACT
        List<TopicDTO> results = themeService.getTopics(null);

        // ASSERT
        assertThat(results).isNotEmpty().allMatch(t -> !t.isLiked());
    }
    
    @Test
    void getUserSubscriptions_ShouldReturnOnlySubscribedThemes() {
        // ACT
        List<TopicDTO> results = themeService.getUserSubscriptions(userId);

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
        List<TopicDTO> results = themeService.getUserSubscriptions(newUser.getId());

        // ASSERT
        assertThat(results).isEmpty();
    }

}
