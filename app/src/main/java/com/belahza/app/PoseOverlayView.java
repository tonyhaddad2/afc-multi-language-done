package com.belahza.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class PoseOverlayView extends View {
    private Pose pose;
    private int imageWidth = 480, imageHeight = 640;
    private boolean flipX = true;
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    public PoseOverlayView(Context context) { super(context); }
    public void setImageSourceInfo(int width, int height, boolean flipX) { this.imageWidth=Math.max(1,width); this.imageHeight=Math.max(1,height); this.flipX=flipX; }
    public void setPose(Pose pose) { this.pose=pose; invalidate(); }
    @Override protected void onDraw(Canvas canvas) { super.onDraw(canvas); if (pose==null) return; p.setStrokeCap(Paint.Cap.ROUND); p.setStrokeWidth(6f); p.setStyle(Paint.Style.STROKE); link(canvas, PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER); link(canvas, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW); link(canvas, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST); link(canvas, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW); link(canvas, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST); link(canvas, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP); link(canvas, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP); link(canvas, PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP); link(canvas, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE); link(canvas, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE); link(canvas, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE); link(canvas, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE); p.setStyle(Paint.Style.FILL); for (PoseLandmark lm : pose.getAllPoseLandmarks()) { if (lm.getInFrameLikelihood()<0.45f) continue; PointF pt = map(lm.getPosition()); p.setColor(Color.argb(235,231,25,36)); canvas.drawCircle(pt.x, pt.y, 9f, p); p.setColor(Color.WHITE); canvas.drawCircle(pt.x, pt.y, 4f, p); } }
    private void link(Canvas c, int aType, int bType) { PoseLandmark a=pose.getPoseLandmark(aType), b=pose.getPoseLandmark(bType); if(a==null||b==null||a.getInFrameLikelihood()<0.35f||b.getInFrameLikelihood()<0.35f)return; PointF pa=map(a.getPosition()), pb=map(b.getPosition()); p.setColor(Color.argb(210,255,255,255)); p.setStrokeWidth(6f); c.drawLine(pa.x,pa.y,pb.x,pb.y,p); p.setColor(Color.argb(180,231,25,36)); p.setStrokeWidth(3f); c.drawLine(pa.x,pa.y,pb.x,pb.y,p); }
    private PointF map(PointF src) { float x=src.x/imageWidth*getWidth(); float y=src.y/imageHeight*getHeight(); if(flipX)x=getWidth()-x; return new PointF(x,y); }
}
