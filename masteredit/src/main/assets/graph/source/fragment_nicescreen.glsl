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

    int row = int(textureCoordinate.x * 3.0);
    int col = int(textureCoordinate.y * 3.0);

    vec2 textureCoordinateToUse = textureCoordinate;

    textureCoordinateToUse.x = (textureCoordinate.x - float(row) / 3.0) * 3.0;
    textureCoordinateToUse.y = (textureCoordinate.y - float(col) / 3.0) * 3.0;

    //gl_FragColor=texture2D(inputImageTexture, textureCoordinateToUse);

    vec2 uv = textureCoordinate.xy;

    if (uv.x <= 1.0/3.0) {
        uv.x = uv.x * 3.0;
    }else if (uv.x >= 2.0/3.0){
        uv.x = (uv.x - 2.0/3.0) * 3.0;
    }else{
        uv.x = (uv.x - 1.0/3.0)*3.0;
    }

    if (uv.y <= 1.0/3.0) {
        uv.y = uv.y * 3.0;
    }else if (uv.y >= 2.0/3.0){
        uv.y = (uv.y - 2.0/3.0) * 3.0;
    }else{
        uv.y = (uv.y - 1.0/3.0)*3.0;
    }

    gl_FragColor = texture2D(inputImageTexture, uv);
}
