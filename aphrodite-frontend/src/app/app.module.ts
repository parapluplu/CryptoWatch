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
import { StatsComponent } from './stats/stats.component';
import { EnrichedTransferMessageService } from './shared/enriched-transfer-message/enriched-transfer-message.service';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { AdsenseModule } from 'ng2-adsense';
import { AboutComponent } from './about/about.component';
import { ContactComponent } from './contact/contact.component';
import { MatIconModule} from "@angular/material";
import { TriggerInfoComponent } from './trigger-info/trigger-info.component';
import { StatsService } from './shared/stats/stats.service';


@NgModule({
  declarations: [
    AppComponent,
    TriggerEventsListComponent,
    TriggerEventDetailsComponent,
    DashboardComponent,
    StatsComponent,
    AboutComponent,
    ContactComponent,
    TriggerInfoComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    HttpClientModule,
    NgbModule.forRoot(),
    CookieModule.forRoot(),
    AppRoutingModule,
    NgSelectModule,
    FormsModule,
    AdsenseModule.forRoot({
      adClient: 'ca-pub-6710819307817843'
    }),
    MatIconModule
  ],
  providers: [
    TriggerEventsService,
    TokeninfoService, 
    EnrichedTransferMessageService,
    StatsService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
