precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
highp float red = 0.87;
highp float green = 0.72;
highp float blue = 0.53;
void main() {
    vec4 textureColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    gl_FragColor = vec4(textureColor.r * red, textureColor.g * green, textureColor.b * blue, 1.0);
}
