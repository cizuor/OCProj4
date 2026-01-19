import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Topic } from 'src/app/core/interfaces/topic.interface';
import { TopicService } from 'src/app/core/services/topic-service';
import { TopicCardComponent } from "../topic-card-component/topic-card-component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-topic-list',
  imports: [TopicCardComponent,CommonModule],
  templateUrl: './topic-list-component.html',
  styleUrl: './topic-list-component.css',
})
export class TopicListComponent {
  private route = inject(ActivatedRoute);
  private topicService = inject(TopicService);

  public lTopics : Topic[] = [];

  ngOnInit(): void {
    this.topicService.getAll().subscribe(data => this.lTopics = data );
  }

  handleSubscription(topic: Topic) {
    if (topic.liked) {
      // prevoire evolution je pense pour se désabonné depuis cette page.
      //this.topicService.unsubscribe(topic.id).subscribe(newTopics => this.lTopics = newTopics);
    } else {
      this.topicService.subscribe(topic.id).subscribe(newTopics => this.lTopics = newTopics);
    }
  }
}
