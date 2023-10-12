import {Component, EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FeedbacksService {

  constructor(private http: HttpClient) { }

  rootURL = '/api';
  responseText = {
    answer: ""
  };

  getFeedbacks(stars: string, photos: string, video: string, brand: string, text: string) {
    return this.http.get(this.rootURL + '/feedbacks?stars=' + stars + '&photos=' + photos + '&video=' + video + '&brand=' + brand + "&text=" + text);
  }

  //отзывы определенного поставщика
  getBrandName(name: string, dateFrom:  any, dateTo: any){
    console.log(Date.parse(dateFrom)/1000 + " " + Date.parse(dateTo) / 1000);

    return this.http.get(this.rootURL +
      '/feedback?name=' + name +
      '&dateFrom=' + (Date.parse(dateFrom)/1000).toString() +
      '&dateTo=' + (Date.parse(dateTo) / 1000).toString());
  }

  feedbackDeleted: EventEmitter<String> = new EventEmitter<String>();


  // Метод для вызова события удаления отзыва
  deleteFeedback(feedbackId: String) {
    this.feedbackDeleted.emit(feedbackId);
  }

  sendResponse(feedback: any, text: string) {

    const postData = {
      id: feedback.feedback.id,
      text: text,
      brand: feedback.feedback.productDetails.brandName,
      supplier: feedback.feedback.productDetails.supplierName
    };

    this.http.post('/api/feedbacks/send-answer', postData).subscribe(
      (response) => {
        console.log('Ok!' + JSON.stringify(response));
        this.deleteFeedback(feedback.feedback.id);
      },
      (error) => {
        console.log('error! ' + JSON.stringify(error));
      }
    )
  }

  /*getAnswer(selectedFeedback: any): any{
    const postData ={
      product: selectedFeedback.feedback.productDetails.productName,
      brand: selectedFeedback.feedback.productDetails.brandName,
      supplier: selectedFeedback.feedback.productDetails.supplierName
    }

    this.http.post('/api/feedbacks/answer', postData).subscribe(
      (response: any) => {
        console.log('Ok!' + JSON.stringify(response));
        this.responseText.answer = response.answer;
        return response.answer;
      },
      (error) => {
        console.log('error! ' + JSON.stringify(error));
      }
    )
  }*/

  getAnswer(selectedFeedback: any): Observable<any> {
    const postData = {
      product: selectedFeedback.feedback.productDetails.productName,
      brand: selectedFeedback.feedback.productDetails.brandName,
      supplier: selectedFeedback.feedback.productDetails.supplierName
    };

    return this.http.post<string>('/api/feedbacks/answer', postData);
  }
}
