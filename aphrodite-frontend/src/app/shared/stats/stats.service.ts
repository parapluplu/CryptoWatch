import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import { environment } from '../../../environments/environment';

@Injectable()
export class StatsService {

  constructor(private http: HttpClient) {}

  getTxnSummary(tokens: Array<string>): Observable<any> {
    return this.http.get(environment.statApiUrl + '/txnSummary', {
      params: {
        tokenSymbols: tokens.join(',')
      }
    });
  }
}
