import { Injectable } from '@angular/core';
import { Http, Response, RequestOptions, Headers } from '@angular/http';
import { Observable } from 'rxjs';
import { Product } from '../bo/Product';
import { BaseService } from './BaseService';
import { Configuration } from './Configuration';
import { ProductConverter } from '../converter/ProductConverter';

@Injectable()
export class SimpleService extends BaseService { 
  converter: ProductConverter;
  http: Http;
  constructor(http: Http, configuration: Configuration) {
    super(configuration);
    this.http = http;
    this.converter = new ProductConverter(configuration);
  }
  
  public getProduct(criteria: string): Observable<Product> {
    let url = super.replacePlaceholder(this.baseUrl + '/getProduct/{0}', [ criteria ]);
    let body = '';
    let headers = new Headers({ 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'});
    let options = new RequestOptions({ headers: headers });
    return this.http.post(url, body, options)
      .catch(e => this.converter.handleError(e))
      .map((res:Response) => { return this.converter.convert(res) });
  }
  
}