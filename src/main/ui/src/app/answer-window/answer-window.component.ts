import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {HttpClient} from "@angular/common/http";
import { FeedbacksService } from '../feedbacks/feedbacks.service';

@Component({
  selector: 'app-answer-window',
  templateUrl: './answer-window.component.html',
  styleUrls: ['./answer-window.component.css']
})

export class AnswerWindowComponent {
  selectedFeedbacks;
  currentFeedbackIndex;
  answerText;
  responseText = {
    answer: ""
  };


  constructor(
      @Inject(MAT_DIALOG_DATA) public data: any,
      private http: HttpClient,
      private dialogRef: MatDialogRef<AnswerWindowComponent>,
      private feedbacksService: FeedbacksService) {

    this.currentFeedbackIndex = 0;
    this.selectedFeedbacks = data;
    this.answerText = "";
    this.getAnswer();
  }

  nextPage(){
    if(this.currentFeedbackIndex < this.selectedFeedbacks.length - 1)
      this.currentFeedbackIndex++;
    else if(this.currentFeedbackIndex == this.selectedFeedbacks.length - 1)
      this.currentFeedbackIndex = 0;
    this.getAnswer();
  }

  prevPage(){
    if(this.currentFeedbackIndex > 0)
      this.currentFeedbackIndex--;
    else if(this.currentFeedbackIndex == 0)
      this.currentFeedbackIndex = this.selectedFeedbacks.length - 1;
    this.getAnswer();
  }

  getCurrentFeedback(){
    return this.selectedFeedbacks[this.currentFeedbackIndex];
  }

  deleteItem(){
    if(this.currentFeedbackIndex == this.selectedFeedbacks.length - 1) {
      this.selectedFeedbacks.splice(this.currentFeedbackIndex, 1);
      this.currentFeedbackIndex--;
    } else {
      this.selectedFeedbacks.splice(this.currentFeedbackIndex, 1);
    }
  }

  sendResponse() {
    this.feedbacksService.sendResponse(this.selectedFeedbacks[this.currentFeedbackIndex], this.answerText);
    this.deleteItem();
    this.getAnswer();
  }

  getAnswer() {
    this.feedbacksService.getAnswer(this.selectedFeedbacks[this.currentFeedbackIndex]).subscribe(
      (response) => {
        console.log('Ok!' + response.answer);
        this.answerText = response.answer;
      },
      (error) => {
        console.log('error! ' + JSON.stringify(error));
      }
    );
  }
}
