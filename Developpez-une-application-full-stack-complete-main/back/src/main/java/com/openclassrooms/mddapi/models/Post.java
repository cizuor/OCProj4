package com.openclassrooms.mddapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Data //gestion des getter setter
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="post_id")
	private Long id;

	
	@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Topic topic;
	
	@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User auteur;
	
	@NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String titre;
	
	@NotBlank
    @Size(max = 3000)
    @Column(nullable = false, columnDefinition  = "TEXT")
    private String contenu;
	
	@CreationTimestamp
    @Column(updatable = false,nullable = false)
    private LocalDateTime createdAt;

}
