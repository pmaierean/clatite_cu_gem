import $ from 'jquery';
import Rx from 'rxjs/Rx';

/*
Rx.Observable.from(['Abc','Bcd'])
    .subscribe(
        x => {
            Rx.Observable.of(x + ' this')
            .subscribe(
                x => {
                    console.log(x);
                }
            );
        }
    );
*/
// Equivalent
Rx.Observable.from(['Abc','Bcd'])
    .mergeMap(
        v => {
            return Rx.Observable.of('The value is ' + v);
        }
    )
    .subscribe(
        x => {
             console.log(x);
       }
    );
