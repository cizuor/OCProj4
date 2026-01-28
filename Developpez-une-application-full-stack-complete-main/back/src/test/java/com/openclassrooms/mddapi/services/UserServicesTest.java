package com.openclassrooms.mddapi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.exception.BadRequestException;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.payload.request.UpdateUserRequest;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@SpringBootTest
@Transactional // Très important : annule les modifs en BDD après chaque test
class UserServicesTest {
	
	@Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository themeRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    
    private User testUser;
    private Topic testTheme;

    @BeforeEach
    void setUp() {
        // Nettoyage
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setPseudo("TestUser");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);

        testTheme = new Topic();
        testTheme.setName("Spring Boot IT");
        testTheme.setDescription("Test d'intégration");
        testTheme = themeRepository.save(testTheme);
    }

    @Test
    void findByID_ShouldReturnUserDTO_WhenUserExists() {
    	// ACT
        UserDTO result = userService.getUserById(testUser.getId());

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getPseudo()).isEqualTo("TestUser");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void findByID_ShouldThrowException_WhenUserDoesNotExist() {
    	// ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void update_ShouldModifyUser_WhenValidData() {
    	// ARRANGE
        UpdateUserRequest updateInfo = new UpdateUserRequest();
        updateInfo.setPseudo("NewPseudo");
        updateInfo.setEmail("new@test.com");

        // ACT
        UserDTO result = userService.update(testUser.getId(), updateInfo);

        // ASSERT
        assertThat(result.getPseudo()).isEqualTo("NewPseudo");
        assertThat(result.getEmail()).isEqualTo("new@test.com");
        
        // Vérification user non dto
        User inDb = userRepository.findById(testUser.getId()).get();
        assertThat(inDb.getPseudo()).isEqualTo("NewPseudo");
    }
    
    @Test
    void update_ShouldChangePassword_WhenPasswordIsValid() {
        // ARRANGE
        UpdateUserRequest req = new UpdateUserRequest();
        req.setPseudo("newPseudo");
        req.setEmail("test@test.com");
        req.setPassword("newPassword123"); // Mot de passe valide

        // ACT
        userService.update(testUser.getId(), req);

        // ASSERT
        User inDb = userRepository.findById(testUser.getId()).get();
        // On vérifie que le mot de passe en base a été modifié (il doit être hashé)
        assertThat(inDb.getPassword()).startsWith("$2a$"); 
        // On vérifie que BCrypt reconnaît le nouveau mot de passe
        assertThat(passwordEncoder.matches("newPassword123", inDb.getPassword())).isTrue();
    }
    
    
    @Test
    void update_ShouldThrowException_WhenPasswordIsTooShort() {
        // ARRANGE
        UpdateUserRequest req = new UpdateUserRequest();
        req.setPseudo("newPseudo");
        req.setEmail("test@test.com");
        req.setPassword("123"); // Trop court
        Long userId = testUser.getId();

        // ACT & ASSERT
        assertThrows(BadRequestException.class, () -> {
            userService.update(userId, req);
        });
    }
    
    
    @Test
    void update_ShouldNotChangePassword_WhenPasswordIsEmpty() {
        // ARRANGE
        String oldPassword = testUser.getPassword();
        UpdateUserRequest req = new UpdateUserRequest();
        req.setPseudo("newPseudo");
        req.setEmail("test@test.com");
        req.setPassword(""); // Vide
        
        Long userId = testUser.getId();

        // ACT
        userService.update(testUser.getId(), req);

        // ASSERT
        User inDb = userRepository.findById(userId).get();
        assertThat(inDb.getPassword()).isEqualTo(oldPassword); // Le MDP n'a pas bougé
    }

    @Test
    void subscribe_ShouldAddThemeToUser() {
    	// ARRANGE
        userService.subscribe(testUser.getId(), testTheme.getId());

        // ACT
        User updatedUser = userRepository.findById(testUser.getId()).get();
        
        // ASSERT
        assertThat(updatedUser.getAbonnements()).hasSize(1);
        assertThat(updatedUser.getAbonnements().iterator().next().getName()).isEqualTo("Spring Boot IT");
    }

    @Test
    void unsubscribe_ShouldRemoveThemeFromUser() {
    	// ARRANGE
        userService.subscribe(testUser.getId(), testTheme.getId());
        
        // ACT
        userService.unsubscribe(testUser.getId(), testTheme.getId());
        
        // ASSERT
        User updatedUser = userRepository.findById(testUser.getId()).get();
        
        assertThat(updatedUser.getAbonnements()).isEmpty();
    }
    
    @Test
    void subscribe_ShouldThrowException_WhenTopicNotFound() {
    	Long userId = testUser.getId();
        Long nonExistentTopicId = 999L;
        // ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> {
            userService.subscribe(userId, nonExistentTopicId); // ID topic inexistant
        });
    }

    @Test
    void unsubscribe_ShouldThrowException_WhenTopicNotFound() {
    	Long userId = testUser.getId();
        Long nonExistentTopicId = 999L;
        // ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> {
            userService.unsubscribe(userId, nonExistentTopicId);
        });
    }
}
