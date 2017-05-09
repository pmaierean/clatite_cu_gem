import $ from 'jquery';
import Rx from 'rxjs/Rx';
/*
const withInterval$ = Rx.Observable.interval(1000)
    .take(10)
    .map(v => v * 2);

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

const array$ = Rx.Observable.from(['Ion', 'Gheorghe', 'Vasile'])
    .map(v => v.toUpperCase())
    .map(v => 'I am ' + v);

array$.subscribe(
    x => {
        console.log(x);
    },
    error => {
        console.log(error);
    },
    complete => {
        console.log('Array complete');
    }
)
*/
function getData(userName) {
    return $.ajax({
        url: 'https://api.github.com/users/' + userName,
        dateType: 'jsonp'
    }).promise();
}
/*
Rx.Observable.fromPromise(getData('pmaierean'))
    .map( user => user.name )
    .subscribe(
        user => {
            console.log(user);
        },
        error => {
            console.log(error);
        },
        complete => {
            console.log('Complete');
        }
    );
*/
// These two are equivalent
Rx.Observable.fromPromise(getData('pmaierean'))
    .pluck( 'name' )
    .subscribe(
        user => {
            console.log(user);
        },
        error => {
            console.log(error);
        },
        complete => {
            console.log('Complete');
        }
    );
