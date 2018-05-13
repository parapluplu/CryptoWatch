import { Component, OnInit } from '@angular/core';

import { TriggerEventsService } from '../shared/triggerevents/triggerevents.service';
import { TokeninfoService } from '../shared/tokeninfo/tokeninfo.service';

import { TriggerEventDetailsComponent } from '../trigger-event-details/trigger-event-details.component';
import { ActivatedRoute } from '@angular/router';
import { CookieService } from 'ngx-cookie';
import { StatsService } from '../shared/stats/stats.service';

const SELECTED_TOKENS_COOKIE: string = "SELECTED_TOKENS";

@Component({
  selector: 'app-trigger-events-list',
  templateUrl: './trigger-events-list.component.html',
  styleUrls: ['./trigger-events-list.component.css']
})
export class TriggerEventsListComponent implements OnInit {
  txnSummary: Array<any>;
  availableTokenInfoes: Array<string>;
  selectedTokens: Array<string>;
  triggerEvents: Array<any>;
  page: number;
  pageSize: number = 20;
  pages: number;
  collectionSize: number;
  updating: boolean = false;


  constructor(private triggerEventsService: TriggerEventsService,
    private tokeninfoService: TokeninfoService,
    private route: ActivatedRoute,
    private cookieService: CookieService,
    private statsService: StatsService
  ) { }

  ngOnInit() {
    this.tokeninfoService.getAll().subscribe(data => {
      this.availableTokenInfoes = data._embedded.tokenInfoes.map(e => e.symbol);
      this.availableTokenInfoes.sort();
      if (this.cookieService.get(SELECTED_TOKENS_COOKIE) == null) {
        this.selectedTokens = this.availableTokenInfoes;
        this.saveCookie(this.selectedTokens);
      } else {
        this.selectedTokens = <Array<string>>this.cookieService.getObject(SELECTED_TOKENS_COOKIE);
      }
      this.update();
    });
  }

  update() {
    this.updating = true;
    this.saveCookie(this.selectedTokens);
    this.triggerEventsService.getForTokens(this.selectedTokens, this.page - 1, this.pageSize).subscribe(data => {
      this.page = data.page.number + 1;
      this.pages = data.page.totalPages;
      this.collectionSize = data.page.totalElements;
      this.triggerEvents = data._embedded.triggerEvents;
      if (this.page > this.pages && this.selectedTokens.length > 0) {
        this.page = this.pages - 1;
        this.update();
      } else {
        this.statsService.getTxnSummary(this.selectedTokens).subscribe(txnSummary => {
          this.txnSummary = txnSummary;
          this.txnSummary.sort(function(a,b) {
            if(a._id.symbol < b._id.symbol) {
              return -1;
            } else if(a._id.symbol > b._id.symbol) {
              return 1;
            }
            return 0;
          });
        });
        this.updating = false;
      }
    });
  }

  toPage(page: number): void {
    this.updating = true;
    this.page = page + 1;
    this.update();
  }

  change() {
    this.updating = true;
    this.page = 1;
    this.update();
  }

  selectAll() {
    this.selectedTokens = [];
    this.availableTokenInfoes.forEach(token => {
      this.selectedTokens.push(token);
    });
    this.change();
  }

  saveCookie(selectedTokens: Array<string>): void {
    const expiryDate = new Date();
    expiryDate.setDate(expiryDate.getDate() + 30);
    this.cookieService.putObject(SELECTED_TOKENS_COOKIE, selectedTokens, {
      expires: expiryDate
    });
  }

}
