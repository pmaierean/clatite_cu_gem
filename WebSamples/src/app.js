import $ from 'jquery';
import Rx from 'rxjs/Rx';

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