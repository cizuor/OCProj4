import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Post } from 'src/app/core/interfaces/post.interface';
import { PostService } from 'src/app/core/services/post-service';

@Component({
  selector: 'app-post-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './post-list-component.html',
  styleUrl: './post-list-component.css',
})
export class PostListComponent implements OnInit {
  private postService = inject(PostService);
  public posts: Post[] = [];

  public sortDirection: string = 'desc';

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts() {
    this.postService.getFeed(this.sortDirection).subscribe(data => this.posts = data);
  }


  toggleSort(): void{

    if(this.sortDirection == 'asc'){
      this.sortDirection = 'desc';
    }else{
      this.sortDirection = 'asc'
    }

    this.loadPosts();
    
  }

}
