import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Topic } from 'src/app/core/interfaces/topic.interface';

@Component({
  selector: 'app-topic-card',
  imports: [],
  templateUrl: './topic-card-component.html',
  styleUrl: './topic-card-component.css',
})
export class TopicCardComponent {

  //recoi le topic que le component va afficher
  @Input() topic!: Topic;

  // si la carte s'affiche depuis le profile
  @Input() isProfileMode : boolean = false;

  // action abo ou désabo
  @Output() onToggle = new EventEmitter<Topic>();


  toggle() {
    this.onToggle.emit(this.topic);
  }
}
