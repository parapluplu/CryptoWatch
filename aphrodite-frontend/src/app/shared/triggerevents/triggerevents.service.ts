import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class TriggerEventsService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<any> {
    return this.http.get('//localhost:8001/triggerEvents/all?sort=date,desc');
  }

  getById(id: string): Observable<any> {
    return this.http.get('//localhost:8001/api/triggerEvents/' + id);
  }
}
