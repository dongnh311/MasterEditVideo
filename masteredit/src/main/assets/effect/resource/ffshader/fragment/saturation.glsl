precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
float saturation = 0.6;
const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);
void main()
{
    vec4 textureColor = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    float luminance = dot(textureColor.rgb, luminanceWeighting);
    vec3 greyScaleColor = vec3(luminance);
    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);
}
