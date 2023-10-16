import {isDevMode, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FeedbackComponent} from './feedback/feedback.component';
import {FeedbacksComponent} from './feedbacks/feedbacks.component';
import {StartComponent} from "./start/start.component";
import {HttpClientModule} from "@angular/common/http";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {FormGroup, FormsModule} from "@angular/forms";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import {MatCardModule} from "@angular/material/card";
import {FlexLayoutModule} from "@angular/flex-layout";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatPaginatorModule} from "@angular/material/paginator";
import { AnswerDialogComponent } from './answer-dialog/answer-dialog.component';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatDialogModule} from "@angular/material/dialog";
import {RouterModule, Routes} from "@angular/router";
import {MatCheckboxModule} from "@angular/material/checkbox";
import { AnswerWindowComponent } from './answer-window/answer-window.component';
import {MAT_DATE_LOCALE, MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatSelectModule} from "@angular/material/select";



@NgModule({
  declarations: [
    AppComponent,
    FeedbackComponent,
    FeedbacksComponent,
    AnswerDialogComponent,
    StartComponent,
    AnswerWindowComponent

  ],
    imports: [
        //RouterModule.forRoot(appRoutes),
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        BrowserAnimationsModule,
        MatSlideToggleModule,
        FormsModule,
        MatButtonToggleModule,
        MatProgressSpinnerModule,
        LayoutModule,
        MatToolbarModule,
        MatButtonModule,
        MatSidenavModule,
        MatIconModule,
        MatListModule,
        MatCardModule,
        FlexLayoutModule,
        MatIconModule,
        MatExpansionModule,
        MatPaginatorModule,
        MatFormFieldModule,
        MatDialogModule,
        MatCheckboxModule,
        MatNativeDateModule,
        MatDatepickerModule,
        FormsModule,
        MatSelectModule
    ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'ru-RU' }
  ],
  bootstrap: [AppComponent]
})


export class AppModule { }
