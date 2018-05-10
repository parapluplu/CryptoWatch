import {TriggerEventDetailsComponent} from './trigger-event-details/trigger-event-details.component';
import {TriggerEventsListComponent} from './trigger-events-list/trigger-events-list.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { DashboardComponent } from './dashboard/dashboard.component';
import { StatsComponent } from './stats/stats.component';
import { AboutComponent } from './about/about.component';
import { ContactComponent } from './contact/contact.component';


const routes: Routes = [
  {path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {path: 'dashboard', component: DashboardComponent},
  {path: 'triggerevents', component: TriggerEventsListComponent},
  {path: 'trigger-event-details/:id', component: TriggerEventDetailsComponent},
  {path: 'stats', component: StatsComponent},
  {path: 'about', component: AboutComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes),
            NgbModule],
  exports: [RouterModule]
})
export class AppRoutingModule {
  // empty
}

