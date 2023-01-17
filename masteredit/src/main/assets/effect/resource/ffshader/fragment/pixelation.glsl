precision mediump float;
varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
float imageWidthFactor = 1.0 / 720.0;
float imageHeightFactor = 1.0 / 720.0;
float pixel = 16.0;
void main()
{
    vec2 uv  = textureCoordinate.xy * 0.5 + 0.5;
    float dx = pixel * imageWidthFactor;
    float dy = pixel * imageHeightFactor;
    vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));
    vec3 tc = texture2D(inputImageTexture, coord).xyz;
    gl_FragColor = vec4(tc, 1.0);
}
