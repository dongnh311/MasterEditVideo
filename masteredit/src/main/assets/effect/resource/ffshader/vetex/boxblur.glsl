attribute vec4 position;

highp float texelWidthOffset = 0.003;
highp float texelHeightOffset = 0.003;
highp float blurSize = 2.0;

varying highp vec2 centerTextureCoordinate;
varying highp vec2 oneStepLeftTextureCoordinate;
varying highp vec2 twoStepsLeftTextureCoordinate;
varying highp vec2 oneStepRightTextureCoordinate;

varying highp vec2 twoStepsRightTextureCoordinate;

void main() {
  gl_Position = position;

  vec2 firstOffset = vec2(1.5 * texelWidthOffset, 1.5 * texelHeightOffset) * blurSize;
  vec2 secondOffset = vec2(3.5 * texelWidthOffset, 3.5 * texelHeightOffset) * blurSize;

  centerTextureCoordinate = position.xy * 0.5 + 0.5;
  oneStepLeftTextureCoordinate = centerTextureCoordinate - firstOffset;
  twoStepsLeftTextureCoordinate = centerTextureCoordinate - secondOffset;
  oneStepRightTextureCoordinate = centerTextureCoordinate + firstOffset;
  twoStepsRightTextureCoordinate = centerTextureCoordinate + secondOffset;
}
