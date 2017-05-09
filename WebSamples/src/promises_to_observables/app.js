import $ from 'jquery';
import Rx from 'rxjs/Rx';

// From a Promise

const myPromise = new Promise((resolve, reject) => {
    console.log('Creating the promise');
    setTimeout(() => {
        resolve('Hello from promise');
    }, 3000);
});
/*
myPromise.then(x => {
    console.log(x);
});
*/
const myPromise$ = Rx.Observable.fromPromise(myPromise);
myPromise$.subscribe(
    x => { 
        console.log(x) 
    },
    err => {
        console.log(err);
    },
    complete => {
        console.log('Complete');
    }
);

function getData(userName) {
    return $.ajax({
        url: 'https://api.github.com/users/' + userName,
        dateType: 'jsonp'
    }).promise();
}

Rx.Observable.fromPromise(getData('pmaierean'))
    .subscribe(
        x => {
            console.log(x);
        },
        error => {
            console.log(error);
        },
        complete => {
            console.log('Complete');
        }
    );