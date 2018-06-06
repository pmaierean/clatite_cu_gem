import { Product } from '../bo/Product';
import { Configuration } from '../services/Configuration';
import { BaseConverter } from './BaseConverter';
import { Response } from '@angular/http';

export class ProductConverter extends BaseConverter {
  
  constructor(configuration: Configuration) {
    super(configuration);
  }

  convert(res:Response) : Product {
    super.handleError(res);  
    let d = res.json(); 
    let name: string = d.name;
    let dt: Date = new Date(d.dt);
    let price: number = d.price;
    return new Product(name, dt, price);
  }
}