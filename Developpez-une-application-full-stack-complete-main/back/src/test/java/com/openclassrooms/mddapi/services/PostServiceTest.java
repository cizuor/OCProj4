package com.openclassrooms.mddapi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
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
    private TopicRepository themeRepository;

    private User author;
    private Topic topicJava;
    private Topic topicAngular;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setEmail("author@test.com");
        author.setPseudo("AuthorIT");
        author.setPassword("password");
        author = userRepository.save(author);

        topicJava = themeRepository.findByName("Java")
                .orElseThrow(() -> new RuntimeException("Thème Java non trouvé dans data.sql"));

        topicAngular = themeRepository.findByName("Angular")
                .orElseThrow(() -> new RuntimeException("Thème Angular non trouvé dans data.sql"));

        Post post = new Post();
        post.setTitre("Premier Post");
        post.setContenu("Contenu du premier post");
        post.setAuteur(author);
        post.setTopic(topicJava);
        savedPost = postRepository.save(post);
    }

    @Test
    void findByID_ShouldReturnPost_WhenExists() {
    	// ACT
        PostDTO found = postService.findByID(savedPost.getId());
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
        List<PostDTO> posts = postService.findAll();
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
        newPost.setTopic(topicAngular);
        
        PostDTO postDto = PostDTO.fromEntity(newPost);
        
        // ACT
        PostDTO created = postService.create(postDto);

        // ASSERT
        assertThat(created.getId()).isNotNull();
        assertThat(postRepository.findById(created.getId())).isPresent();
    }

    @Test
    void findByThemeId_ShouldFilterPosts() {
    	// ACT
        List<PostDTO> results = postService.findByThemeId(Arrays.asList(topicJava.getId()));
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
        updateInfo.setTopic(topicJava);
        updateInfo.setAuteur(author);
        
        PostDTO postDto = PostDTO.fromEntity(updateInfo);

        // ACT
        PostDTO updated = postService.update(savedPost.getId(), postDto,author.getId());

        // ASSERT
        assertThat(updated.getTitre()).isEqualTo("Titre Modifié");
        
        // Vérification en BDD
        Post inDb = postRepository.findById(savedPost.getId()).get();
        assertThat(inDb.getTitre()).isEqualTo("Titre Modifié");
    }

    @Test
    void delete_ShouldRemovePost() {
    	// ACT
        postService.delete(savedPost.getId(),savedPost.getAuteur().getId());

        // ASSERT
        assertThat(postRepository.findById(savedPost.getId())).isEmpty();
    }
    
    
    @Test
    void delete_ShouldReturnError() {
    	// ACT && ASSERT
    	assertThrows(RuntimeException.class, () -> {
    		postService.delete(savedPost.getId(),savedPost.getAuteur().getId()+1);
        });
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
        secondPost.setTopic(topicJava);
        postRepository.save(secondPost);

        // ACT
        List<PostDTO> results = postService.findByThemeIdOrderByCreateAtAsc(Arrays.asList(topicJava.getId()));

        // ASSERT
        assertThat(results).hasSize(2);
        // Le premier post créé doit être en premier (ASC)
        assertThat(results.get(0).getTitre()).isEqualTo("Premier Post");
        assertThat(results.get(1).getTitre()).isEqualTo("Second Post");
    }

}
