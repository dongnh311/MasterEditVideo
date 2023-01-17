precision mediump float;
varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
highp float exposure = 1.0;
void main()
{
    highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    gl_FragColor = vec4(textureColor.rgb * pow(2.0, exposure), textureColor.w);
}
