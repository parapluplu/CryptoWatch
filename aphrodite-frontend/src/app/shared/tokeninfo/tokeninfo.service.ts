import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import { environment } from '../../../environments/environment';

@Injectable()
export class TokeninfoService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<any> {
    return this.http.get(environment.apiUrl + '/api/tokenInfoes');
  }

  getById(id: string): Observable<any> {
    return this.http.get(environment.apiUrl + '/api/tokenInfoes/' + id);
  }
}
