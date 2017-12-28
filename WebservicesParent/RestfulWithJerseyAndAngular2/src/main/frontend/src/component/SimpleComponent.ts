import { Component, EventEmitter, OnInit } from '@angular/core';
import { Product } from "../bo/Product";
import { SimpleService } from "../services/SimpleService";

@Component({
  selector: 'product_display',
  template: `
  <div class="product">
    <ul>
      <li>Name: {{ product.name }} </li>
      <li>Expiration date: {{ product.dt }} </li>
      <li>Price: $ {{ product.price }} </li> 
  </div>
  `
})
export class SimpleComponent implements OnInit {
  product: Product;
  
  constructor(private simpleService: SimpleService) {  
  }

  ngOnInit(): void {
    this.simpleService.getProduct('123').subscribe(
      (r: Product) => { 
        this.product = r;
        console.debug('The product has been downloaded');   
      },
      e => {
        console.error(e);
      }
    );
  }

}
