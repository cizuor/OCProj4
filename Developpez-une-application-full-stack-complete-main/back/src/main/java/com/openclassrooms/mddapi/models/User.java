package com.openclassrooms.mddapi.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
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
			name = "user_subscriptions", 
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
        )
    private Set<Topic> abonnements = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    
    
    public void addAbo(Topic theme) {
		if (theme != null && !this.abonnements.contains(theme)) {
            this.abonnements.add(theme);
        }
    }
    
    public void removeAbo(Topic theme) {
		if (theme != null && this.abonnements.contains(theme)) {
            this.abonnements.remove(theme);
        }
    }

}
