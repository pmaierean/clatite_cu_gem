import $ from 'jquery';
import Rx from 'rxjs/Rx';

const number = [1,22,45,77, 99];

const number$ = Rx.Observable.from(number);

number$.subscribe(
    x => {
        console.log(x);
    },
    err => {
        console.log(err);
    },
    complete => {
        console.log('completed');
    }
)

const posts = [
    {title:'this is title 1', value: 123, z: 1, body: 'This is my first value'},
    {title:'this is title 2', value: 124, z: 2, body: 'This is my second value'},
    {title:'this is title 3', value: 125, z: 3, body: 'This is my third value'},
];

const posts$ = Rx.Observable.from(posts);

posts$.subscribe(
    x => {
        console.log(x);
        $('#posts').append('<li><h3>' + x.title + '</h3><p>' + x.body + '</p></li>')
    },
    err => {
        console.log(err);
    },
    complete => {
        console.log('completed');
    }
);
// From Set
const st = new Set(['123', true, {title:'This is my title', body:'this is my body'}]);
const st$ = Rx.Observable.from(st);
st$.subscribe(
    x => {
        console.log(x);
    },
    err => {
        console.log(err);
    },
    complete => {
        console.log('completed');
    }
);
// From Map
const mp = new Map([[1,2],[3,4],[5,6]]);
const mp$ = Rx.Observable.from(mp);
mp$.subscribe(
    x => {
        console.log(x);
    },
    err => {
        console.log(err);
    },
    complete => {
        console.log('completed');
    }
);