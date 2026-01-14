package com.openclassrooms.mddapi.Controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PostControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository themeRepository;

    private User testUser;
    private UserDetailsImpl userDetails;
    private Topic testTopic;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setPseudo("AuthorTest");
        testUser.setEmail("author@test.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);
        userDetails = UserDetailsImpl.build(testUser);

        testTopic = new Topic();
        testTopic.setName("Java Programming");
        testTopic.setDescription("Everything about Java");
        testTopic = themeRepository.save(testTopic);

        testPost = new Post();
        testPost.setTitre("Mon premier article");
        testPost.setContenu("Ceci est un contenu de test très intéressant.");
        testPost.setAuteur(testUser);
        testPost.setTopic(testTopic);
        testPost = postRepository.save(testPost);
    }

    @Test
    void getById_ShouldReturnPost() throws Exception {
    	// ACT & ASSERT
        mockMvc.perform(get("/api/article/" + testPost.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Mon premier article"))
                .andExpect(jsonPath("$.userName").value("AuthorTest"));
    }

    @Test
    void getAll_ShouldReturnListOfPosts() throws Exception {
    	// ACT & ASSERT
        mockMvc.perform(get("/api/article")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].titre").exists());
    }

    @Test
    void getFeed_ShouldReturnEmptyList_WhenNoSubscriptions() throws Exception {
    	// ACT & ASSERT
        mockMvc.perform(get("/api/article/feed")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getFeed_ShouldReturnPosts_WhenSubscribedToTheme() throws Exception {
        // ARRANGE
        testUser.addAbo(testTopic);
        userRepository.save(testUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/article/feed")
                .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].topicName").value("Java Programming"));
    }

    @Test
    void create_ShouldSaveNewPost() throws Exception {
    	// ARRANGE
        PostDTO newPostDto = new PostDTO();
        newPostDto.setTitre("Nouvel Article via API");
        newPostDto.setContenu("Contenu généré par le test MockMvc.");
        newPostDto.setTopicId(testTopic.getId());

        // ACT & ASSERT
        mockMvc.perform(post("/api/article")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Nouvel Article via API"));
    }

    @Test
    void update_ShouldModifyPost_WhenAuthorIsCurrentUser() throws Exception {
    	// ARRANGE
        PostDTO updateDto = new PostDTO();
        updateDto.setTitre("Titre Modifié");
        updateDto.setContenu("Nouveau contenu");
        updateDto.setTopicId(testTopic.getId());

        // ACT & ASSERT
        mockMvc.perform(put("/api/article/" + testPost.getId())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Titre Modifié"));
    }

    @Test
    void delete_ShouldRemovePost() throws Exception {
    	// ACT
        mockMvc.perform(delete("/api/article/" + testPost.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk());

        // ASSERT
        mockMvc.perform(get("/api/article/" + testPost.getId())
                .with(user(userDetails)))
                .andExpect(status().isNotFound()); 
    }
    
    @Test
    void update_ShouldReturn403_WhenAuthorIsNotCurrentUser() throws Exception {
        // ARRANGE
        User otherUser = new User();
        otherUser.setEmail("other@test.com");
        otherUser.setPseudo("Hacker");
        otherUser.setPassword("pwd");
        otherUser = userRepository.save(otherUser);
        UserDetailsImpl otherDetails = UserDetailsImpl.build(otherUser);

        PostDTO updateDto = new PostDTO();
        updateDto.setTitre("Tentative de hack");
        updateDto.setContenu("Ceci est un contenu valide"); 
        updateDto.setTopicId(testTopic.getId());            

        // ACT & ASSERT
        mockMvc.perform(put("/api/article/" + testPost.getId())
                .with(user(otherDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden()); // 403 Access Denied
    }
}
