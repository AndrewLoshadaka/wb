import {Component, EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class FeedbacksService {

  constructor(private http: HttpClient) { }

  rootURL = '/api';

  getFeedbacks(stars: string, photos: string, video: string, brand: string) {
    return this.http.get(this.rootURL + '/feedbacks?stars=' + stars + '&photos=' + photos + '&video=' + video + '&brand=' + brand);
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

}
