import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {FeedbacksComponent} from "./feedbacks/feedbacks.component";

const routes: Routes = [
  {path: 'list', component: FeedbacksComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
