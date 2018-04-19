import {Component, OnInit} from '@angular/core';

import {TriggerEventsService} from '../shared/triggerevents/triggerevents.service';
import {TokeninfoService} from '../shared/tokeninfo/tokeninfo.service';

import {TriggerEventDetailsComponent} from '../trigger-event-details/trigger-event-details.component';
import { ActivatedRoute } from '@angular/router';
import { CookieService } from 'ngx-cookie';

const SELECTED_TOKENS_COOKIE: string = "SELECTED_TOKENS";

@Component({
  selector: 'app-trigger-events-list',
  templateUrl: './trigger-events-list.component.html',
  styleUrls: ['./trigger-events-list.component.css']
})
export class TriggerEventsListComponent implements OnInit {
  availableTokenInfoes: Array<string>;
  selectedTokenInfoes: {};
  triggerEvents: Array<any>;
  page: number;
  pageSize: number = 20;
  pages: number;
  collectionSize: number;
  updating: boolean = false;
  

  constructor(private triggerEventsService: TriggerEventsService, 
    private tokeninfoService: TokeninfoService, 
    private route: ActivatedRoute, 
    private cookieService: CookieService) {}

  ngOnInit() {
    this.tokeninfoService.getAll().subscribe(data => {
      this.availableTokenInfoes = data._embedded.tokenInfoes.map(e => e.symbol);
      if(this.cookieService.get(SELECTED_TOKENS_COOKIE) == null) {
        this.selectedTokenInfoes = {};
        this.availableTokenInfoes.forEach(token => {
          this.selectedTokenInfoes[token] = true;
        });
        this.cookieService.putObject(SELECTED_TOKENS_COOKIE, this.selectedTokenInfoes);
      } else {
        this.selectedTokenInfoes = <Map<string, boolean>> this.cookieService.getObject(SELECTED_TOKENS_COOKIE);
      }
      this.update();
    });
  }

  update() {
    this.updating = true;
    const tokens = new Array();
    Object.entries(this.selectedTokenInfoes).forEach(([key, checked]) => {
      if(checked === true) {
        tokens.push(key);
      }
    });
    this.triggerEventsService.getForTokens(tokens, this.page-1, this.pageSize).subscribe(data => {
      this.page = data.page.number+1;
      this.pages = data.page.totalPages;
      this.collectionSize=data.page.totalElements;
      this.triggerEvents = data._embedded.triggerEvents;
      if(this.page > this.pages) {
        this.page = this.pages-1;
        this.update();
      } else {
        this.updating = false;
      }
    });
  }

  toPage(page: number): void {
    this.updating = true;
    this.page = page+1;
    this.update();
  }

  changeToken(token: string, event): void {
    this.updating = true;
    this.selectedTokenInfoes[token] = event.target.checked;
    this.cookieService.putObject(SELECTED_TOKENS_COOKIE, this.selectedTokenInfoes);
    this.page = 1;
    this.update();
  }

}
