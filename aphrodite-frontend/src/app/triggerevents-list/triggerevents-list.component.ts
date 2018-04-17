import {Component, OnInit} from '@angular/core';

import {TriggerEventsService} from '../shared/triggerevents/triggerevents.service';
import {TokeninfoService} from '../shared/tokeninfo/tokeninfo.service';

import {TriggerEventDetailsComponent} from '../trigger-event-details/trigger-event-details.component';
import { ActivatedRoute } from '@angular/router';
import { CookieService } from 'ngx-cookie';

const SELECTED_TOKENS_COOKIE: string = "SELECTED_TOKENS";

@Component({
  selector: 'app-triggerevents-list',
  templateUrl: './triggerevents-list.component.html',
  styleUrls: ['./triggerevents-list.component.css']
})
export class TriggereventsListComponent implements OnInit {
  availableTokenInfoes: Array<string>;
  selectedTokenInfoes: {};
  triggerEvents: Array<any>;
  selectedPage: number;
  pageSize: number = 20;
  pages: number;
  

  constructor(private triggerEventsService: TriggerEventsService, 
    private tokeninfoService: TokeninfoService, 
    private route: ActivatedRoute, 
    private cookieService: CookieService) {}

  ngOnInit() {
    const optionalPageParameter = this.route.snapshot.paramMap.get('page');
    if(optionalPageParameter) {
      this.selectedPage = parseInt(optionalPageParameter);
    } else {
      this.selectedPage = 0;
    }
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
    const tokens = new Array();
    Object.entries(this.selectedTokenInfoes).forEach(([key, checked]) => {
      if(checked === true) {
        tokens.push(key);
      }
    });
    this.triggerEventsService.getForTokens(tokens, this.selectedPage, this.pageSize).subscribe(data => {
      this.selectedPage = data.page.number;
      this.pages = data.page.totalPages;
      this.triggerEvents = data._embedded.triggerEvents;
    });
  }

  toPage(page: number): void {
    console.log('change');
    this.selectedPage = page;
    this.update();
  }

  changeToken(token: string, event): void {
    this.selectedTokenInfoes[token] = event.target.checked;
    this.cookieService.putObject(SELECTED_TOKENS_COOKIE, this.selectedTokenInfoes);
    this.update();
  }

}
