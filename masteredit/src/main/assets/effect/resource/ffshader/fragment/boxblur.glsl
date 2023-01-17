precision mediump float;

uniform lowp sampler2D inputImageTexture;

varying highp vec2 centerTextureCoordinate;
varying highp vec2 oneStepLeftTextureCoordinate;
varying highp vec2 twoStepsLeftTextureCoordinate;
varying highp vec2 oneStepRightTextureCoordinate;
varying highp vec2 twoStepsRightTextureCoordinate;

void main() {
    lowp vec4 color = texture2D(inputImageTexture, centerTextureCoordinate) * 0.2;
    color += texture2D(inputImageTexture, oneStepLeftTextureCoordinate) * 0.2;
    color += texture2D(inputImageTexture, oneStepRightTextureCoordinate) * 0.2;
    color += texture2D(inputImageTexture, twoStepsLeftTextureCoordinate) * 0.2;
    color += texture2D(inputImageTexture, twoStepsRightTextureCoordinate) * 0.2;
    gl_FragColor = color;
}

