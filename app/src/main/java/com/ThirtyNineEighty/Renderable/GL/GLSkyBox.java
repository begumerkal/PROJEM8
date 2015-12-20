package com.ThirtyNineEighty.Renderable.GL;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ThirtyNineEighty.Game.Objects.Descriptions.RenderableDescription;
import com.ThirtyNineEighty.Providers.IDataProvider;
import com.ThirtyNineEighty.Renderable.RendererContext;
import com.ThirtyNineEighty.Renderable.Shaders.*;
import com.ThirtyNineEighty.Resources.Entities.*;
import com.ThirtyNineEighty.Resources.Sources.*;
import com.ThirtyNineEighty.System.GameContext;

import java.io.IOException;
import java.io.ObjectInputStream;

public class GLSkyBox
  extends GLRenderable<GLRenderable.Data>
{
  private static final long serialVersionUID = 1L;

  private transient Texture textureData;
  private transient Geometry geometryData;

  public GLSkyBox(RenderableDescription description, IDataProvider<Data> provider)
  {
    super(description, provider);

    geometryData = GameContext.resources.getGeometry(new FileGeometrySource(description.modelName));
    textureData = GameContext.resources.getTexture(new FileTextureSource(description.textureName, true));
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();

    geometryData = GameContext.resources.getGeometry(new FileGeometrySource(description.modelName));
    textureData = GameContext.resources.getTexture(new FileTextureSource(description.textureName, true));
  }

  @Override
  public int getShaderId()
  {
    return Shader.ShaderSkyBox;
  }

  @Override
  protected void draw(RendererContext context, Data data)
  {
    ShaderSkyBox shader = (ShaderSkyBox) Shader.getCurrent();

    // build PVM matrix
    Matrix.multiplyMM(modelProjectionViewMatrix, 0, context.getProjectionViewMatrix(), 0, modelMatrix, 0);

    // bind texture to 0 slot
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureData.getHandle());

    // send uniform data to shader
    GLES20.glUniform1i(shader.uniformTextureHandle, 0);
    GLES20.glUniformMatrix4fv(shader.uniformMatrixProjectionHandle, 1, false, modelProjectionViewMatrix, 0);

    // bind data buffer
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, geometryData.getHandle());

    // set offsets to arrays for buffer
    GLES20.glVertexAttribPointer(shader.attributePositionHandle, 3, GLES20.GL_FLOAT, false, 32, 0);
    GLES20.glVertexAttribPointer(shader.attributeTexCoordHandle, 2, GLES20.GL_FLOAT, false, 32, 24);

    // enable attribute arrays
    GLES20.glEnableVertexAttribArray(shader.attributePositionHandle);
    GLES20.glEnableVertexAttribArray(shader.attributeTexCoordHandle);

    // validating if debug
    shader.validate();
    geometryData.validate();
    textureData.validate();

    // draw
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, geometryData.getTrianglesCount() * 3);

    // disable attribute arrays
    GLES20.glDisableVertexAttribArray(shader.attributePositionHandle);
    GLES20.glDisableVertexAttribArray(shader.attributeTexCoordHandle);
  }
}