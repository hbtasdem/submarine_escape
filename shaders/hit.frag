#version 120

uniform vec3 hitColor;
uniform float intensity;

void main() {
    gl_FragColor = vec4(hitColor, intensity);
}
