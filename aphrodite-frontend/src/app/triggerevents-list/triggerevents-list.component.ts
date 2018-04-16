import {Component, OnInit} from '@angular/core';

import {TriggerEventsService} from '../shared/triggerevents/triggerevents.service';
import {TokeninfoService} from '../shared/tokeninfo/tokeninfo.service';

import {TriggerEventDetailsComponent} from '../trigger-event-details/trigger-event-details.component';
@Component({
  selector: 'app-triggerevents-list',
  templateUrl: './triggerevents-list.component.html',
  styleUrls: ['./triggerevents-list.component.css']
})
export class TriggereventsListComponent implements OnInit {
  tokenInfoes: any;
  triggerEvents: Array<any>;

  selectedTriggerEvent: any;

  constructor(private triggerEventsService: TriggerEventsService, private tokeninfoService: TokeninfoService) {}

  ngOnInit() {
    this.triggerEventsService.getAll().subscribe(data => {
      this.triggerEvents = data.content;
    });
      this.tokeninfoService.getAll().subscribe(data => {
        this.tokenInfoes = data._embedded.tokenInfoes.map(e => e.symbol);
      });
  }

  onSelect(e: any): void {
    this.selectedTriggerEvent = e;
  }

}
