import $ from 'jquery';
import Rx from 'rxjs/Rx';
/*
Rx.Observable.interval(5000)
    .map(x => 't5=' + x)
    .merge(Rx.Observable.interval(1000).map(y => 't1=' + y))
    .take(10)
    .subscribe(
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
*/
// These are equivalent
var t5$ = Rx.Observable.interval(5000).map(x => 't5=' + x);
var t1$ = Rx.Observable.interval(1000).map(y => 't1=' + y);

var m$ = Rx.Observable.merge(t5$, t1$);

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