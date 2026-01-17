import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Topic } from 'src/app/core/interfaces/topic.interface';
import { PostService } from 'src/app/core/services/post-service';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TopicService } from 'src/app/core/services/topic-service';

@Component({
  selector: 'app-post',
  imports: [CommonModule, 
    ReactiveFormsModule],
  templateUrl: './post-component.html',
  styleUrl: './post-component.css',
})
export class PostComponent {
  private fb = inject(FormBuilder);
  private postService = inject(PostService);
  private topicService = inject(TopicService);
  private router = inject(Router);


  public topics: Topic[] = [];

  public postForm = this.fb.group({
    topicId: ['', [Validators.required]],
    titre: ['', [Validators.required, Validators.maxLength(100)]],
    contenu: ['', [Validators.required, Validators.minLength(10)]]
  });


  ngOnInit(): void {
    // On charge les thèmes dès l'arrivée sur la page
    this.topicService.getAll().subscribe({
      next: (data) => this.topics = data,
      error: (err) => console.error('Erreur lors du chargement des thèmes', err)
    });
  }


  public onSubmit(): void {
    if (this.postForm.valid) {
      // On convertit les valeurs du formulaire en objet Post
      // Note : pas besoin de userID, le backend le récupère via le Token !
      const newPost = this.postForm.value as any;

      this.postService.create(newPost).subscribe({
        next: () => {
          // Redirection vers le fil d'actualités après succès
          this.router.navigate(['/articles']);
        },
        error: (err) => console.error('Erreur lors de la création', err)
      });
    }
  }
}
