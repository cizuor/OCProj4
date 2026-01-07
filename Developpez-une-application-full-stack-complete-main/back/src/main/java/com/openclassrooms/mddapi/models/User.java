package com.openclassrooms.mddapi.models;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
	    name = "users",
	    uniqueConstraints = {
	        @UniqueConstraint(columnNames = "email"),
	        @UniqueConstraint(columnNames = "pseudo")
	    }
	)
@NoArgsConstructor
@AllArgsConstructor
@Data //gestion des getter setter
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
    @Email
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String email;
	
	@NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String pseudo;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String password;
    
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_abo", // Nom de la table de jointure
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
        )
    private Set<Theme> abonnements = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    
    
    public void addAbo(Theme theme) {
		if (theme != null && !this.abonnements.contains(theme)) {
            this.abonnements.add(theme);
        }
    }
    
    public void removeAbo(Theme theme) {
		if (theme != null && this.abonnements.contains(theme)) {
            this.abonnements.remove(theme);
        }
    }

}
