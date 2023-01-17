precision mediump float;
varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
highp float threshold = 0.5;
const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);
void main()
{
    highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    highp float luminance = dot(textureColor.rgb, W);
    highp float thresholdResult = step(luminance, threshold);
    highp vec3 finalColor = abs(thresholdResult - textureColor.rgb);
    gl_FragColor = vec4(finalColor, textureColor.w);
}
