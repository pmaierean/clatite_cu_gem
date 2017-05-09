import $ from 'jquery';
import Rx from 'rxjs/Rx';

const source$ = new Rx.Observable(observer => {
    console.log('Creating the observable');
    observer.next('This is one value');
    observer.next('This is the second value');
    observer.next('This is the third value');
    setTimeout(() => {
        observer.next('After the wait time');
        observer.complete();
    }, 3000);
});

source$.subscribe(
    x => {
        console.log(x);
    },
    err => {
        console.log(err);
    },
    complete => {
        console.log('Completed');
    }
);

const source2$ = new Rx.Observable(observer => {
    console.log('Creating the observable 2');
    observer.next('This is one value 2');
    observer.next('This is the second value 2');
    observer.next('This is the third value 2');

    observer.error(new Error('Something went wrong 2'));
    setTimeout(() => {
        observer.next('After the wait time 2');
        observer.complete();
    }, 3000);
});

source2$.catch(
    err => Rx.Observable.of(err)
).subscribe(
    x => {
        console.log(x);
    },
    err => {
        console.log(err);
    },
    complete => {
        console.log('Completed');
    }
);