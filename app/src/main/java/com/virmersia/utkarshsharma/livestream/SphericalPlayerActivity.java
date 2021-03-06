package com.virmersia.utkarshsharma.livestream;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.virmersia.utkarshsharma.livestream.MainActivity;
import com.virmersia.utkarshsharma.livestream.SphericalVideoPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SphericalPlayerActivity extends AppCompatActivity {
    private final String SAMPLE_VIDEO_PATH ="http://35.154.133.57:1935/vods3/_definst_/mp4:amazons3/vimersia/vim/shark.mp4/playlist.m3u8";

    //"android.resource://com.oculus.sample/raw/" + R.raw.sample360;

    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0x1;

    private SphericalVideoPlayer videoPlayer;
//    private MainActivity Ma= new MainActivity();
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spherical_player);
        videoPlayer = (SphericalVideoPlayer) findViewById(R.id.spherical_video_player);


        Bundle extras = getIntent().getExtras();
        path = extras.getString("userInput");
        videoPlayer.setVideoURIPath(path);
        videoPlayer.playWhenReady();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestExternalStoragePermission();
    }
void getPath(String s)
{
    s=path;
}
    @Override
    protected void onPause() {
        super.onPause();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            init();
        }
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(
                context,
                msg,
                Toast.LENGTH_SHORT).show();
    }

    private void init() {
        videoPlayer.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                videoPlayer.initRenderThread(surface, width, height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                videoPlayer.releaseResources();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        videoPlayer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            return;
        }

        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            toast(this, "Access not granted for reading video file :(");
            return;
        }

        init();
    }

    public static String readRawTextFile(Context context, int resId) {
        InputStream is = context.getResources().openRawResource(resId);
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader buf = new BufferedReader(reader);
        StringBuilder text = new StringBuilder();
        try {
            String line;
            while ((line = buf.readLine()) != null) {
                text.append(line).append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
