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

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11C;
import union.xenfork.fe2d.Application;
import union.xenfork.fe2d.ApplicationConfig;
import union.xenfork.fe2d.Fe2D;
import union.xenfork.fe2d.graphics.Color;
import union.xenfork.fe2d.graphics.ShaderProgram;
import union.xenfork.fe2d.graphics.mesh.GeometryMesh;
import union.xenfork.fe2d.graphics.mesh.Mesh;
import union.xenfork.fe2d.graphics.texture.Texture;
import union.xenfork.fe2d.graphics.texture.TextureAtlas;
import union.xenfork.fe2d.graphics.texture.TextureParam;
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
    private Mesh backgroundMesh;
    private Mesh mesh;
    private Texture backgroundTexture;
    private Texture faceTexture;
    private TextureAtlas blockTextureAtlas;
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4fStack modelMatrix = new Matrix4fStack(2);

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        projectionMatrix.setOrtho2D(0, width, 0, height);
    }

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
            Fe2D.files.internal(ResourcePath.assets("breakout:shader/shader.vsh")).loadString(),
            Fe2D.files.internal(ResourcePath.assets("breakout:shader/shader.fsh")).loadString(),
            layout
        );
        shaderProgram.addSampler(0);

        backgroundMesh = GeometryMesh.quad(vertexBuilder -> vertexBuilder
                .floats(0.0f, 1.0f, 0.0f).ints(Color.WHITE_BITS).floats(0.0f, 0.0f)
                .floats(0.0f, 0.0f, 0.0f).ints(Color.WHITE_BITS).floats(0.0f, 1.0f)
                .floats(1.0f, 0.0f, 0.0f).ints(Color.WHITE_BITS).floats(1.0f, 1.0f)
                .floats(1.0f, 1.0f, 0.0f).ints(Color.WHITE_BITS).floats(1.0f, 0.0f),
            layout);

        mesh = Mesh.dynamic(layout, 4, 6);
        mesh.setVertices(vertexBuilder -> vertexBuilder
            .floats(0.0f, 1.0f, 0.0f).ints(Color.rgbaPackABGR(1f, 0f, 0f, 1f)).floats(0.0f, 0.0f)
            .floats(0.0f, 0.0f, 0.0f).ints(Color.rgbaPackABGR(0f, 1f, 0f, 1f)).floats(0.0f, 1.0f)
            .floats(1.0f, 0.0f, 0.0f).ints(Color.rgbaPackABGR(0f, 0f, 1f, 1f)).floats(1.0f, 1.0f)
            .floats(1.0f, 1.0f, 0.0f).ints(Color.rgbaPackABGR(1f, 1f, 1f, 1f)).floats(1.0f, 0.0f)
        );
        mesh.setIndices(0, 1, 2, 0, 2, 3);

        backgroundTexture = Texture.ofFile(Fe2D.files.internal(ResourcePath.assets("breakout:texture/background.png")),
            new TextureParam().minFilter(GL11C.GL_LINEAR));
        blockTextureAtlas = TextureAtlas.load(
            TextureAtlas.entry(Fe2D.files.internal(ResourcePath.assets("breakout:texture/block.png")), "block"),
            TextureAtlas.entry(Fe2D.files.internal(ResourcePath.assets("breakout:texture/block_solid.png")), "block_solid")
        );
        faceTexture = Texture.ofFile(Fe2D.files.internal(ResourcePath.assets("breakout:texture/face.png")));
    }

    @Override
    public void render() {
        super.render();
        shaderProgram.use();
        shaderProgram.setProjectionMatrix(projectionMatrix);
        shaderProgram.setModelMatrix(modelMatrix);
        shaderProgram.uploadUniforms();

        backgroundTexture.bind();
        modelMatrix.pushMatrix().scaling(Fe2D.graphics.width(), Fe2D.graphics.height(), 0f);
        shaderProgram.setModelMatrix(modelMatrix);
        modelMatrix.popMatrix();
        shaderProgram.uploadUniforms();
        backgroundMesh.render();

        blockTextureAtlas.bind();
        modelMatrix.pushMatrix().scaling(256,128, 0f);
        shaderProgram.setModelMatrix(modelMatrix);
        modelMatrix.popMatrix();
        shaderProgram.uploadUniforms();
        mesh.render();
        Texture.ZERO.bind();

        ShaderProgram.ZERO.use();
    }

    @Override
    public void dispose() {
        super.dispose();
        dispose(shaderProgram);
        dispose(backgroundMesh);
        dispose(mesh);
        dispose(backgroundTexture);
        dispose(blockTextureAtlas);
        dispose(faceTexture);
    }

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        config.useStderr = true;
        config.applicationName = "Breakout";
        config.windowWidth = 1280;
        config.windowHeight = 720;
        new Breakout().launch(config);
    }
}
