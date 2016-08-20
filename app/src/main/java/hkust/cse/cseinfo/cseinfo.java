package hkust.cse.cseinfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.lang.Runnable;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import Objects.Sound;
import Proccess.Proccessing;

import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.AudioEvent;

public class cseinfo extends Activity {
    /** Called when the activity is first created. */
    AudioDispatcher dispatcher ;
    Long spentTime;
    PitchDetectionHandler pdh;
    private boolean mStartRecording = true;
    List<Sound> analysisedResult; //sound

    boolean stop = true;
    float pre_freq = 0;

    /** Timer **/
    private Long startTime;
    private Handler handler = new Handler();
    private TextView time;
    Button Button_RecStop;
    Button Button_Library;
    Button Button_ready;
    /**Save? not yet done**/
    private boolean isSave;

    /**Recording Class**/
    private RecordAudioActivity audio;



    private void save(){

        audio.stopRecording();
        File savefile = new File (Environment.getExternalStorageDirectory().getAbsolutePath()+"/final.wav");
        File destfile= new File("C:/Users/Lee/Desktop/file.wav");
        destfile.renameTo(savefile);

    }
    private Runnable updateTimer = new Runnable() {
        public void run() {
            final TextView time = (TextView) findViewById(R.id.timer);
            spentTime = System.currentTimeMillis() - startTime;
            Long minius = (spentTime/1000)/60;
            Long seconds = (spentTime/1000) % 60;
            if (seconds==0 && minius==0)
                time.setText("00:00");
            else if (seconds < 10 && minius < 10)
                time.setText("0"+minius+":0"+seconds);
            else if (seconds>=10 && minius < 10)
                time.setText("0"+minius+":"+seconds);
            else if (seconds>=10 && minius>= 10)
                time.setText(minius+":"+seconds);

            handler.postDelayed(this, 1000);
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        audio = new RecordAudioActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cseinfo);;
        Button_RecStop = (Button)findViewById(R.id.button_recstop);
        Button_Library = (Button)findViewById(R.id.button_library);
        Button_ready = (Button)findViewById((R.id.button_ready));
        Button_RecStop.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                time = (TextView) findViewById(R.id.timer);
                if (mStartRecording){
                    //Start record (it will work when smartphone is used)
                    //audio.onRecord(mStartRecording);
                    analysisedResult = new ArrayList<Sound>();
                    //Audio Pitch Detection
                    dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

                    pdh = new PitchDetectionHandler() {
                        @Override
                        public void handlePitch(PitchDetectionResult result,AudioEvent e) {
                            final float pitchInHz = result.getPitch();
                            final float prob=result.getProbability();

                            float time = ((float)(System.currentTimeMillis() - startTime))/1000;//这里要改,时间不一直是零
                            if (pitchInHz != -1 && prob>0.9 && Math.abs(pre_freq-pitchInHz)>5) {
                                Sound sound = new Sound(time, 0, pitchInHz);
                                sound.prob = prob;
                                analysisedResult.add(sound);
                                pre_freq = pitchInHz;
                            }
                            if(prob>0.9 ){

                                pre_freq = pitchInHz;

                            }


                        }
                    };
                    AudioProcessor p = new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
                    dispatcher.addAudioProcessor(p);
                    new Thread(dispatcher,"Audio Dispatcher").start();



                    //Timer
                    time.setVisibility(View.VISIBLE);
                    startTime = System.currentTimeMillis();
                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);

                    //internal setting
                    mStartRecording = !mStartRecording;
                    Button_RecStop.setBackgroundDrawable(getResources().getDrawable(R.drawable.stopbtn));
                }
                else{
                    //Invisible the time textfield and stop timing
                    time.setVisibility(View.INVISIBLE);
                    handler.removeCallbacks(updateTimer);

                    //Auto Save
                    //save();
                    dispatcher.stop();
                    //internal setting
                    mStartRecording = !mStartRecording;
                    Button_RecStop.setBackgroundDrawable(getResources().getDrawable(R.drawable.recbtn));
                    Button_ready.setVisibility(View.VISIBLE);//can proceed
                }
            }
        });
        Button_Library.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                // link to database library
                audio.onPlay(true);
            }

        });
        Button_ready.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                // link to database library
                ProgressBar loadingbar = (ProgressBar)findViewById(R.id.loadingbar);
                loadingbar.setVisibility(View.VISIBLE);
                //hide all button
                Button_ready.setVisibility(View.INVISIBLE);
                Button_RecStop.setVisibility(View.INVISIBLE);
                Button_Library.setVisibility(View.INVISIBLE);
                //
                new AnalysisTask().execute();
            }

        });
    }
    class AnalysisTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //analysisedResult = Proccessing.analysis_wav();
                return true;
            }catch (Exception e)
            {
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(getApplicationContext(), "Proccessing Success!!", Toast.LENGTH_SHORT).show();
                ProgressBar loadingbar = (ProgressBar)findViewById(R.id.loadingbar);
                loadingbar.setVisibility(View.INVISIBLE);
                //display all button
                Button_ready.setVisibility(View.VISIBLE);
                Button_RecStop.setVisibility(View.VISIBLE);
                Button_Library.setVisibility(View.VISIBLE);
                //
                Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                intent.putExtra("EndTime", ((float) spentTime/1000));
                intent.putParcelableArrayListExtra("AnalysisedResult", (ArrayList<Sound>) analysisedResult);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), "Proccessing Fail!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
