var canvas = new Image(150,150);
new Window("Arc Sample", canvas);
var ctx = canvas.getContext("2D");

// source: http://developer.mozilla.org/en/docs/Canvas_tutorial:Drawing_shapes


ctx.beginPath();
ctx.arc(75,75,50,0,Math.PI*2,true); // Outer circle
ctx.moveTo(110,75);
ctx.arc(75,75,35,0,Math.PI,false);   // Mouth (clockwise)
ctx.moveTo(65,65);
ctx.arc(60,65,5,0,Math.PI*2,true);  // Left eye
ctx.moveTo(95,65);
ctx.arc(90,65,5,0,Math.PI*2,true);  // Right eye
ctx.stroke();