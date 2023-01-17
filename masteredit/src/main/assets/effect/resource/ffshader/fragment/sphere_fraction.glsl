precision mediump float;

varying vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;
highp vec2 center = vec2(0.5, 0.5);
highp float radius = 0.5;
highp float aspectRatio = 1.0;
highp float refractiveIndex = 0.71;

void main() {
    highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x * 0.5 + 0.5, (textureCoordinate.y * 0.5 + 0.5 * aspectRatio + 0.5 - 0.5 * aspectRatio));
    highp float distanceFromCenter = distance(center, textureCoordinateToUse);
    lowp float checkForPresenceWithinSphere = step(distanceFromCenter, radius);

    distanceFromCenter = distanceFromCenter / radius;

    highp float normalizedDepth = radius * sqrt(1.0 - distanceFromCenter * distanceFromCenter);
    highp vec3 sphereNormal = normalize(vec3(textureCoordinateToUse - center, normalizedDepth));

    highp vec3 refractedVector = refract(vec3(0.0, 0.0, -1.0), sphereNormal, refractiveIndex);

    gl_FragColor = texture2D(inputImageTexture, (refractedVector.xy + 1.0) * 0.5) * checkForPresenceWithinSphere;
}

