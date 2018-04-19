import {TriggerEventsService} from '../shared/triggerevents/triggerevents.service';
import {Component, OnInit, Input} from '@angular/core';
import {Params, ActivatedRoute} from '@angular/router';
import { Location } from '@angular/common';


@Component({
  selector: 'app-trigger-event-details',
  templateUrl: './trigger-event-details.component.html',
  styleUrls: ['./trigger-event-details.component.css']
})
export class TriggerEventDetailsComponent implements OnInit {

  data: any;
  Math: any;

  constructor(
    private route: ActivatedRoute,
    private triggerEventsService: TriggerEventsService,
    private location: Location) {
      this.Math = Math;
    }

  ngOnInit() {
    this.getData();
  }

  getData(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.triggerEventsService.getById(id)
      .subscribe(data => this.data = data);
  }

  cancel() {
    this.location.back();
  }
}
