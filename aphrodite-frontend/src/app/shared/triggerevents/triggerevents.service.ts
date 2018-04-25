import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import { environment } from '../../../environments/environment.prod';

@Injectable()
export class TriggerEventsService {

  getForTokens(tokens: Array<string>, page: number, pageSize: number): any {
    return this.http.get(environment.apiUrl + 'triggerEvents/search/findByTokens', {
      params: {
        tokenSymbols: tokens.join(','),
        page: page.toString(),
        size: pageSize.toString(),
        sort: 'date,desc'
      }
    });
  }
  constructor(private http: HttpClient) {}

  getAll(): Observable<any> {
    return this.http.get(environment.apiUrl + '/triggerEvents/all?sort=date,desc');
  }

  getById(id: string): Observable<any> {
    return this.http.get(environment.apiUrl + '/triggerEvents/' + id);
  }
}
