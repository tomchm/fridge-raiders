package edu.cornell.gdiac.game.shaders;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class CustomShader {
    static final public ShaderProgram createCustomShader() {
        String gamma = "";
        if (RayHandler.getGammaCorrection())
            gamma = "sqrt";

        final String vertexShader =
                "attribute vec4 vertex_positions;\n" //
                        + "attribute vec4 quad_colors;\n" //
                        + "attribute float s;\n"
                        + "uniform mat4 u_projTrans;\n" //
                        + "varying vec4 v_color;\n" //
                        + "void main()\n" //
                        + "{\n" //
                        + "   v_color = (s + 0.8) * quad_colors * 0.5;\n" //
                        + "   gl_Position =  u_projTrans * vertex_positions;\n" //
                        + "}\n";
        final String fragmentShader = "#ifdef GL_ES\n" //
                + "precision lowp float;\n" //
                + "#define MED mediump\n"
                + "#else\n"
                + "#define MED \n"
                + "#endif\n" //
                + "varying vec4 v_color;\n" //
                + "void main()\n"//
                + "{\n" //
                + "  gl_FragColor = "+gamma+"(v_color);\n" //
                + "}";

        ShaderProgram.pedantic = false;
        ShaderProgram customShader = new ShaderProgram(vertexShader,
                fragmentShader);
        if (customShader.isCompiled() == false) {
            Gdx.app.log("ERROR", customShader.getLog());
        }

        return customShader;
    }
}

