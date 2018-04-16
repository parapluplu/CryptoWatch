import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {CommonModule} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { DatePipe } from '@angular/common';


import {AppComponent} from './app.component';

import {TriggerEventsService} from './shared/triggerevents/triggerevents.service';
import {TokeninfoService} from './shared/tokeninfo/tokeninfo.service';
import {TriggereventsListComponent} from './triggerevents-list/triggerevents-list.component';
import {TriggerEventDetailsComponent} from './trigger-event-details/trigger-event-details.component';

import {AppRoutingModule} from './app-routing.module';

@NgModule({
  declarations: [
    AppComponent,
    TriggereventsListComponent,
    TriggerEventDetailsComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    HttpClientModule,
    NgbModule.forRoot(),
    AppRoutingModule
  ],
  providers: [TriggerEventsService,
              TokeninfoService],
  bootstrap: [AppComponent]
})
export class AppModule {}
