#ifdef GL_ES
precision highp float;
#else
#define highp
#define mediump
#define lowp
#endif
uniform sampler2D inputImageTexture;
varying highp vec2 textureCoordinate;
void main() {
    int col = int(textureCoordinate.y * 2.0);
    vec2 textureCoordinateToUse = textureCoordinate;
    textureCoordinateToUse.y = (textureCoordinate.y - float(col) / 2.0) * 2.0;
    textureCoordinateToUse.y = textureCoordinateToUse.y/1280.0*720.0+1.0/4.0;

    vec4 mars = texture2D(inputImageTexture, textureCoordinateToUse);

    vec2 uv = textureCoordinate.xy;
    float y;
    if (uv.y >= 0.0 && uv.y <= 0.5) {
        y = uv.y + 0.25;
    }else{
        y = uv.y - 0.25;
    }

    gl_FragColor = texture2D(inputImageTexture, vec2(uv.x, y));

    //gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse);
}
