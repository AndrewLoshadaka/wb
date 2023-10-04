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
  selectedFeedbacks: any[];
  currentFeedbackIndex: number = 0;
  responseText = {
    answer: ""
  };


  constructor(
      @Inject(MAT_DIALOG_DATA) public data: any,
      private http: HttpClient,
      private dialogRef: MatDialogRef<AnswerWindowComponent>,
      private feedbacksService: FeedbacksService) {

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

  sendResponse(feedbackId: String, brandName: String, supplierName: string) {

    const postData = {
      id: feedbackId,
      text: this.responseText.answer,
      brandName: brandName,
      supplier: supplierName
    };

    this.http.post('/api/feedbacks/send-answer', postData).subscribe(
      (response) => {
        console.log('Ok!' + JSON.stringify(response));
        this.deleteItem();
        if (this.selectedFeedbacks.length === 0) {
                this.dialogRef.close();
        }
        this.feedbacksService.deleteFeedback(feedbackId);
      },
      (error) => {
        console.log('error! ' + JSON.stringify(error));
      }
    )
    this.getAnswer();
  }

  getAnswer(){
    const postData ={
      product: this.selectedFeedbacks[this.currentFeedbackIndex].feedback.productDetails.productName,
      brand: this.selectedFeedbacks[this.currentFeedbackIndex].feedback.productDetails.brandName,
      supplier: this.selectedFeedbacks[this.currentFeedbackIndex].feedback.productDetails.supplierName
    }

    this.http.post('/api/feedbacks/answer', postData).subscribe(
      (response: any) => {
        console.log('Ok!' + JSON.stringify(response));
        this.responseText.answer = response.answer;
      },
      (error) => {
        console.log('error! ' + JSON.stringify(error));
      }
    )
  }
}
