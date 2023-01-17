precision mediump float;
varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
lowp float vibrance = 3.0;
void main()
{
    lowp vec4 color = texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5);
    lowp float average = (color.r + color.g + color.b) / 3.0;
    lowp float mx = max(color.r, max(color.g, color.b));
    lowp float amt = (mx - average) * (-vibrance * 3.0);
    color.rgb = mix(color.rgb, vec3(mx), amt);
    gl_FragColor = color;
}
