import { Component } from '@angular/core';
import {FeedbacksService} from "./feedbacks.service";
import {PageEvent} from "@angular/material/paginator";
import {FeedbackComponent} from "../feedback/feedback.component";

@Component({
  selector: 'app-feedbacks',
  templateUrl: './feedbacks.component.html',
  styleUrls: ['./feedbacks.component.css']
})
export class FeedbacksComponent {

  constructor(private feedbacksService: FeedbacksService) {}

  allBrandName: string[] = [];
  brands: string = "";
  name = "";
  allFeedbacks: any[] = [];


  showClick: boolean = false;
  stars = "";
  photos = "";
  video = "true";
  showFilters: boolean = false;
  currentFeedbacks: FeedbackComponent[] = [];
  currentChoseFeedback: boolean [] = [];
  pageSize: number = 25;
  pageIndex: number = 0;

  getBrands(){
    this.showClick = false;
    this.allBrandName = [];
    this.feedbacksService.getBrandName(this.name).subscribe((v: any) => {
      this.allBrandName = v;
    });
    this.showFilters = true;
  }


  reloadFeedbacks() {
    this.showClick = true;
    this.allFeedbacks = [];
    this.feedbacksService.getFeedbacks(this.stars, this.photos, this.name, this.brands)
      .subscribe((v: any) => {
        this.allFeedbacks = v;
        this.pageSize = 25;
        this.pageIndex = 0;


        for(let i = 0; i < v.length; i++) {
          this.currentChoseFeedback[i] = false;
        }

        this.currentFeedbacks = this.allFeedbacks.slice(this.pageSize * this.pageIndex,
          this.pageSize * this.pageIndex + this.pageSize)
      });
  }

  getFeedbacksCount() {
    return this.allFeedbacks.length;
  }

  handlePageEvent(e: PageEvent) {
    this.pageSize = e.pageSize;
    this.pageIndex = e.pageIndex;
    this.currentFeedbacks = this.allFeedbacks.slice(this.pageSize * this.pageIndex,
      this.pageSize * this.pageIndex + this.pageSize)
  }

  sendAnswerOnFeedbacks() {
    for(let i = 0; i < this.currentChoseFeedback.length; i++){
      console.log(this.currentFeedbacks[i].checked);
    }
  }

  setChoose(){

  }
}
