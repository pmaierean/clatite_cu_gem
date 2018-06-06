import { Observable } from 'rxjs/Rx';
import { Configuration } from '../services/Configuration';
import { Response } from '@angular/http';

export abstract class BaseConverter {
  
  constructor(configuration: Configuration) {
 
  }
 
  public handleError(r: any) : any {
    if (r instanceof Response) {
      if (r.status == 401) {
        window.location.reload(true);
        Observable.throw('Unauthorized');
      }
    }
    else
      Observable.throw(r);
    return r; 
  }
}