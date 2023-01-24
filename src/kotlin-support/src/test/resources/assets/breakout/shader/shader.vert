#version 150 core

in vec2 fe_Position;
in vec4 fe_Color;
in vec2 fe_TexCoord0;

out vec4 vertexColor;
out vec2 UV0;

uniform mat4 fe_ProjMatrix, fe_ModelMatrix;

void main() {
    gl_Position = fe_ProjMatrix * fe_ModelMatrix * vec4(fe_Position, 0.0, 1.0);
    vertexColor = fe_Color;
    UV0 = fe_TexCoord0;
}
