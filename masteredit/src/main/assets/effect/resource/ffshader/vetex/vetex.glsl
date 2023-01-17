attribute vec2 position;
varying vec2 textureCoordinate;
  void main(void) {
      gl_Position = vec4(position, 0, 1);
      textureCoordinate = position;
  }