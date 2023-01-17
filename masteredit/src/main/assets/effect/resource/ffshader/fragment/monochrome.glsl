precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
highp float intensity = 0.5;
const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);
void main() {
    vec4 textureColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    float luminance = dot(textureColor.rgb, luminanceWeighting);
    vec4 desat = vec4(vec3(luminance), 1.0);
    vec4 outputColor = vec4(
    (desat.r < 0.5 ? (2.0 * desat.r * 0.6) : (1.0 - 2.0 * (1.0 - desat.r) * (1.0 - 0.6))),
    (desat.g < 0.5 ? (2.0 * desat.g * 0.45) : (1.0 - 2.0 * (1.0 - desat.g) * (1.0 - 0.45))),
    (desat.b < 0.5 ? (2.0 * desat.b * 0.3) : (1.0 - 2.0 * (1.0 - desat.b) * (1.0 - 0.3))),
    1.0
    );

    gl_FragColor = vec4(mix(textureColor.rgb, outputColor.rgb, intensity), textureColor.a);
  }
