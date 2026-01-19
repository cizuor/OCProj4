import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Topic } from 'src/app/core/interfaces/topic.interface';
import { UpdateRequest } from 'src/app/core/interfaces/updateRequest.interface';
import { User } from 'src/app/core/interfaces/user.interface';
import { TopicService } from 'src/app/core/services/topic-service';
import { UserService } from 'src/app/core/services/user-service';
import { TopicCardComponent } from "src/app/features/topic/components/topic-card-component/topic-card-component";

@Component({
  selector: 'profil',
  imports: [CommonModule,ReactiveFormsModule, RouterLink, TopicCardComponent],
  templateUrl: './user-component.html',
  styleUrl: './user-component.css',
})
export class UserComponent implements OnInit{
  private route = inject(ActivatedRoute);
  private topicService = inject(TopicService);
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  public lTopics : Topic[] = [];
  public userForm!: FormGroup;


  ngOnInit(): void {

    this.userForm = this.fb.group({
      pseudo: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: [''] 
    });

    this.userService.getMe().subscribe(user => {
      this.userForm.patchValue({
        pseudo: user.pseudo,
        email: user.email
      });
    });

    this.loadSubscriptions();
  }

  loadSubscriptions() {
    this.topicService.getFolowed().subscribe(data => this.lTopics = data );
  }

  onSave(): void {
    if (this.userForm.valid) {
       const updateData: UpdateRequest = {
        pseudo: this.userForm.value.pseudo,
        email: this.userForm.value.email
      };
      this.userService.update(updateData).subscribe({
        next: () => alert('Profil mis à jour '),
        error: (err) => console.error(err)
      });
    }
  }

  handleSubscription(topic: Topic) {
      this.topicService.unsubscribe(topic.id).subscribe(newTopics => this.lTopics = newTopics);
  }

}
