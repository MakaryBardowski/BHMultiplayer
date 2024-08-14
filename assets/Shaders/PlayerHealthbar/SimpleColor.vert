in vec3 inPosition;
in vec4 inTexCoord;

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldMatrix;

out vec4 v_texCoord;
out float timeSinceStartSeconds;
out vec4 vertexPosition;

void main(){
    vec4 position = vec4(inPosition,1.0);
    vertexPosition = g_WorldMatrix*position;
    v_texCoord = inTexCoord;
    gl_Position = g_WorldViewProjectionMatrix * position;
}