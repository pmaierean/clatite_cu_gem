import $ from 'jquery';
import Rx from 'rxjs/Rx';

const withRange$ = Rx.Observable.range(0, 10);

withRange$.subscribe(
    x => {
        console.log('Range: ' + x);
    },
    error => {
        console.log(error);
    },
    complete => {
        console.log('Range complete');
    }
)

const withInterval$ = Rx.Observable.interval(1000).take(10);

withInterval$.subscribe(
    x => {
        console.log('Interval: ' + x);
    },
    error => {
        console.log(error);
    },
    complete => {
        console.log('Interval complete');
    }
)

const withTimer$ = Rx.Observable.timer(10000, 500).take(10);

withTimer$.subscribe(
    x => {
        console.log('Timer: ' + x);
    },
    error => {
        console.log(error);
    },
    complete => {
        console.log('Timer complete');
    }
)