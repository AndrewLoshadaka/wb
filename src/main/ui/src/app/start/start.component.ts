import { Component} from "@angular/core";
import {Router, RouterModule} from "@angular/router";



@Component({
  selector: 'app-start',
  templateUrl: './start.component.html',
  styleUrls: ['./start.component.css']
})

export class StartComponent {
  constructor(private router: Router) {
  }


  feedbacks() {
    this.router.navigate(['/list']).then(r => {});
  }
}
