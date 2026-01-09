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
        // Nettoyage et création d'un utilisateur de test
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
        // When
        UserDTO result = userService.findByID(testUser.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPseudo()).isEqualTo("TestUser");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void findByID_ShouldThrowException_WhenUserDoesNotExist() {
        // When / Then
        assertThrows(EntityNotFoundException.class, () -> {
            userService.findByID(999L);
        });
    }

    @Test
    void update_ShouldModifyUser_WhenValidData() {
        // Given
        UserDTO updateInfo = new UserDTO();
        updateInfo.setPseudo("NewPseudo");
        updateInfo.setEmail("new@test.com");

        // When
        UserDTO result = userService.update(testUser.getId(), updateInfo);

        // Then
        assertThat(result.getPseudo()).isEqualTo("NewPseudo");
        assertThat(result.getEmail()).isEqualTo("new@test.com");
        
        // Vérification en base de données réelle
        User inDb = userRepository.findById(testUser.getId()).get();
        assertThat(inDb.getPseudo()).isEqualTo("NewPseudo");
    }

    @Test
    void subscribe_ShouldAddThemeToUser() {
        // When
        userService.subscribe(testUser.getId(), testTheme.getId());

        // Then
        User updatedUser = userRepository.findById(testUser.getId()).get();
        // On vérifie que le thème est présent dans la liste d'abonnements
        assertThat(updatedUser.getAbonnements()).hasSize(1);
        assertThat(updatedUser.getAbonnements().iterator().next().getTitle()).isEqualTo("Spring Boot IT");
    }

    @Test
    void unsubscribe_ShouldRemoveThemeFromUser() {
        // Given : on s'abonne d'abord
        userService.subscribe(testUser.getId(), testTheme.getId());
        
        // When
        userService.unsubscribe(testUser.getId(), testTheme.getId());

        // Then
        User updatedUser = userRepository.findById(testUser.getId()).get();
        assertThat(updatedUser.getAbonnements()).isEmpty();
    }
}
