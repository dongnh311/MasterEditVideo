precision mediump float;
varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
lowp float distance = 0.2;
highp float slope = 0.5;
void main()
{
    highp vec4 color = vec4(1.0);
    highp float  d = textureCoordinate.y * slope +  distance;
    highp vec4 c = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    c = (c - d * color) / (1.0 - d);
    gl_FragColor = c;
}
