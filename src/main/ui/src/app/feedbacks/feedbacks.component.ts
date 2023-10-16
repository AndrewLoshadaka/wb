import {Component, ViewChildren, QueryList, AfterViewInit, OnInit} from '@angular/core';
import {FormGroup, FormControl} from '@angular/forms';
import {FeedbacksService} from "./feedbacks.service";
import {PageEvent} from "@angular/material/paginator";
import {FeedbackComponent} from "../feedback/feedback.component";
import {MatDialog} from "@angular/material/dialog";
import {AnswerWindowComponent} from "../answer-window/answer-window.component";
import { MAT_DATE_LOCALE } from '@angular/material/core';
import {AnswerDialogComponent} from "../answer-dialog/answer-dialog.component";
@Component({
  selector: 'app-feedbacks',
  templateUrl: './feedbacks.component.html',
  styleUrls: ['./feedbacks.component.css'],
})

export class FeedbacksComponent implements AfterViewInit, OnInit {
  @ViewChildren(FeedbackComponent) feedbackComponents!: QueryList<FeedbackComponent>;


  constructor(private feedbacksService: FeedbacksService, public dialog: MatDialog) {
      this.feedbacksService.feedbackDeleted.subscribe((feedbackId: string) => {
        // Удалит отзыв из массива allFeedbacks
        this.allFeedbacks = this.allFeedbacks.filter(feedback => feedback.id !== feedbackId);
        this.currentFeedbacks = this.currentFeedbacks.filter(feedback => feedback.id !== feedbackId);
        this.currentFeedbacks = this.allFeedbacks.slice(this.pageSize * this.pageIndex,
          this.pageSize * this.pageIndex + this.pageSize)
    });

    this.endDate = new Date();
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);
    this.startDate = oneWeekAgo;
  }

  ngOnInit() {

  }

  openModal() {
    this.dialog.open(AnswerWindowComponent, {
      width: '1400px',
      height: '800px',
      data: {
        feedbacks: this.selectedFeedbacks,
        templates: this.templates
      }
    });
  }


  allBrandName: string[] = [];
  brands: string = "";
  name = "";
  allFeedbacks: any[] = [];
  templates: any[] = [];


  showClick: boolean = false;
  stars: number[] = [];
  photos = "";
  video = "";
  text = "";
  showFilters: boolean = false;

  currentFeedbacks: any[] = [];
  selectedFeedbacks: any[] = [];
  allChecked = false;

  pageSize: number = 25;
  pageIndex: number = 0;

  startDate: Date | null = null;
  endDate: Date | null = null;

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
    this.allChecked = false
    this.showClick = true;
    this.allFeedbacks = [];

    this.feedbacksService.getFeedbacks(this.stars, this.photos, this.video, this.brands, this.text)
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
    this.selectedFeedbacks = this.feedbackComponents.filter(feedback => feedback.checked);
    this.feedbacksService.getTemplateAnswer().subscribe((data: any) => {
      this.templates = data;
    });
    this.openModal();
  }
  onCheckboxChange (event: { feedbackId: number, checked: boolean }) {
    const feedbackToUpdate = this.allFeedbacks.find(feedback => feedback.id === event.feedbackId);
    if (feedbackToUpdate) {
        feedbackToUpdate.checked = event.checked;
    }
  }

  setAllChoose() {
    this.allChecked = !this.allChecked;
    if (this.allChecked) {
      this.selectedFeedbacks = this.currentFeedbacks;
      this.feedbackComponents.forEach((feedbackComponent) => {
        if(!feedbackComponent.checked)
          feedbackComponent.setChoose();
      });
    }
    else {
      this.feedbackComponents.forEach((feedbackComponent) => {
        if(feedbackComponent.checked)
          feedbackComponent.setChoose();
      });
    }
  }


  autoAnswerOnFeedbacks() {
    this.selectedFeedbacks = this.feedbackComponents.filter(feedback => feedback.checked);
    this.selectedFeedbacks.forEach((feedback) => {
      this.feedbacksService.getAnswer(feedback).subscribe(
        (response) => {
          this.feedbacksService.sendResponse(feedback, response.answer)
        },
        (error) => {
          console.log('Error getting answer: ' + JSON.stringify(error));
        }
      );
    });
  }
}
