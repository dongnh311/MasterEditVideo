precision mediump float;
varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
highp vec2 blurCenter = vec2(0.5, 0.5);
highp float blurSize = 0.5;
void main()
{
    highp vec2 samplingOffset = 1.0/100.0 * (blurCenter - textureCoordinate * 0.5 + 0.5) * blurSize;
    lowp vec4 fragmentColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5) * 0.18;
    fragmentColor += texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5 + samplingOffset) * 0.15;
    fragmentColor += texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5 + (2.0 * samplingOffset)) *  0.12;
    fragmentColor += texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5 + (3.0 * samplingOffset)) * 0.09;
    fragmentColor += texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5 + (4.0 * samplingOffset)) * 0.05;
    fragmentColor += texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5 - samplingOffset) * 0.15;
    fragmentColor += texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5 - (2.0 * samplingOffset)) *  0.12;
    fragmentColor += texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5 - (3.0 * samplingOffset)) * 0.09;
    fragmentColor += texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5 - (4.0 * samplingOffset)) * 0.05;

    gl_FragColor = fragmentColor;
}
