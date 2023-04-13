/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.List;

/**
 * A view which renders a series of custom graphics to be overlayed on top of an associated preview
 * (i.e., the camera preview). The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.
 *
 * <p>Supports scaling and mirroring of the graphics relative the camera's preview properties. The
 * idea is that detection items are expressed in terms of an image size, but need to be scaled up to
 * the full view size, and also mirrored in the case of the front-facing camera.
 *
 * <p>Associated {@link Graphic} items should use the following methods to convert to view
 * coordinates for the graphics that are drawn:
 *
 * <ol>
 *   <li>{@link Graphic#scale(float)} adjusts the size of the supplied value from the image scale to
 *       the view scale.
 *   <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
 *       coordinate from the image's coordinate system to the view coordinate system.
 * </ol>
 */
public class GraphicOverlay extends View {
  private final Object lock = new Object();
  private final List<Graphic> graphics = new ArrayList<>();
  // Matrix for transforming from image coordinates to overlay view coordinates.
  private final Matrix transformationMatrix = new Matrix();

  private int imageWidth;
  private int imageHeight;
  // The factor of overlay View size to image size. Anything in the image coordinates need to be
  // scaled by this amount to fit with the area of overlay View.
  private float scaleFactor = 1.0f;
  // The number of horizontal pixels needed to be cropped on each side to fit the image with the
  // area of overlay View after scaling.
  private float postScaleWidthOffset;
  // The number of vertical pixels needed to be cropped on each side to fit the image with the
  // area of overlay View after scaling.
  private float postScaleHeightOffset;
  private boolean isImageFlipped;
  private boolean needUpdateTransformation = true;

  /**
   * Base class for a custom graphics object to be rendered within the graphic overlay. Subclass
   * this and implement the {@link Graphic#draw(Canvas)} method to define the graphics element. Add
   * instances to the overlay using {@link GraphicOverlay#add(Graphic)}.
   */
  public abstract static class Graphic {
    private GraphicOverlay overlay;

    public Graphic(GraphicOverlay overlay) {
      this.overlay = overlay;
    }

    /**
     * Draw the graphic on the supplied canvas. Drawing should use the following methods to convert
     * to view coordinates for the graphics that are drawn:
     *
     * <ol>
     *   <li>{@link Graphic#scale(float)} adjusts the size of the supplied value from the image
     *       scale to the view scale.
     *   <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
     *       coordinate from the image's coordinate system to the view coordinate system.
     * </ol>
     *
     * @param canvas drawing canvas
     */
    public abstract void draw(Canvas canvas);

    protected void drawRect(
        Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
      canvas.drawRect(left, top, right, bottom, paint);
    }

    protected void drawText(Canvas canvas, String text, float x, float y, Paint paint) {
      canvas.drawText(text, x, y, paint);
    }

    /** Adjusts the supplied value from the image scale to the view scale. */
    public float scale(float imagePixel) {
      return imagePixel * overlay.scaleFactor;
    }

    /** Returns the application context of the app. */
    public Context getApplicationContext() {
      return overlay.getContext().getApplicationContext();
    }

    public boolean isImageFlipped() {
      return overlay.isImageFlipped;
    }

    /**
     * Adjusts the x coordinate from the image's coordinate system to the view coordinate system.
     */
    public float translateX(float x) {
      if (overlay.isImageFlipped) {
        return overlay.getWidth() - (scale(x) - overlay.postScaleWidthOffset);
      } else {
        return scale(x) - overlay.postScaleWidthOffset;
      }
    }

    /**
     * Adjusts the y coordinate from the image's coordinate system to the view coordinate system.
     */
    public float translateY(float y) {
      return scale(y) - overlay.postScaleHeightOffset;
    }

    /**
     * Returns a {@link Matrix} for transforming from image coordinates to overlay view coordinates.
     */
    public Matrix getTransformationMatrix() {
      return overlay.transformationMatrix;
    }

    public void postInvalidate() {
      overlay.postInvalidate();
    }

    /**
     * Given the {@code zInImagePixel}, update the color for the passed in {@code paint}. The color will be
     * more red if the {@code zInImagePixel} is smaller, or more blue ish vice versa. This is
     * useful to visualize the z value of landmarks via color for features like Pose and Face Mesh.
     *
     * @param paint the paint to update color with
     * @param canvas the canvas used to draw with paint
     * @param visualizeZ if true, paint color will be changed.
     * @param rescaleZForVisualization if true, re-scale the z value with zMin and zMax to make
     *     color more distinguishable
     * @param zInImagePixel the z value used to update the paint color
     * @param zMin min value of all z values going to be passed in
     * @param zMax max value of all z values going to be passed in
     */
    public void updatePaintColorByZValue(
        Paint paint,
        Canvas canvas,
        boolean visualizeZ,
        boolean rescaleZForVisualization,
        float zInImagePixel,
        float zMin,
        float zMax) {
      if (!visualizeZ) {
        return;
      }

      // When visualizeZ is true, sets up the paint to different colors based on z values.
      // Gets the range of z value.
      float zLowerBoundInScreenPixel;
      float zUpperBoundInScreenPixel;

      if (rescaleZForVisualization) {
        zLowerBoundInScreenPixel = min(-0.001f, scale(zMin));
        zUpperBoundInScreenPixel = max(0.001f, scale(zMax));
      } else {
        // By default, assume the range of z value in screen pixel is [-canvasWidth, canvasWidth].
        float defaultRangeFactor = 1f;
        zLowerBoundInScreenPixel = -defaultRangeFactor * canvas.getWidth();
        zUpperBoundInScreenPixel = defaultRangeFactor * canvas.getWidth();
      }

      float zInScreenPixel = scale(zInImagePixel);

      if (zInScreenPixel < 0) {
        // Sets up the paint to be red if the item is in front of the z origin.
        // Maps values within [zLowerBoundInScreenPixel, 0) to [255, 0) and use it to control the
        // color. The larger the value is, the more red it will be.
        int v = (int) (zInScreenPixel / zLowerBoundInScreenPixel * 255);
        v = Ints.constrainToRange(v, 0, 255);
        paint.setARGB(255, 255, 255 - v, 255 - v);
      } else {
        // Sets up the paint to be blue if the item is behind the z origin.
        // Maps values within [0, zUpperBoundInScreenPixel] to [0, 255] and use it to control the
        // color. The larger the value is, the more blue it will be.
        int v = (int) (zInScreenPixel / zUpperBoundInScreenPixel * 255);
        v = Ints.constrainToRange(v, 0, 255);
        paint.setARGB(255, 255 - v, 255 - v, 255);
      }
    }
  }

  public GraphicOverlay(Context context, AttributeSet attrs) {
    super(context, attrs);
    addOnLayoutChangeListener(
        (view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
            needUpdateTransformation = true);
  }

  /** Removes all graphics from the overlay. */
  public void clear() {
    synchronized (lock) {
      graphics.clear();
    }
    postInvalidate();
  }

  /** Adds a graphic to the overlay. */
  public void add(Graphic graphic) {
    synchronized (lock) {
      graphics.add(graphic);
    }
  }

  /** Removes a graphic from the overlay. */
  public void remove(Graphic graphic) {
    synchronized (lock) {
      graphics.remove(graphic);
    }
    postInvalidate();
  }

  /**
   * Sets the source information of the image being processed by detectors, including size and
   * whether it is flipped, which informs how to transform image coordinates later.
   *
   * @param imageWidth the width of the image sent to ML Kit detectors
   * @param imageHeight the height of the image sent to ML Kit detectors
   * @param isFlipped whether the image is flipped. Should set it to true when the image is from the
   *     front camera.
   */
  public void setImageSourceInfo(int imageWidth, int imageHeight, boolean isFlipped) {
    Preconditions.checkState(imageWidth > 0, "image width must be positive");
    Preconditions.checkState(imageHeight > 0, "image height must be positive");
    synchronized (lock) {
      this.imageWidth = imageWidth;
      this.imageHeight = imageHeight;
      this.isImageFlipped = isFlipped;
      needUpdateTransformation = true;
    }
    postInvalidate();
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  private void updateTransformationIfNeeded() {
    if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
      return;
    }
    float viewAspectRatio = (float) getWidth() / getHeight();
    float imageAspectRatio = (float) imageWidth / imageHeight;
    postScaleWidthOffset = 0;
    postScaleHeightOffset = 0;
    if (viewAspectRatio > imageAspectRatio) {
      // The image needs to be vertically cropped to be displayed in this view.
      scaleFactor = (float) getWidth() / imageWidth;
      postScaleHeightOffset = ((float) getWidth() / imageAspectRatio - getHeight()) / 2;
    } else {
      // The image needs to be horizontally cropped to be displayed in this view.
      scaleFactor = (float) getHeight() / imageHeight;
      postScaleWidthOffset = ((float) getHeight() * imageAspectRatio - getWidth()) / 2;
    }

    transformationMatrix.reset();
    transformationMatrix.setScale(scaleFactor, scaleFactor);
    transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset);

    if (isImageFlipped) {
      transformationMatrix.postScale(-1f, 1f, getWidth() / 2f, getHeight() / 2f);
    }

    needUpdateTransformation = false;
  }

  /** Draws the overlay with its associated graphic objects. */
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    synchronized (lock) {
      updateTransformationIfNeeded();

      for (Graphic graphic : graphics) {
        graphic.draw(canvas);
      }
    }
  }
}
