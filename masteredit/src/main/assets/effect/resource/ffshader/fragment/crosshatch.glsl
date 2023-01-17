precision mediump float;
varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
highp float crossHatchSpacing = 0.03;
highp float lineWidth = 0.003;
const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);
void main()
{
    highp float luminance = dot(texture2D(inputImageTexture, textureCoordinate * 0.5 + 0.5).rgb, W);
    lowp vec4 colorToDisplay = vec4(1.0, 1.0, 1.0, 1.0);
    if (luminance < 1.00)
    {
        if (mod(textureCoordinate.x + textureCoordinate.y, crossHatchSpacing) <= lineWidth)
        {
            colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);
        }
    }
    if (luminance < 0.75)
    {
        if (mod(textureCoordinate.x - textureCoordinate.y, crossHatchSpacing) <= lineWidth)
        {
            colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);
        }
    }
    if (luminance < 0.50)
    {
        if (mod(textureCoordinate.x + textureCoordinate.y - (crossHatchSpacing / 2.0), crossHatchSpacing) <= lineWidth)
        {
            colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);
        }
    }
     if (luminance < 0.3)
     {
         if (mod(textureCoordinate.x - textureCoordinate.y - (crossHatchSpacing / 2.0), crossHatchSpacing) <= lineWidth)
         {
             colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);
         }
     }
     gl_FragColor = colorToDisplay;
  }          
