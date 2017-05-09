import $ from 'jquery';
import Rx from 'rxjs/Rx';

console.log('RxJS Boiler Running...');
var button = $('#myButton');


var myButtonStream$ = Rx.Observable.fromEvent(button,'click');

myButtonStream$.subscribe(
    function(e) {
       console.log(e.target.innerHTML);
    },
    function(err) {
        console.log(err);
    },
    function() {
        console.log('Complete');
    }
);

var input = $('#myInput');
var myInputStream$ = Rx.Observable.fromEvent(input,'keyup');

myInputStream$.subscribe(
    function(e) {
       console.log(e.currentTarget.value);
       $('#typedText').text(e.currentTarget.value);
    },
    function(err) {
        console.log(err);
    },
    function() {
        console.log('Complete');
    }
);

var myMouseMoveStream$ = Rx.Observable.fromEvent(document,'mousemove');

myMouseMoveStream$.subscribe(
    function(e) {
       $('#mouseMove').text('X: ' + e.clientX + ' Y: ' + e.clientY);
    },
    function(err) {
        console.log(err);
    },
    function() {
        console.log('Complete');
    }
);