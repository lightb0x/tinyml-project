/*
  This sketch reads a raw Stream of RGB565 pixels
  from the Serial port and displays the frame on
  the window.

  Use with the Examples -> CameraCaptureRawBytes Arduino sketch.

  This example code is in the public domain.
*/

import processing.serial.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

Serial myPort;

// must match resolution used in the sketch
final int cameraWidth = 96;
final int cameraHeight = 96;

// grayscale
final int cameraBytesPerPixel = 1;

final int bytesPerFrame = cameraWidth * cameraHeight * cameraBytesPerPixel;

PImage myImage;

void setup()
{
  size(96, 96);

  // if you have only ONE serial port active
  //myPort = new Serial(this, Serial.list()[0], 9600); // if you have only ONE serial port active

  // if you know the serial port name
  //myPort = new Serial(this, "COM5", 9600);                    // Windows
  //myPort = new Serial(this, "/dev/ttyACM0", 9600);             // Linux
  myPort = new Serial(this, "/dev/cu.usbmodem14201", 9600);  // Mac

  // wait for full frame of bytes
  myPort.buffer(bytesPerFrame);  

  myImage = createImage(cameraWidth, cameraHeight, RGB);
}

void draw()
{
  image(myImage, 0, 0);
}

void serialEvent(Serial myPort) {
  byte[] frameBuffer = new byte[bytesPerFrame];

  // read the saw bytes in
  myPort.readBytes(frameBuffer);

  // create image to set byte values
  PImage img = createImage(cameraWidth, cameraHeight, RGB);

  // access raw bytes via byte buffer
  ByteBuffer bb = ByteBuffer.wrap(frameBuffer);
  bb.order(ByteOrder.BIG_ENDIAN);

  int i = 0;

  img.loadPixels();
  
  while (bb.hasRemaining()) {
    byte g = bb.get(); // read signed byte value
    int ng;
    // convert from -128 ~ 127 to 0 ~ 255
    if (g < 0) {
      ng = (int)g + 256;
    } else {
      ng = (int)g;
    }
    // set pixel color
    img.pixels[i++] = color(ng);
  }
  img.updatePixels();

  // assign image for next draw
  myImage = img;
}
