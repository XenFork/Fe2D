/*
 * Fork Engine 2D
 * Copyright (C) 2023 XenFork Union
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package union.xenfork.fe2d.test;

import org.lwjgl.opengl.GL11C;
import union.xenfork.fe2d.Application;
import union.xenfork.fe2d.ApplicationConfig;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.mesh.Mesh;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.vertex.VertexAttribute;
import union.xenfork.fe2d.graphics.vertex.VertexLayout;
import union.xenfork.fe2d.util.ResourcePath;

/**
 * breakout game
 *
 * @author squid233
 * @since 0.1.0
 */
public final class Breakout extends Application {
    private ShaderProgram shaderProgram;
    private Mesh mesh;
    private Texture texture;

    @Override
    public void init() {
        super.init();
        GL11C.glClearColor(0f, 0f, 0f, 1f);

        VertexLayout layout = new VertexLayout(
            VertexAttribute.position().getImplicit(),
            VertexAttribute.colorPacked().getImplicit(),
            VertexAttribute.texCoord(0).getImplicit()
        );
        shaderProgram = new ShaderProgram(
            Fe2D.files.internal(new ResourcePath("breakout:shader/shader.vsh"), ResourcePath.ASSETS).loadString(),
            Fe2D.files.internal(new ResourcePath("breakout:shader/shader.fsh"), ResourcePath.ASSETS).loadString(),
            layout
        );
        shaderProgram.setUniform("Sampler0", 0);

        mesh = Mesh.dynamic(layout, 4, 6);
        mesh.setVertices(vertexBuilder -> vertexBuilder
            .floats(-0.0f, 0.5f, 0.0f).ints(Color.rgbaPackABGR(1f, 0f, 0f, 1f))
            .floats(-0.5f, -0.5f, 0.0f).ints(Color.rgbaPackABGR(0f, 1f, 0f, 1f))
            .floats(0.5f, -0.5f, 0.0f).ints(Color.rgbaPackABGR(0f, 0f, 1f, 1f))
            .floats(0.5f, 0.5f, 0.0f).ints(Color.rgbaPackABGR(1f, 1f, 1f, 1f))
        );
        mesh.setIndices(0, 1, 2, 0, 2, 3);

        texture = Texture.ofImage(Fe2D.files.internal(new ResourcePath("breakout:texture/brick.png"), ResourcePath.ASSETS));
    }

    @Override
    public void render() {
        super.render();
        shaderProgram.use();
        shaderProgram.uploadUniforms();
        texture.bind();
        mesh.render();
        Texture.ZERO.bind();
        ShaderProgram.ZERO.use();
    }

    @Override
    public void dispose() {
        super.dispose();
        dispose(shaderProgram);
        dispose(mesh);
        dispose(texture);
    }

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        config.useStderr = true;
        config.applicationName = "Breakout";
        new Breakout().launch(config);
    }
}
