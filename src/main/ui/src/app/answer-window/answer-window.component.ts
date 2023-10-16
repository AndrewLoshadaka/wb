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
  templates: any[] = [];
  selectedValue: string = '';
  constructor(
      @Inject(MAT_DIALOG_DATA) public data: any,
      private dialogRef: MatDialogRef<AnswerWindowComponent>,
      private feedbacksService: FeedbacksService) {

    this.currentFeedbackIndex = 0;
    this.selectedFeedbacks = data.feedbacks;
    this.templates = data.templates;
    this.answerText = "";
    this.getAnswer();
  }

  nextPage(){
    if(this.currentFeedbackIndex < this.selectedFeedbacks.length - 1)
      this.currentFeedbackIndex++;
    else if(this.currentFeedbackIndex == this.selectedFeedbacks.length - 1)
      this.currentFeedbackIndex = 0;
    this.getAnswer();
    this.selectedValue = '';
  }

  prevPage(){
    if(this.currentFeedbackIndex > 0)
      this.currentFeedbackIndex--;
    else if(this.currentFeedbackIndex == 0)
      this.currentFeedbackIndex = this.selectedFeedbacks.length - 1;
    this.getAnswer();
    this.selectedValue = '';
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
    if(this.selectedFeedbacks.length === 0) {
      this.dialogRef.close();
    }
  }

  sendResponse() {
    this.feedbacksService.sendResponse(this.selectedFeedbacks[this.currentFeedbackIndex], this.answerText);
    this.deleteItem();
    this.getAnswer();
    this.selectedValue = '';
  }

  getAnswer() {
    this.feedbacksService.getAnswer(this.selectedFeedbacks[this.currentFeedbackIndex]).subscribe(
      (response) => {
        this.answerText = response.answer;
      },
      (error) => {
        console.log('error! ' + JSON.stringify(error));
      }
    );
  }

  getAnswerByTemplate(index: number){
    this.answerText = this.templates[index].answer + '\nС уважением, ' + this.selectedFeedbacks[this.currentFeedbackIndex].feedback.productDetails.brandName;
  }
}
