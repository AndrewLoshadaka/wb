import {Component, Input, Output, EventEmitter} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {AnswerDialogComponent} from "../answer-dialog/answer-dialog.component";

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})


export class FeedbackComponent {
  @Input() feedback: any;
  @Output() checkboxChange = new EventEmitter<{feedbackId: number, checked: boolean}>();
  isImageExpanded: boolean = false;

  checked: boolean = false;


  constructor(public dialog: MatDialog) {}

  openDialog(): void {
    const dialogRef = this.dialog.open(AnswerDialogComponent, {
      data: {name: this.feedback.text},
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
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
    this.checkboxChange.emit({ feedbackId: this.feedback.id, checked: this.checked });
  }
}
