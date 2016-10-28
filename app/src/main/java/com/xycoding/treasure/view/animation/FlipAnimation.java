package com.xycoding.treasure.view.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 翻转动画
 *
 * Created by xuyang on 2016/9/26.
 */
public class FlipAnimation extends Animation {

    private Camera mCamera;
    private float mCenterX;
    private float mCenterY;
    private float fromDegree; //旋转起始角度
    private float toDegree; //旋转终止角度

    /**
     * Creates a 3D flip animation.
     *
     * @param fromDegree
     * @param toDegree
     */
    public FlipAnimation(float fromDegree, float toDegree) {
        this.fromDegree = fromDegree;
        this.toDegree = toDegree;
        setDuration(150);
        setFillAfter(false);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCenterX = width / 2;
        mCenterY = height / 2;
        mCamera = new Camera();
        mCamera.setLocation(0, 0, -mCenterX / 2);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float degree = fromDegree + (toDegree - fromDegree) * interpolatedTime;
        final Matrix matrix = t.getMatrix();
        mCamera.save();
        mCamera.rotateY(degree);
        mCamera.getMatrix(matrix);
        mCamera.restore();
        matrix.preTranslate(-mCenterX, -mCenterY);
        matrix.postTranslate(mCenterX, mCenterY);
    }

}
