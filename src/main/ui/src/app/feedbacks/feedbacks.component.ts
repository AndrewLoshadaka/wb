import { Component, ViewChildren, QueryList, AfterViewInit } from '@angular/core';
import {FeedbacksService} from "./feedbacks.service";
import {PageEvent} from "@angular/material/paginator";
import {FeedbackComponent} from "../feedback/feedback.component";
import {MatDialog} from "@angular/material/dialog";
import {AnswerWindowComponent} from "../answer-window/answer-window.component";

@Component({
  selector: 'app-feedbacks',
  templateUrl: './feedbacks.component.html',
  styleUrls: ['./feedbacks.component.css']
})
export class FeedbacksComponent implements AfterViewInit {
  @ViewChildren(FeedbackComponent) feedbackComponents!: QueryList<FeedbackComponent>;


  constructor(private feedbacksService: FeedbacksService, public dialog: MatDialog) {}

  openModal() {
    this.dialog.open(AnswerWindowComponent, {
      width: '1400px', // Указать желаемую ширину модального окна
      height: '800px',
      data: this.selectedFeedbacks
    });
  }


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
  selectedFeedbacks: any[] = [];

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

  ngAfterViewInit() {
    this.currentFeedbacks = this.allFeedbacks.slice(
      this.pageSize * this.pageIndex,
      this.pageSize * this.pageIndex + this.pageSize
    );
  }

  sendAnswerOnFeedbacks() {
    //создает массив с отзывами которые выбраны
    this.selectedFeedbacks = this.feedbackComponents.filter(feedback => feedback.checked);
    this.openModal();
  }

  onCheckboxChange (event: { feedbackId: number, checked: boolean }) { //вызывается при изменении чекбокса в дочернем
    const feedbackToUpdate = this.allFeedbacks.find(feedback => feedback.id === event.feedbackId); //находит среди всех отзывов отзыв с id выбранного
    if (feedbackToUpdate) {
        feedbackToUpdate.checked = event.checked;
    }
  }



  protected readonly console = console;
}
