import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DatePipe } from '@angular/common';
import { AppRoutingModule } from './app-routing.module';

import { CookieModule } from 'ngx-cookie';


import { AppComponent } from './app.component';

import { TriggerEventsService } from './shared/triggerevents/triggerevents.service';
import { TokeninfoService } from './shared/tokeninfo/tokeninfo.service';
import { TriggerEventsListComponent } from './trigger-events-list/trigger-events-list.component';
import { TriggerEventDetailsComponent } from './trigger-event-details/trigger-event-details.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { GraphComponent } from './graph/graph.component';
import { EnrichedTransferMessageService } from './shared/enriched-transfer-message/enriched-transfer-message.service';


@NgModule({
  declarations: [
    AppComponent,
    TriggerEventsListComponent,
    TriggerEventDetailsComponent,
    DashboardComponent,
    GraphComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    HttpClientModule,
    NgbModule.forRoot(),
    CookieModule.forRoot(),
    AppRoutingModule
  ],
  providers: [
    TriggerEventsService,
    TokeninfoService, 
    EnrichedTransferMessageService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
