package com.openclassrooms.mddapi.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Data //gestion des getter setter
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	
	@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;
	
	@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
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
