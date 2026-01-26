package com.openclassrooms.mddapi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.models.Topic;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@SpringBootTest
@Transactional
class CommentServiceTest {
    private CommentService commentService;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final TopicRepository themeRepository;
    
    
    
    @Autowired
    public CommentServiceTest(CommentService commentService, CommentRepository commentRepository,
			UserRepository userRepository, PostRepository postRepository, TopicRepository themeRepository) {
		super();
		this.commentService = commentService;
		this.commentRepository = commentRepository;
		this.userRepository = userRepository;
		this.postRepository = postRepository;
		this.themeRepository = themeRepository;
	}

	private User testUser;
    private Post testPost;
    private CommentDTO savedComment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("commenter@test.com");
        testUser.setPseudo("JeanMimi");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);

        Topic topic = themeRepository.findAll().get(0);

        testPost = new Post();
        testPost.setTitre("Post pour commentaires");
        testPost.setContenu("Ceci est un post de test.");
        testPost.setAuteur(testUser);
        testPost.setTopic(topic);
        testPost = postRepository.save(testPost);

        Comment comment = new Comment();
        comment.setContenu("Super article !");
        comment.setAuthor(testUser);
        comment.setPost(testPost);
        savedComment = CommentDTO.fromEntity(commentRepository.save(comment));
    }

    @Test
    void findByID_ShouldReturnComment_WhenExists() {
        // ACT
    	CommentDTO found = commentService.findByID(savedComment.getId());

        // ASSERT
        assertThat(found).isNotNull();
        assertThat(found.getContenu()).isEqualTo("Super article !");
        assertThat(found.getAuthorName()).isEqualTo("JeanMimi");
    }

    @Test
    void findByID_ShouldThrowException_WhenNotFound() {
        // ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> {
            commentService.findByID(999L);
        });
    }

    @Test
    void findAll_ShouldReturnAllComments() {
        // ACT
        List<CommentDTO> comments = commentService.findAll();

        // ASSERT
        assertThat(comments).contains(savedComment);
    }

    @Test
    void findByPostId_ShouldReturnOnlyCommentsForSpecificPost() {
        // ARRANGE
        Post secondPost = new Post();
        secondPost.setTitre("Autre Post");
        secondPost.setContenu("Contenu");
        secondPost.setAuteur(testUser);
        secondPost.setTopic(themeRepository.findAll().get(0));
        secondPost = postRepository.save(secondPost);

        Comment otherComment = new Comment();
        otherComment.setContenu("Commentaire sur le second post");
        otherComment.setAuthor(testUser);
        otherComment.setPost(secondPost);
        commentRepository.save(otherComment);

        // ACT
        List<CommentDTO> results = commentService.findByPostId(testPost.getId());

        // ASSERT
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContenu()).isEqualTo("Super article !");
    }

    @Test
    void create_ShouldSaveCommentProperly() {
        // ARRANGE
        Comment newComment = new Comment();
        newComment.setContenu("Nouveau commentaire via service");
        newComment.setAuthor(testUser);
        newComment.setPost(testPost);
        
        

        // ACT
        CommentDTO created = commentService.create(CommentDTO.fromEntity(newComment),testPost.getId(),testUser.getId());

        // ASERT
        assertThat(created.getId()).isNotNull();
        assertThat(created.getContenu()).isEqualTo("Nouveau commentaire via service");
        
        assertThat(commentRepository.findById(created.getId())).isPresent();
    }
}
