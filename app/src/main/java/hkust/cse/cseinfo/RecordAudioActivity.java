package hkust.cse.cseinfo;
import android.app.Activity;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.content.Context;
import android.util.Log;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;


public class RecordAudioActivity extends Activity
{
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private RecordButton mRecordButton = null;
    private TestAudioRecord mRecorder = null;

    private MediaPlayer   mPlayer = null;

    public void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    public void startPlaying() {
        mRecorder.startPlaying();
    }

    public void stopPlaying() {
        mRecorder.stopPlaying();
    }

    public void startRecording() {
        mRecorder = new TestAudioRecord();
        mRecorder.setOutputFile(mFileName);
        /*
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
*/
        mRecorder.startRecording();
        

    }

    public void stopRecording() {
    	/*
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        */
    	// Stop recording
    	mRecorder.stopRecording();
    	//File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/tmp.raw");
    	//file.delete();
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    public RecordAudioActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/final.wav";
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            //mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}