package com.openclassrooms.mddapi.Controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository themeRepository;

    private User testUser;
    private Post testPost;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setPseudo("JeanCommentaire");
        testUser.setEmail("jean@test.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        Topic topic = new Topic();
        topic.setName("Test Theme");
        topic = themeRepository.save(topic);

        testPost = new Post();
        testPost.setTitre("Article de test");
        testPost.setContenu("Contenu de test");
        testPost.setAuteur(testUser);
        testPost.setTopic(topic);
        testPost = postRepository.save(testPost);

        testComment = new Comment();
        testComment.setContenu("Ceci est un commentaire existant");
        testComment.setAuthor(testUser);
        testComment.setPost(testPost);
        testComment = commentRepository.save(testComment);

        // SIMULER L'AUTHENTIFICATION 
        // On "injecte" l'utilisateur dans le contexte de sécurité pour les tests
        UserDetailsImpl userDetails = UserDetailsImpl.build(testUser);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getById_ShouldReturnComment() throws Exception {
    	
    	// ACT & ASSERT
        mockMvc.perform(get("/api/commentaire/" + testComment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu").value("Ceci est un commentaire existant"))
                .andExpect(jsonPath("$.authorName").value("JeanCommentaire"));
    }

    @Test
    void getByPost_ShouldReturnListOfComments() throws Exception {
    	// ACT & ASSERT
        mockMvc.perform(get("/api/commentaire/article/" + testPost.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].contenu").value("Ceci est un commentaire existant"));
    }

    @Test
    void create_ShouldSaveAndReturnNewComment() throws Exception {
        // ARRANGE
        CommentDTO newCommentDto = new CommentDTO();
        newCommentDto.setContenu("Super article, merci !");
        
        // ACT & ASSERT
        mockMvc.perform(post("/api/commentaire/article/" + testPost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCommentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu").value("Super article, merci !"))
                .andExpect(jsonPath("$.authorName").value("JeanCommentaire"));
    }

    @Test
    void create_ShouldReturn400_WhenContentIsEmpty() throws Exception {
    	// ARRANGE
        CommentDTO invalidDto = new CommentDTO();
        invalidDto.setContenu(""); // @NotBlank donc erreur

        // ACT & ASSERT
        mockMvc.perform(post("/api/commentaire/article/" + testPost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
