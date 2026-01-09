package com.openclassrooms.mddapi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@SpringBootTest
@Transactional // Très important : annule les modifs en BDD après chaque test
public class UserServicesTest {
	
	@Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;
    
    
    private User testUser;
    private Theme testTheme;

    @BeforeEach
    void setUp() {
        // Nettoyage
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setPseudo("TestUser");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);

        testTheme = new Theme();
        testTheme.setTitle("Spring Boot IT");
        testTheme.setDescription("Test d'intégration");
        testTheme = themeRepository.save(testTheme);
    }

    @Test
    void findByID_ShouldReturnUserDTO_WhenUserExists() {
    	// ACT
        UserDTO result = userService.findByID(testUser.getId());

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getPseudo()).isEqualTo("TestUser");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void findByID_ShouldThrowException_WhenUserDoesNotExist() {
    	// ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> {
            userService.findByID(999L);
        });
    }

    @Test
    void update_ShouldModifyUser_WhenValidData() {
    	// ARRANGE
        UserDTO updateInfo = new UserDTO();
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
    void subscribe_ShouldAddThemeToUser() {
    	// ARRANGE
        userService.subscribe(testUser.getId(), testTheme.getId());

        // ACT
        User updatedUser = userRepository.findById(testUser.getId()).get();
        
        // ASSERT
        assertThat(updatedUser.getAbonnements()).hasSize(1);
        assertThat(updatedUser.getAbonnements().iterator().next().getTitle()).isEqualTo("Spring Boot IT");
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
}
