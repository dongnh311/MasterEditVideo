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
    int col = int(textureCoordinate.y * 3.0);
    vec2 textureCoordinateToUse = textureCoordinate;
    textureCoordinateToUse.y = (textureCoordinate.y - float(col) / 3.0) * 3.0;
    textureCoordinateToUse.y = textureCoordinateToUse.y/1280.0*320.0+1.0/3.0;

    vec2 uv = textureCoordinate.xy;
    if (uv.y < 1.0/3.0) {
        uv.y = uv.y + 1.0/3.0;
    } else if (uv.y > 2.0/3.0){
        uv.y = uv.y - 1.0/3.0;
    }

    gl_FragColor = texture2D(inputImageTexture, uv);

    //gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse);
}
