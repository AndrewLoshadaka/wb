import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {AnswerDialogComponent} from "../answer-dialog/answer-dialog.component";

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})


export class FeedbackComponent implements OnInit{
  @Input() feedback: any;
  @Input() allChecked: boolean = false;

  @Output() toggleChange = new EventEmitter<void>();
  @Output() checkboxChange = new EventEmitter<{feedbackId: number, checked: boolean}>();

  isImageExpanded: boolean = false;
  checked: boolean = this.allChecked;
  ngOnInit() {
    this.checked = this.allChecked;
  }
  constructor(public dialog: MatDialog) {
  }
  openPhotoModal(images: any[], index: number) {
    this.dialog.open(AnswerDialogComponent, {
      width: '1400px',
      maxHeight: '900px',
      data: {
        images: images,
        index: index
      }
    });
  }
  expandImage() {
    this.isImageExpanded = true;
  }

  shrinkImage() {
    this.isImageExpanded = false;
  }

  setChoose() {
    this.checked = !this.checked;
    this.checkboxChange.emit({checked: this.checked, feedbackId: this.feedback.id});
  }
}
