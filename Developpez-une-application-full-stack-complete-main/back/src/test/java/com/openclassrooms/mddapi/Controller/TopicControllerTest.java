package com.openclassrooms.mddapi.Controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TopicControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    private UserDetailsImpl userDetails;
    private Topic savedTopic;

    @BeforeEach
    void setUp() {
       
        User user = new User();
        user.setPseudo("TopicTester");
        user.setEmail("tester@topic.com");
        user.setPassword("password");
        user = userRepository.save(user);
        
        
        userDetails = UserDetailsImpl.build(user);

        
        Topic topic = new Topic();
        topic.setName("Java 21");
        topic.setDescription("Découverte des nouveautés de Java 21");
        savedTopic = topicRepository.save(topic);
    }

    @Test
    void getById_ShouldReturnTopicWithLikedStatus() throws Exception {
    	// ACT & ASSERT
        mockMvc.perform(get("/api/topic/" + savedTopic.getId())
                .with(user(userDetails))) 
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java 21"))
                .andExpect(jsonPath("$.liked").value(false)); 
    }

    @Test
    void getAll_ShouldReturnListOfTopics() throws Exception {
    	// ACT & ASSERT
        mockMvc.perform(get("/api/topic")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    void getAllLiked_ShouldReturnOnlyFollowedTopics() throws Exception {
        // ARRANGE
        User user = userRepository.findById(userDetails.getId()).get();
        user.addAbo(savedTopic);
        userRepository.save(user);

        // ACT & ASSERT
        mockMvc.perform(get("/api/topic/suivie")
                .with(user(userDetails)))
        		.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java 21"))
                .andExpect(jsonPath("$[0].liked").value(true));
    }

    @Test
    void create_ShouldSaveNewTopic() throws Exception {
        TopicDTO newTopic = new TopicDTO();
        newTopic.setTitle("Angular 17");
        newTopic.setDescription("Le futur du web");

        mockMvc.perform(post("/api/topic")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTopic)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Angular 17"));
    }

    @Test
    void create_ShouldReturn400_WhenTitleIsEmpty() throws Exception {
        TopicDTO invalidTopic = new TopicDTO();
        invalidTopic.setTitle("");

        mockMvc.perform(post("/api/topic")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTopic)))
                .andExpect(status().isBadRequest());
    }
}
