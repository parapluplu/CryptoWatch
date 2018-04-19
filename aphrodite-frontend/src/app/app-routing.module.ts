import {TriggerEventDetailsComponent} from './trigger-event-details/trigger-event-details.component';
import {TriggerEventsListComponent} from './trigger-events-list/trigger-events-list.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { DashboardComponent } from './dashboard/dashboard.component';
import { GraphComponent } from './graph/graph.component';


const routes: Routes = [
  {path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {path: 'dashboard', component: DashboardComponent},
  {path: 'triggerevents', component: TriggerEventsListComponent},
  {path: 'trigger-event-details/:id', component: TriggerEventDetailsComponent},
  {path: 'graph', component: GraphComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes),
            NgbModule],
  exports: [RouterModule]
})
export class AppRoutingModule {
  // empty
}

