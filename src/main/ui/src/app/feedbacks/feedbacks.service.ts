import {Component, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class FeedbacksService {

  constructor(private http: HttpClient) { }

  rootURL = '/api';

  getFeedbacks(stars: string, photos: string, name: string, brand: string) {
    return this.http.get(this.rootURL + '/feedbacks?stars=' + stars + '&photos=' + photos + '&name=' + name + '&brand=' + brand);
  }

  //отзывы определенного поставщика
  getBrandName(name: string){
    return this.http.get(this.rootURL + '/feedback?name=' + name);
  }
}
