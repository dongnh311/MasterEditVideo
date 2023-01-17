attribute vec4 position;

const lowp int GAUSSIAN_SAMPLES = 9;

highp float texelWidthOffset = 0.01;
highp float texelHeightOffset = 0.01;
highp float blurSize = 0.3;

varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];

void main() {
  gl_Position = position;
  highp vec2 textureCoordinate = position.xy * 0.5 + 0.5;

  // Calculate the positions for the blur
  int multiplier = 0;
  highp vec2 blurStep;
  highp vec2 singleStepOffset = vec2(texelHeightOffset, texelWidthOffset) * blurSize;

  for (lowp int i = 0; i < GAUSSIAN_SAMPLES; i++) {
     multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));
     // Blur in x (horizontal)
      blurStep = float(multiplier) * singleStepOffset;
      blurCoordinates[i] = textureCoordinate.xy + blurStep;
  }
}
