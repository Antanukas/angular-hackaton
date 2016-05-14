

var websocket = undefined;
var currentStream = undefined;
var ended = true;

function start() {
    ended = false;
    websocket = new WebSocket('wss://stark-river-37161.herokuapp.com/streams/testStream');
    //websocket = new WebSocket('ws://localhost:8080/streams/testStream');
    websocket.binaryType = "arraybuffer";
    websocket.onopen = init;
}

function stop() {
    currentStream.getTracks()[0].stop();
    console.log(currentStream)
}

function init() {

    navigator.getUserMedia({audio: true}, initializeRecorder, errorCallback);
    function initializeRecorder(stream) {
       currentStream = stream;
       currentStream.addEventListener('ended', function() { ended = true;});

       var video = document.querySelector('video');
       //video.src = window.URL.createObjectURL(stream);

       audio_context = new AudioContext;
       sampleRate = audio_context.sampleRate;
       var audioInput = audio_context.createMediaStreamSource(stream);

       var bufferSize = 16384;
       // record only 1 channel
       var recorder = audio_context.createScriptProcessor(bufferSize, 1, 1);
       // specify the processing function
       recorder.onaudioprocess = recorderProcess;
       // connect stream to our recorder
       audioInput.connect(recorder);
       // connect our recorder to the previous destination
       recorder.connect(audio_context.destination);
    }

    function recorderProcess(e) {
      if (ended) return;
      var left = e.inputBuffer.getChannelData(0);
      var data = left;
      console.log(data);
      websocket.send(data);
    }

    function errorCallback(error) {
        console.log(error);
    }
    function convertFloat32ToInt16(buffer) {
      l = buffer.length;
      buf = new Int16Array(l);
      while (l--) {
        buf[l] = Math.min(1, buffer[l])*0x7FFF;
      }
      console.log(typeof buf);
      return buf.buffer;
    }
}