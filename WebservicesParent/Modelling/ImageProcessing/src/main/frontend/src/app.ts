import { Component, EventEmitter } from '@angular/core';
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { platformBrowserDynamic } from "@angular/platform-browser-dynamic";
import { SimpleService } from "./services/SimpleService";
import { SimpleComponent } from "./component/SimpleComponent";

@NgModule({
  declarations: [ SimpleComponent ],
  imports: [ BrowserModule ],
  bootstrap: [ SimpleComponent ],
  providers: [ SimpleService ]
})
class AppModule {}

platformBrowserDynamic().bootstrapModule(AppModule);
