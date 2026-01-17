import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PostComment } from 'src/app/core/interfaces/comment.interface';
import { Post } from 'src/app/core/interfaces/post.interface';
import { CommentService } from 'src/app/core/services/comment-service';
import { PostService } from 'src/app/core/services/post-service';

@Component({
  selector: 'app-post-detail',
  imports: [CommonModule, 
    ReactiveFormsModule,RouterLink],
  templateUrl: './post-detail-component.html',
  styleUrl: './post-detail-component.css',
})
export class PostDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private postService = inject(PostService);
  private commentService = inject(CommentService);
  private fb = inject(FormBuilder);

  public post?: Post;
  public comments: PostComment[] = [];
  public postId!: number;

  public commentForm = this.fb.group({
    contenu: ['', [Validators.required, Validators.maxLength(500)]]
  });

  ngOnInit(): void {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    
    // 1. Charger l'article
    this.postService.getById(this.postId).subscribe(data => this.post = data);
    
    // 2. Charger les commentaires
    this.commentService.getAllCommentFromPost(this.postId).subscribe(data => this.comments = data);
  }

  public sendComment(): void {
    const commentValue = this.commentForm.value as PostComment;
    this.commentService.create(commentValue, this.postId).subscribe({
      next: (newComment) => {
        this.comments.push(newComment); // On ajoute en live
        this.commentForm.reset();
      }
    });
  }
}
