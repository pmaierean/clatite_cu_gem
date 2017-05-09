import $ from 'jquery';
import Rx from 'rxjs/Rx';

var r1$ = Rx.Observable.range(0, 5).map(x => 'r1=' + x);
var r2$ = Rx.Observable.range(6, 11).map(y => 'r2=' + y);

var m$ = Rx.Observable.concat(r1$, r2$);

m$.subscribe(
        x => {
            console.log(x);
        },
        error => {
            console.log(error);
        },
        complete => {
            console.log('complete');
        }
    )
