package com.openclassrooms.mddapi.Controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.payload.request.UpdateUserRequest;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.services.UserDetailsImpl;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    private User testUser;
    private UserDetailsImpl userDetails;
    private Topic testTopic;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setPseudo("Pierre75");
        testUser.setEmail("pierre@test.com");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);
        
        userDetails = UserDetailsImpl.build(testUser);

        testTopic = new Topic();
        testTopic.setName("Java 21");
        testTopic.setDescription("Les nouveautés de Java");
        testTopic = topicRepository.save(testTopic);
    }

    @Test
    void findById_ShouldReturnUserWithHiddenEmail() throws Exception {
    	// ACT & ASSERT
        mockMvc.perform(get("/api/utilisateur/" + testUser.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("Pierre75"))
                .andExpect(jsonPath("$.email").value("hide@information.com"));
    }

    @Test
    void getMyProfile_ShouldReturnFullUser() throws Exception {
    	// ACT & ASSERT
        mockMvc.perform(get("/api/utilisateur/me")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("Pierre75"))
                .andExpect(jsonPath("$.email").value("pierre@test.com"));
    }

    @Test
    void subscribe_ShouldAddTopicAndReturnList() throws Exception {
    	// ACT & ASSERT
        mockMvc.perform(post("/api/utilisateur/subscribe/" + testTopic.getId())
                .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == " + testTopic.getId() + ")].liked").value(true));
    }

    @Test
    void unsubscribe_ShouldRemoveTopicAndReturnList() throws Exception {
        // ARRANGE
        testUser.addAbo(testTopic);
        userRepository.save(testUser);

        // ACT & ASSERT
        mockMvc.perform(post("/api/utilisateur/unsubscribe/" + testTopic.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + testTopic.getId() + ")].liked").value(false));
    }

    @Test
    void updateMyProfile_ShouldUpdateData() throws Exception {
        // ARRANGE
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setPseudo("PierreUpdated");
        updateRequest.setEmail("newemail@test.com");

        // ACT & ASSERT
        mockMvc.perform(put("/api/utilisateur/me")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("PierreUpdated"))
                .andExpect(jsonPath("$.email").value("newemail@test.com"));
        
        User updatedInDb = userRepository.findById(testUser.getId()).get();
        assert(updatedInDb.getPseudo().equals("PierreUpdated"));
    }

    @Test
    void updateMyProfile_ShouldReturn400_WhenInvalidData() throws Exception {
    	// ARRANGE
        UpdateUserRequest invalidRequest = new UpdateUserRequest();
        invalidRequest.setPseudo(""); // Provoque une erreur @NotBlank

        // ACT & ASSERT
        mockMvc.perform(put("/api/utilisateur/me")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}
