precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
float contrast = 2.5;
void main() {
    vec4 textureColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);
}

