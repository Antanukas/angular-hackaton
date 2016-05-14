start();

window.AudioContext = window.AudioContext || window.webkitAudioContext;
var context = new AudioContext();
function start() {
    ended = false;

    websocket = new WebSocket('wss://stark-river-37161.herokuapp.com/listeners/testStream');
    //websocket = new WebSocket('ws://localhost:8080/listeners/testStream');
    websocket.binaryType = "arraybuffer";
    websocket.onmessage = function(event) {
        console.log('receiving', new Float32Array(event.data));
        playSound(event.data);
    }
}
function stop() {
}

function playSound(buffer) {
  var array = new Float32Array(buffer);
  audioBuffer = context.createBuffer(1, array.length+1, 44100);
  var source = context.createBufferSource(); // creates a sound source
  audioBuffer.getChannelData(0).set(array);
  source.buffer = audioBuffer;                    // tell the source which sound to play
  source.connect(context.destination);       // connect the source to the context's destination (the speakers)
  source.start(0);                           // play the source now
                                             // note: on older systems, may have to use deprecated noteOn(time);
}