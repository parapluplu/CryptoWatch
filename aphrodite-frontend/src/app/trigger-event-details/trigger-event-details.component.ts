import {TriggerEventsService} from '../shared/triggerevents/triggerevents.service';
import {Component, OnInit, Input} from '@angular/core';
import {Params, ActivatedRoute} from '@angular/router';
import { Location } from '@angular/common';
import { EnrichedTransferMessageService } from '../shared/enriched-transfer-message/enriched-transfer-message.service';


@Component({
  selector: 'app-trigger-event-details',
  templateUrl: './trigger-event-details.component.html',
  styleUrls: ['./trigger-event-details.component.css']
})
export class TriggerEventDetailsComponent implements OnInit {

  error: string;
  trigger: any;
  data: any;
  Math: any;

  constructor(
    private route: ActivatedRoute,
    private enrichedTransferMessageService: EnrichedTransferMessageService,
    private triggerEventsService: TriggerEventsService,
    private location: Location) {
      this.Math = Math;
    }

  ngOnInit() {
    this.getTrigger();
  }

  getData(trigger: any): void {
    if(trigger.eventType == 'txnBasedTriggerEvent') {
      this.enrichedTransferMessageService.getById(trigger.data.txnHash)
        .subscribe(data => {
          this.data = data
        });
    } else {
      this.error = "Not supported";
    }
  }

  getTrigger(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.triggerEventsService.getById(id)
      .subscribe(data => {
         this.trigger = data;
         this.getData(this.trigger);
      });
  }

  cancel() {
    this.location.back();
  }
}
