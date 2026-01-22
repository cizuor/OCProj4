import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Topic } from 'src/app/core/interfaces/topic.interface';
import { UpdateRequest } from 'src/app/core/interfaces/updateRequest.interface';
import { TopicService } from 'src/app/core/services/topic-service';
import { UserService } from 'src/app/core/services/user-service';
import { TopicCardComponent } from "src/app/features/topic/components/topic-card-component/topic-card-component";

@Component({
  selector: 'profil',
  imports: [CommonModule,ReactiveFormsModule, TopicCardComponent],
  templateUrl: './user-component.html',
  styleUrl: './user-component.css',
})
export class UserComponent implements OnInit{
  private route = inject(ActivatedRoute);
  private topicService = inject(TopicService);
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  public lTopics : Topic[] = [];



  public userForm = this.fb.group({
      pseudo: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: [''] 
    });

  ngOnInit(): void {

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

      const formValues = this.userForm.value;
       const updateData: UpdateRequest = {
        pseudo: formValues.pseudo ?? '',
        email: formValues.email?? ''
      };

      if (formValues.password && formValues.password.trim() !== '') {
        updateData.password = formValues.password;
      }
      this.userService.update(updateData).subscribe({
        next: (updatedUser) => {
          alert('Profil mis à jour '),
          this.userForm.get('password')?.reset();
        },
        error: (err) => console.error('Erreur lors de la mise à jour ',err)
      });
    }
  }

  handleSubscription(topic: Topic) {
    this.topicService.unsubscribe(topic.id).subscribe(allTopics => {
    this.lTopics = allTopics.filter(t => t.liked);
  });
  }

}
