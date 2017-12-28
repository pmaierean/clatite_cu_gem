import { Observable } from 'rxjs/Rx';
import { Configuration } from './Configuration';

export abstract class BaseService {
  re : RegExp = new RegExp('(\d)?\}');
  baseUrl: string = '';
  
  constructor(config: Configuration) {
    this.baseUrl = config.baseUrl;
  }
  
  public replacePlaceholder(template: string, args: string[]) : string {
    let ret = '';
    Observable.from(template.split('\{')).subscribe(
      x => {
        if (this.re.test(x)) {
          let m = x.indexOf('}');
          let ix = parseInt(x.substring(0, m));
          if (args.length > ix)
            ret = ret + args[ix];
          else
            ret = ret + 'unresolved[' + ix + ']';
          ret = ret + x.substring(m+1);         
        }
        else
          ret = ret + x;
      },
      err => {
        console.log(err);
      }
    );
    return ret;
  }
}