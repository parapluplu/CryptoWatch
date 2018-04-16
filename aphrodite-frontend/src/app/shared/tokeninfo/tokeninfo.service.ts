import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class TokeninfoService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<any> {
    return this.http.get('//localhost:8001/api/tokenInfoes');
  }

  getById(id: string): Observable<any> {
    return this.http.get('//localhost:8001/api/tokenInfoes/' + id);
  }
}
