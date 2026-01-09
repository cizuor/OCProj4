package com.openclassrooms.mddapi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@SpringBootTest
@Transactional
public class PostServiceTest {
	
	@Autowired
    private PostService postService;
	
	@Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    private User author;
    private Theme themeJava;
    private Theme themeAngular;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setEmail("author@test.com");
        author.setPseudo("AuthorIT");
        author.setPassword("password");
        author = userRepository.save(author);

        themeJava = themeRepository.findByTitle("Java")
                .orElseThrow(() -> new RuntimeException("Thème Java non trouvé dans data.sql"));

        themeAngular = themeRepository.findByTitle("Angular")
                .orElseThrow(() -> new RuntimeException("Thème Angular non trouvé dans data.sql"));

        Post post = new Post();
        post.setTitre("Premier Post");
        post.setContenu("Contenu du premier post");
        post.setAuteur(author);
        post.setTheme(themeJava);
        savedPost = postRepository.save(post);
    }

    @Test
    void findByID_ShouldReturnPost_WhenExists() {
    	// ACT
        Post found = postService.findByID(savedPost.getId());
        // ASSERT
        assertThat(found).isNotNull();
        assertThat(found.getTitre()).isEqualTo("Premier Post");
    }

    @Test
    void findByID_ShouldThrowException_WhenNotFound() {
    	// ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> {
            postService.findByID(999L);
        });
    }

    @Test
    void findAll_ShouldReturnListWithAtLeastOnePost() {
    	// ACT
        List<Post> posts = postService.findAll();
        // ASSERT
        assertThat(posts).isNotEmpty();
        assertThat(posts.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void create_ShouldSaveNewPost() {
    	// ARRANGE
        Post newPost = new Post();
        newPost.setTitre("Nouveau Post");
        newPost.setContenu("Nouveau Contenu");
        newPost.setAuteur(author);
        newPost.setTheme(themeAngular);
        
        // ACT
        Post created = postService.create(newPost);

        // ASSERT
        assertThat(created.getId()).isNotNull();
        assertThat(postRepository.findById(created.getId())).isPresent();
    }

    @Test
    void findByThemeId_ShouldFilterPosts() {
    	// ACT
        List<Post> results = postService.findByThemeId(Arrays.asList(themeJava.getId()));
        // ASSERT
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitre()).isEqualTo("Premier Post");
    }

    @Test
    void update_ShouldModifyExistingPost() {
    	// ARRANGE
        Post updateInfo = new Post();
        updateInfo.setTitre("Titre Modifié");
        updateInfo.setContenu("Contenu Modifié");
        updateInfo.setTheme(themeJava);
        updateInfo.setAuteur(author);

        // ACT
        Post updated = postService.update(savedPost.getId(), updateInfo);

        // ASSERT
        assertThat(updated.getTitre()).isEqualTo("Titre Modifié");
        
        // Vérification en BDD
        Post inDb = postRepository.findById(savedPost.getId()).get();
        assertThat(inDb.getTitre()).isEqualTo("Titre Modifié");
    }

    @Test
    void delete_ShouldRemovePost() {
    	// ACT
        postService.delete(savedPost);

        // ASSERT
        assertThat(postRepository.findById(savedPost.getId())).isEmpty();
    }

    @Test
    void findByThemeIdOrderByCreateAtAsc_ShouldReturnSortedPosts() throws InterruptedException {
        // On crée un deuxième post un peu plus tard (on attend 10ms pour être sûr de la différence de timestamp)
        Thread.sleep(100);
        // ARRANGE
        Post secondPost = new Post();
        secondPost.setTitre("Second Post");
        secondPost.setContenu("Contenu 2");
        secondPost.setAuteur(author);
        secondPost.setTheme(themeJava);
        postRepository.save(secondPost);

        // ACT
        List<Post> results = postService.findByThemeIdOrderByCreateAtAsc(Arrays.asList(themeJava.getId()));

        // ASSERT
        assertThat(results).hasSize(2);
        // Le premier post créé doit être en premier (ASC)
        assertThat(results.get(0).getTitre()).isEqualTo("Premier Post");
        assertThat(results.get(1).getTitre()).isEqualTo("Second Post");
    }

}
