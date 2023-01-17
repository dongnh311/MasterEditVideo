precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
float gamma = 2.0;
void main() {
    vec4 textureColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    gl_FragColor = vec4(pow(textureColor.rgb, vec3(gamma)), textureColor.w);
}
