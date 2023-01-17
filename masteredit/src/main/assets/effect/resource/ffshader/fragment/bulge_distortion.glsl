precision mediump float;

varying highp vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;

highp vec2 center = vec2(0.5, 0.5);
highp float radius = 0.25;
highp float scale = 0.5;

void main() {
    highp vec2 textureCoordinateToUse = textureCoordinate * 0.5 + 0.5;
    highp float dist = distance(center, textureCoordinate * 0.5 + 0.5);
    textureCoordinateToUse -= center;
    if (dist < radius) {
        highp float percent = 1.0 - ((radius - dist) / radius) * scale;
        percent = percent * percent;
        textureCoordinateToUse = textureCoordinateToUse * percent;
    }
    textureCoordinateToUse += center;

    gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse);
}
