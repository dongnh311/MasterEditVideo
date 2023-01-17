precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
void main() {
    vec4 color = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    gl_FragColor = vec4((1.0 - color.rgb), color.w);
}
