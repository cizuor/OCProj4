package com.openclassrooms.mddapi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.models.Post;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@SpringBootTest
@Transactional
public class CommentServiceTest {
	@Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ThemeRepository themeRepository;

    private User testUser;
    private Post testPost;
    private Comment savedComment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("commenter@test.com");
        testUser.setPseudo("JeanMimi");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);

        Theme theme = themeRepository.findAll().get(0);

        testPost = new Post();
        testPost.setTitre("Post pour commentaires");
        testPost.setContenu("Ceci est un post de test.");
        testPost.setAuteur(testUser);
        testPost.setTheme(theme);
        testPost = postRepository.save(testPost);

        Comment comment = new Comment();
        comment.setContenu("Super article !");
        comment.setAuthor(testUser);
        comment.setPost(testPost);
        savedComment = commentRepository.save(comment);
    }

    @Test
    void findByID_ShouldReturnComment_WhenExists() {
        // ACT
        Comment found = commentService.findByID(savedComment.getId());

        // ASSERT
        assertThat(found).isNotNull();
        assertThat(found.getContenu()).isEqualTo("Super article !");
        assertThat(found.getAuthor().getPseudo()).isEqualTo("JeanMimi");
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
        List<Comment> comments = commentService.findAll();

        // ASSERT
        assertThat(comments).isNotEmpty();
        assertThat(comments).contains(savedComment);
    }

    @Test
    void findByPostId_ShouldReturnOnlyCommentsForSpecificPost() {
        // ARRANGE
        Post secondPost = new Post();
        secondPost.setTitre("Autre Post");
        secondPost.setContenu("Contenu");
        secondPost.setAuteur(testUser);
        secondPost.setTheme(themeRepository.findAll().get(0));
        secondPost = postRepository.save(secondPost);

        Comment otherComment = new Comment();
        otherComment.setContenu("Commentaire sur le second post");
        otherComment.setAuthor(testUser);
        otherComment.setPost(secondPost);
        commentRepository.save(otherComment);

        // ACT
        List<Comment> results = commentService.findByPostId(testPost.getId());

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
        Comment created = commentService.create(newComment);

        // ASERT
        assertThat(created.getId()).isNotNull();
        assertThat(created.getContenu()).isEqualTo("Nouveau commentaire via service");
        
        assertThat(commentRepository.findById(created.getId())).isPresent();
    }
}
