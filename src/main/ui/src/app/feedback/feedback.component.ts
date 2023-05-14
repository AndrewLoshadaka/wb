import {Component, Input, Output} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {AnswerDialogComponent} from "../answer-dialog/answer-dialog.component";

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})


export class FeedbackComponent {

  @Input() feedback: any;
  checked: boolean = false;

  constructor(public dialog: MatDialog) {
  }


  openDialog(): void {
    const dialogRef = this.dialog.open(AnswerDialogComponent, {
      data: {name: this.feedback.text},
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  sendAnswerOnFeedbacks() {

  }
}
