precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);
void main() {
    vec4 textureColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    float luminance = dot(textureColor.rgb, W);
    gl_FragColor = vec4(vec3(luminance), textureColor.a);
}
