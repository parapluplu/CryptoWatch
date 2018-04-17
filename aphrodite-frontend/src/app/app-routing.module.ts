import {TriggerEventDetailsComponent} from './trigger-event-details/trigger-event-details.component';
import {TriggereventsListComponent} from './triggerevents-list/triggerevents-list.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: '/triggerevents', pathMatch: 'full' },
  {path: 'triggerevents', component: TriggereventsListComponent},
  {path: 'triggerevents/:page', component: TriggereventsListComponent},
  {path: 'trigger-event-details/:id', component: TriggerEventDetailsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
  // empty
}

