import { Component, ViewChildren, QueryList, AfterViewInit } from '@angular/core';
import {FormGroup, FormControl} from '@angular/forms';
import {FeedbacksService} from "./feedbacks.service";
import {PageEvent} from "@angular/material/paginator";
import {FeedbackComponent} from "../feedback/feedback.component";
import {MatDialog} from "@angular/material/dialog";
import {AnswerWindowComponent} from "../answer-window/answer-window.component";
import { MAT_DATE_LOCALE } from '@angular/material/core';
@Component({
  selector: 'app-feedbacks',
  templateUrl: './feedbacks.component.html',
  styleUrls: ['./feedbacks.component.css'],
})

export class FeedbacksComponent implements AfterViewInit {
  @ViewChildren(FeedbackComponent) feedbackComponents!: QueryList<FeedbackComponent>;


  constructor(private feedbacksService: FeedbacksService, public dialog: MatDialog) {
      this.feedbacksService.feedbackDeleted.subscribe((feedbackId: string) => {
        // Удалит отзыв из массива allFeedbacks
        this.allFeedbacks = this.allFeedbacks.filter(feedback => feedback.id !== feedbackId);
        this.currentFeedbacks = this.currentFeedbacks.filter(feedback => feedback.id !== feedbackId);
        this.currentFeedbacks = this.allFeedbacks.slice(this.pageSize * this.pageIndex,
          this.pageSize * this.pageIndex + this.pageSize)
    });
    this.console.log(this.allFeedbacks.length);

    this.endDate = new Date();
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);
    this.startDate = oneWeekAgo;
  }

  openModal() {
    this.dialog.open(AnswerWindowComponent, {
      width: '1400px',
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
  video = "";
  showFilters: boolean = false;

  currentFeedbacks: any[] = [];
  selectedFeedbacks: any[] = [];

  pageSize: number = 25;
  pageIndex: number = 0;

  startDate: Date | null = null;
  endDate: Date | null = null;

  range = new FormGroup({
    start: new FormControl<Date | null>(null),
    end: new FormControl<Date | null>(null),
  });

  onDateRangeSelected(event: any) {
    if (event.value) {
      this.startDate = event.value.start;
      this.endDate = event.value.end;
    }
  }


  getBrands(){
    this.showClick = false;
    this.allBrandName = [];
    this.feedbacksService.getBrandName(this.name, this.startDate, this.endDate).subscribe((v: any) => {
      this.allBrandName = v;
    });

    this.showFilters = true;
  }


  reloadFeedbacks() {

    this.showClick = true;
    this.allFeedbacks = [];

    this.feedbacksService.getFeedbacks(this.stars, this.photos, this.video, this.brands)
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
