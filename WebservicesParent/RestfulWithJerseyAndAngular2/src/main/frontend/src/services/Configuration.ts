declare var attributes : any;
declare var i18n : any;

export class Configuration {
  attributes: Map<string, string>;
  i18n: Map<string, string>;
  baseUrl: string;
 
  constructor() {
    this.i18n = this.convert2map(i18n);
    this.attributes = this.convert2map(attributes);
    this.baseUrl = this.attributes.get('baseUrl');
  }
 
  public getText(key:any): String {
        return this.i18n[key];
    }
 
  convert2map(a : any) : Map<string, string> {
    let ret = new Map<string, string>();
        Object.keys(i18n).forEach((key:PropertyKey) => {
            let desc: PropertyDescriptor = Object.getOwnPropertyDescriptor(i18n, key);
            let value: String = desc.value;
            ret[key.toString()] = value;
        });
    return ret;
  }
}