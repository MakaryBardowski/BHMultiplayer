uniform vec4 m_tint;
uniform sampler2D m_emptyHealthBar;
uniform sampler2D m_fullHealthBarWithoutFrame;
uniform vec2 m_healthPercent;

in vec4 v_texCoord;
in float timeSinceStartSeconds;
in vec4 vertexPosition;
out vec4 v_color;
void main(){
        vec4 backgroundColor = texture(m_emptyHealthBar,v_texCoord.xy);
        vec4 hpBarColor = texture(m_fullHealthBarWithoutFrame,v_texCoord.xy);

        if(backgroundColor.a == 0. && hpBarColor.a == 0.){
            discard;
        }
        
        v_color = backgroundColor;


        
        if(hpBarColor.a != 0.){
            if(v_texCoord.x <= m_healthPercent.x){
             if(v_texCoord.x <= m_healthPercent.x + m_healthPercent.y){
                v_color = hpBarColor;
               } else if(v_texCoord.x <=  m_healthPercent.x - m_healthPercent.y){
                v_color = vec4(0.196078431,0.803921569,0.,0.);
               }
            } else if (v_texCoord.x > m_healthPercent.x && v_texCoord.x <= m_healthPercent.x + m_healthPercent.y){
                v_color = vec4(1.,1.,0.,0.);
            }

        }
}
