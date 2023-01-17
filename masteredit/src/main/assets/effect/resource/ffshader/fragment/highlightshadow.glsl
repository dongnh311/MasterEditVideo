precision mediump float;
varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
lowp float shadows = 1.0;
lowp float highlights = 0.0;
const mediump vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);
void main()
{
    lowp vec4 source = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    mediump float luminance = dot(source.rgb, luminanceWeighting);
    mediump float shadow = clamp((pow(luminance, 1.0/(shadows+1.0)) + (-0.76)*pow(luminance, 2.0/(shadows+1.0))) - luminance, 0.0, 1.0);
    mediump float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-highlights)) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-highlights)))) - luminance, -1.0, 0.0);
    lowp vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));
    gl_FragColor = vec4(result.rgb, source.a);
}
