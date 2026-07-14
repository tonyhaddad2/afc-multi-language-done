package com.belahza.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.util.Size;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiCoachActivity extends ComponentActivity {
    private static final int CAMERA_REQUEST = 77;

    private PreviewView preview;
    private PoseOverlayView overlay;
    private TextView tracking,checks,feedback,limits;
    private ExecutorService cameraExecutor;
    private PoseDetector detector;
    private ProcessCameraProvider provider;
    private int lensFacing = CameraSelector.LENS_FACING_FRONT;

    private String modeKey = "cardiac";
    private boolean supported = true;
    private boolean locked = false;
    private int stableFrames = 0;
    private int lostFrames = 0;
    private float lockX,lockY,lockWidth;

    private final List<Long> peaks = new ArrayList<>();
    private float previousY = Float.NaN;
    private boolean movingDown = false;
    private long lastPeak = 0;
    private float cadence = -1;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        String requested = getIntent().getStringExtra(EmergencyData.EXTRA_KEY);
        if (requested != null && !requested.isEmpty()) modeKey = requested;
        supported = "cardiac".equals(modeKey) || "drowning".equals(modeKey);

        cameraExecutor = Executors.newSingleThreadExecutor();
        detector = PoseDetection.getClient(new AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                .build());

        LinearLayout root = Ui.vertical(this);
        root.setLayoutDirection(LanguageManager.layoutDirection(this));
        root.setPadding(Ui.dp(this,14),Ui.dp(this,14),Ui.dp(this,14),Ui.dp(this,18));
        root.setBackgroundColor(Ui.SOFT);

        LinearLayout top = Ui.horizontal(this);
        Button back = Ui.button(this,LanguageManager.t(this,"back"),android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        top.addView(back,Ui.mlp(this,Ui.dp(this,104),Ui.dp(this,48),0,0,10,0));
        TextView title = Ui.text(this,LanguageManager.t(this,"coach_title"),24,Ui.NAVY,Typeface.BOLD);
        top.addView(title,Ui.lp(0,-2));
        ((LinearLayout.LayoutParams)title.getLayoutParams()).weight=1;
        root.addView(top);

        limits = Ui.text(this,supported ? LanguageManager.t(this,"ai_limits") : LanguageManager.t(this,"cannot_assess"),13,Ui.MUTED,Typeface.NORMAL);
        root.addView(limits,Ui.mlp(this,-1,-2,0,10,0,10));

        FrameLayout camera = new FrameLayout(this);
        camera.setBackground(Ui.bgDp(this,Ui.NAVY,26));
        preview = new PreviewView(this);
        preview.setScaleType(PreviewView.ScaleType.FILL_CENTER);
        overlay = new PoseOverlayView(this);
        camera.addView(preview,new FrameLayout.LayoutParams(-1,-1));
        camera.addView(overlay,new FrameLayout.LayoutParams(-1,-1));
        root.addView(camera,Ui.mlp(this,-1,0,0,0,0,12));
        ((LinearLayout.LayoutParams)camera.getLayoutParams()).weight=1;

        LinearLayout panel = Ui.card(this);
        tracking = Ui.text(this,LanguageManager.t(this,"tracking_waiting"),14,Ui.RED,Typeface.BOLD);
        checks = Ui.text(this,supported ? "Checks: --" : LanguageManager.t(this,"guided_only"),25,Ui.NAVY,Typeface.BOLD);
        feedback = Ui.text(this,supported ? LanguageManager.t(this,"start_camera") : LanguageManager.t(this,"cannot_assess"),15,Ui.INK,Typeface.BOLD);
        panel.addView(tracking);
        panel.addView(checks,Ui.mlp(this,-1,-2,0,7,0,5));
        panel.addView(feedback);

        LinearLayout controls = Ui.horizontal(this);
        Button start = Ui.button(this,LanguageManager.t(this,"start_camera"),Ui.RED,android.graphics.Color.WHITE);
        Button flip = Ui.button(this,LanguageManager.t(this,"flip_camera"),android.graphics.Color.WHITE,Ui.NAVY);
        Button reset = Ui.button(this,LanguageManager.t(this,"reset"),android.graphics.Color.WHITE,Ui.NAVY);
        controls.addView(start,weight(0,Ui.dp(this,52),0,12,5,0));
        controls.addView(flip,weight(0,Ui.dp(this,52),5,12,5,0));
        controls.addView(reset,weight(0,Ui.dp(this,52),5,12,0,0));
        panel.addView(controls);
        root.addView(panel);

        start.setOnClickListener(v -> ensureCamera());
        flip.setOnClickListener(v -> {
            lensFacing = lensFacing == CameraSelector.LENS_FACING_FRONT ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT;
            resetTracking();
            if (provider != null) startCamera();
        });
        reset.setOnClickListener(v -> resetTracking());

        setContentView(root);
    }

    private LinearLayout.LayoutParams weight(int w,int h,int l,int t,int r,int b) {
        LinearLayout.LayoutParams lp = Ui.mlp(this,w,h,l,t,r,b);
        lp.weight=1;
        return lp;
    }

    private void ensureCamera() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) startCamera();
        else requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST);
    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] results) {
        super.onRequestPermissionsResult(requestCode,permissions,results);
        if (requestCode == CAMERA_REQUEST && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) startCamera();
        else if (requestCode == CAMERA_REQUEST) Toast.makeText(this,LanguageManager.t(this,"camera_permission"),Toast.LENGTH_LONG).show();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                provider = future.get();
                provider.unbindAll();

                Preview cameraPreview = new Preview.Builder().build();
                cameraPreview.setSurfaceProvider(preview.getSurfaceProvider());

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(480,640))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                analysis.setAnalyzer(cameraExecutor,this::analyze);

                CameraSelector selector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
                provider.bindToLifecycle(this,selector,cameraPreview,analysis);
                overlay.setImageSourceInfo(480,640,lensFacing == CameraSelector.LENS_FACING_FRONT);
            } catch(Exception e) {
                feedback.setText("Camera start failed.");
            }
        },ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyze(ImageProxy proxy) {
        Image media = proxy.getImage();
        if (media == null) {
            proxy.close();
            return;
        }
        final double luma = averageLuma(proxy);
        InputImage input = InputImage.fromMediaImage(media,proxy.getImageInfo().getRotationDegrees());
        detector.process(input)
                .addOnSuccessListener(pose -> runOnUiThread(() -> {
                    overlay.setPose(pose);
                    evaluate(pose,luma);
                }))
                .addOnFailureListener(error -> runOnUiThread(() -> feedback.setText("Pose tracking unavailable.")))
                .addOnCompleteListener(task -> proxy.close());
    }

    private double averageLuma(ImageProxy proxy) {
        try {
            ByteBuffer buffer = proxy.getPlanes()[0].getBuffer().duplicate();
            int remaining = buffer.remaining();
            if (remaining == 0) return 255;
            int step = Math.max(1,remaining/1000);
            long sum = 0;
            int count = 0;
            for (int i=0;i<remaining;i+=step) {
                sum += buffer.get(i) & 0xFF;
                count++;
            }
            return count == 0 ? 255 : (double)sum/count;
        } catch(Exception e) {
            return 255;
        }
    }

    private void evaluate(Pose pose,double luma) {
        Metrics m = metrics(pose);
        updateLock(m);

        if (luma < 45) {
            feedback.setText(LanguageManager.t(this,"low_light"));
            checks.setText("Checks: --");
            return;
        }

        if (m.visible < 4) {
            feedback.setText(LanguageManager.t(this,"tracking_waiting"));
            checks.setText("Checks: --");
            return;
        }

        if (m.shoulderWidth < 55) {
            feedback.setText(LanguageManager.t(this,"too_far"));
            checks.setText("Checks: --");
            return;
        }
        if (m.shoulderWidth > 320) {
            feedback.setText(LanguageManager.t(this,"too_close"));
            checks.setText("Checks: --");
            return;
        }

        if (!supported) {
            tracking.setText(locked ? LanguageManager.t(this,"tracking_active") : LanguageManager.t(this,"tracking_waiting"));
            checks.setText(LanguageManager.t(this,"guided_only"));
            feedback.setText(LanguageManager.t(this,"cannot_assess"));
            return;
        }

        int passed = 1;
        boolean armsStraight = m.bestArm >= 155;
        boolean stacked = m.stackDifference <= 0.85;
        updateCadence(m.leftWrist,m.rightWrist);
        boolean rhythmGood = cadence >= 100 && cadence <= 120;

        if (armsStraight) passed++;
        if (stacked) passed++;
        if (rhythmGood) passed++;

        tracking.setText(locked ? LanguageManager.t(this,"tracking_active") : LanguageManager.t(this,"tracking_waiting"));
        tracking.setTextColor(locked ? Ui.GREEN : Ui.RED);
        checks.setText("Checks: "+passed+"/4" + (cadence > 0 ? String.format(Locale.US," · %.0f/min",cadence) : ""));

        if (!locked) feedback.setText(LanguageManager.t(this,"tracking_waiting"));
        else if (!armsStraight) feedback.setText(LanguageManager.t(this,"straighten_arms"));
        else if (!stacked) feedback.setText(LanguageManager.t(this,"shoulders_over_hands"));
        else if (cadence > 0 && cadence < 100) feedback.setText(LanguageManager.t(this,"rhythm_slow"));
        else if (cadence > 120) feedback.setText(LanguageManager.t(this,"rhythm_fast"));
        else feedback.setText(LanguageManager.t(this,"good_form"));
    }

    private void updateLock(Metrics m) {
        if (m.visible < 5 || m.patientLike || m.shoulderWidth < 45) {
            stableFrames=0;
            if (locked && ++lostFrames > 6) locked=false;
            return;
        }

        if (!locked) {
            if (stableFrames == 0) {
                lockX=m.centerX; lockY=m.centerY; lockWidth=m.shoulderWidth; stableFrames=1;
            } else {
                float shift=(float)Math.hypot(m.centerX-lockX,m.centerY-lockY)/Math.max(50f,lockWidth);
                float ratio=m.shoulderWidth/Math.max(1f,lockWidth);
                if (shift < 1.0f && ratio > .65f && ratio < 1.5f) {
                    stableFrames++;
                    lockX=lockX*.75f+m.centerX*.25f;
                    lockY=lockY*.75f+m.centerY*.25f;
                    lockWidth=lockWidth*.75f+m.shoulderWidth*.25f;
                } else {
                    stableFrames=1; lockX=m.centerX; lockY=m.centerY; lockWidth=m.shoulderWidth;
                }
            }
            if (stableFrames >= 7) {
                locked=true;
                lostFrames=0;
            }
            return;
        }

        float shift=(float)Math.hypot(m.centerX-lockX,m.centerY-lockY)/Math.max(50f,lockWidth);
        float ratio=m.shoulderWidth/Math.max(1f,lockWidth);
        if (shift > 2.0f || ratio < .4f || ratio > 2.4f || m.patientLike) {
            if (++lostFrames > 6) {
                locked=false;
                stableFrames=0;
            }
        } else {
            lostFrames=0;
            lockX=lockX*.9f+m.centerX*.1f;
            lockY=lockY*.9f+m.centerY*.1f;
            lockWidth=lockWidth*.9f+m.shoulderWidth*.1f;
        }
    }

    private Metrics metrics(Pose pose) {
        Metrics m = new Metrics();
        m.leftShoulder=pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        m.rightShoulder=pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        m.leftElbow=pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
        m.rightElbow=pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
        m.leftWrist=pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        m.rightWrist=pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
        PoseLandmark leftHip=pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark rightHip=pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);

        PoseLandmark[] upper={m.leftShoulder,m.rightShoulder,m.leftElbow,m.rightElbow,m.leftWrist,m.rightWrist};
        float x=0,y=0;
        for (PoseLandmark landmark:upper) if (good(landmark)) {
            m.visible++; x+=landmark.getPosition().x; y+=landmark.getPosition().y;
        }
        if (m.visible>0) { m.centerX=x/m.visible; m.centerY=y/m.visible; }

        if (good(m.leftShoulder)&&good(m.rightShoulder)) {
            m.shoulderWidth=Math.abs(m.leftShoulder.getPosition().x-m.rightShoulder.getPosition().x);
        }

        double left=good(m.leftShoulder)&&good(m.leftElbow)&&good(m.leftWrist)?angle(m.leftShoulder,m.leftElbow,m.leftWrist):0;
        double right=good(m.rightShoulder)&&good(m.rightElbow)&&good(m.rightWrist)?angle(m.rightShoulder,m.rightElbow,m.rightWrist):0;
        m.bestArm=Math.max(left,right);

        if (good(m.leftShoulder)&&good(m.rightShoulder)&&good(m.leftWrist)&&good(m.rightWrist)) {
            float shoulderX=(m.leftShoulder.getPosition().x+m.rightShoulder.getPosition().x)/2f;
            float wristX=(m.leftWrist.getPosition().x+m.rightWrist.getPosition().x)/2f;
            m.stackDifference=Math.abs(shoulderX-wristX)/Math.max(30f,m.shoulderWidth);
        } else m.stackDifference=10;

        if (good(m.leftShoulder)&&good(m.rightShoulder)&&good(leftHip)&&good(rightHip)) {
            float sx=(m.leftShoulder.getPosition().x+m.rightShoulder.getPosition().x)/2f;
            float sy=(m.leftShoulder.getPosition().y+m.rightShoulder.getPosition().y)/2f;
            float hx=(leftHip.getPosition().x+rightHip.getPosition().x)/2f;
            float hy=(leftHip.getPosition().y+rightHip.getPosition().y)/2f;
            m.patientLike=Math.abs(sx-hx)>Math.abs(sy-hy)*1.35f;
        }
        return m;
    }

    private void updateCadence(PoseLandmark left,PoseLandmark right) {
        if (!good(left)&&!good(right)) return;
        float y=good(left)&&good(right)?(left.getPosition().y+right.getPosition().y)/2f:good(left)?left.getPosition().y:right.getPosition().y;
        long now=System.currentTimeMillis();
        if (!Float.isNaN(previousY)) {
            float delta=y-previousY;
            if (delta>7) movingDown=true;
            if (movingDown&&delta<-7&&now-lastPeak>280) {
                peaks.add(now);
                lastPeak=now;
                movingDown=false;
            }
        }
        previousY=y;
        Iterator<Long> iterator=peaks.iterator();
        while(iterator.hasNext()) if(now-iterator.next()>8000) iterator.remove();
        if(peaks.size()>=3) {
            long first=peaks.get(0),last=peaks.get(peaks.size()-1);
            if(last>first) cadence=(peaks.size()-1)*60000f/(last-first);
        }
    }

    private void resetTracking() {
        locked=false; stableFrames=0; lostFrames=0; peaks.clear(); previousY=Float.NaN; movingDown=false; cadence=-1;
        tracking.setText(LanguageManager.t(this,"tracking_waiting"));
        checks.setText(supported?"Checks: --":LanguageManager.t(this,"guided_only"));
    }

    private boolean good(PoseLandmark landmark) {
        return landmark!=null&&landmark.getInFrameLikelihood()>=.55f;
    }

    private double angle(PoseLandmark a,PoseLandmark b,PoseLandmark c) {
        double abx=a.getPosition().x-b.getPosition().x, aby=a.getPosition().y-b.getPosition().y;
        double cbx=c.getPosition().x-b.getPosition().x, cby=c.getPosition().y-b.getPosition().y;
        double dot=abx*cbx+aby*cby;
        double mag=Math.sqrt(abx*abx+aby*aby)*Math.sqrt(cbx*cbx+cby*cby);
        if(mag==0)return 0;
        double value=Math.max(-1,Math.min(1,dot/mag));
        return Math.toDegrees(Math.acos(value));
    }

    private static final class Metrics {
        int visible;
        float centerX,centerY,shoulderWidth;
        double bestArm,stackDifference;
        boolean patientLike;
        PoseLandmark leftShoulder,rightShoulder,leftElbow,rightElbow,leftWrist,rightWrist;
    }

    @Override protected void onDestroy() {
        if(provider!=null) provider.unbindAll();
        if(detector!=null) detector.close();
        if(cameraExecutor!=null) cameraExecutor.shutdown();
        super.onDestroy();
    }
}
