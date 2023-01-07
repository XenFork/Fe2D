#version 150 core

in vec3 fe_Position;
in vec4 fe_Color;
in vec2 fe_TexCoord0;

out vec4 vertexColor;
out vec2 UV0;

uniform mat4 ProjViewMat, ModelMat;

void main() {
    gl_Position = ProjViewMat * ModelMat * vec4(fe_Position, 1.0);
    vertexColor = fe_Color;
    UV0 = fe_TexCoord0;
}
