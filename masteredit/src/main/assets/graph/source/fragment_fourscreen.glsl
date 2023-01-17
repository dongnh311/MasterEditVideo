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

    int row = int(textureCoordinate.x * 2.0);
    int col = int(textureCoordinate.y * 2.0);

    vec2 textureCoordinateToUse = textureCoordinate;

    textureCoordinateToUse.x = (textureCoordinate.x - float(row) / 2.0) * 2.0;
    textureCoordinateToUse.y = (textureCoordinate.y - float(col) / 2.0) * 2.0;

    //gl_FragColor=texture2D(inputImageTexture, textureCoordinateToUse);

    vec2 uv = textureCoordinate.xy;

    if (uv.x <= 0.5) {
        uv.x = uv.x * 2.0;
    }else{
        uv.x = (uv.x - 0.5) * 2.0;
    }

    if (uv.y <= 0.5) {
        uv.y = uv.y * 2.0;
    }else{
        uv.y = (uv.y - 0.5) * 2.0;
    }

    gl_FragColor = texture2D(inputImageTexture, uv);
}
