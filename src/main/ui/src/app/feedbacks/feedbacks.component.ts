import { Component } from '@angular/core';
import {FeedbacksService} from "./feedbacks.service";
import {PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-feedbacks',
  templateUrl: './feedbacks.component.html',
  styleUrls: ['./feedbacks.component.css']
})
export class FeedbacksComponent {

  constructor(private feedbacksService: FeedbacksService) {}

  allFeedbacks: any[] = [];
  stars = "5";
  photos = "true";
  currentFeedbacks: any[] = [];
  pageSize: number = 25;
  pageIndex: number = 0;


  ngOnInit() {
    console.log("INIT")
    this.reloadFeedbacks();
  }

  reloadFeedbacks() {
    this.allFeedbacks = [];
    this.feedbacksService.getFeedbacks(this.stars, this.photos)
      .subscribe((v: any) => {
        this.allFeedbacks = v;
        this.pageSize = 25;
        this.pageIndex = 0;
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
}
