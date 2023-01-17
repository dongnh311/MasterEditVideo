precision mediump float;
varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
highp float colorLevels = 10.0;
void main()
{
    highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    gl_FragColor = floor((textureColor * colorLevels) + vec4(0.5)) / colorLevels;
}
