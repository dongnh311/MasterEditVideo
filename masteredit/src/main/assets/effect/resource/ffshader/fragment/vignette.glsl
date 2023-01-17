precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
lowp vec2 vignetteCenter = vec2(0.5, 0.5);
highp float vignetteStart = 0.2;
highp float vignetteEnd = 0.85;
void main() {
    lowp vec3 rgb = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5).rgb;
    lowp float d = distance(textureCoordinate * 0.5 + 0.5, vec2(vignetteCenter.x, vignetteCenter.y));
    lowp float percent = smoothstep(vignetteStart, vignetteEnd, d);
    gl_FragColor = vec4(mix(rgb.x, 0.0, percent), mix(rgb.y, 0.0, percent), mix(rgb.z, 0.0, percent), 1.0);
}


