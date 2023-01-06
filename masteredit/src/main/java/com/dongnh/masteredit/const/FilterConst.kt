package com.dongnh.masteredit.const

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
const val GAMMA_FRAGMENT_SHADER = """precision mediump float; varying vec2 textureCoordinate;
 
             uniform lowp sampler2D inputImageTexture;
             uniform lowp float gamma;
             
             void main()
             {
                 lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
                 
                 gl_FragColor = vec4(pow(textureColor.rgb, vec3(gamma)), textureColor.w);
             }"""

const val BRIGHTNESS_FRAGMENT_SHADER = """precision mediump float; varying vec2 textureCoordinate;
                         uniform lowp sampler2D inputImageTexture;
                         uniform lowp float brightness;
                         
                         void main()
                         {
                             lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
                             
                             gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);
                         }"""


const val CONTRAST_FRAGMENT_SHADER = """precision mediump float; varying vec2 textureCoordinate;
                                 
                                 uniform lowp sampler2D inputImageTexture;
                                 uniform lowp float contrast;
                                 
                                 void main()
                                 {
                                     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
                                     
                                     gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);
                                 }"""