import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {HttpClient} from "@angular/common/http";
import * as Excel from 'xlsx';

@Component({
  selector: 'app-answer-window',
  templateUrl: './answer-window.component.html',
  styleUrls: ['./answer-window.component.css']
})
export class AnswerWindowComponent {
  selectedFeedbacks: any[];
  currentFeedbackIndex: number = 0;
  responseText = {
    answer: undefined
  };

  //tempAnswer: string = 'Хороший ответ на хороший отзыв! С уважением '

  constructor(
      @Inject(MAT_DIALOG_DATA) public data: any,
      private http: HttpClient) {
    this.selectedFeedbacks = data;

    this.getAnswer();
  }

  nextPage(){
    this.getAnswer();
    if(this.currentFeedbackIndex < this.selectedFeedbacks.length - 1)
      this.currentFeedbackIndex++;
    else if(this.currentFeedbackIndex == this.selectedFeedbacks.length - 1)
      this.currentFeedbackIndex = 0;
  }

  prevPage(){
    this.getAnswer();
    if(this.currentFeedbackIndex > 0)
      this.currentFeedbackIndex--;
    else if(this.currentFeedbackIndex == 0)
      this.currentFeedbackIndex = this.selectedFeedbacks.length - 1;
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

  sendResponse(feedbackId: String, brandName: String) {
    const postData = {
      id: feedbackId,
      text: this.responseText.answer + this.selectedFeedbacks[this.currentFeedbackIndex].feedback.productDetails.brandName,
      brandName: brandName
    };

    this.http.post('/api/feedbacks/answer', postData).subscribe(
      (response) => {
        console.log('Ok!' + JSON.stringify(response));
        this.deleteItem();
      },
      (error) => {
        console.log('error! ' + JSON.stringify(error));
      }
    )
    this.getAnswer();
  }

  getAnswer(){
    this.http.get('/api/feedbacks/answer').subscribe( (v: any) => {
      this.responseText = v;
      },
      (error) => {
      console.log('Error!' + JSON.stringify(error));
      }
    )
  }
}
