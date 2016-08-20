package Proccess;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;

import org.apache.http.impl.cookie.BasicMaxAgeHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Objects.Instruments;
import Objects.Sound;

/**
 * Created by Pan on 11/11/2015.
 * will just read storage/final.wav to analysis to make the operation easier(no parameter passing is needed!)
 */
public class Proccessing {
    public static List<Sound> analysis_wav() {

        try {
            Thread.sleep(3000);                 //return null after 10s
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        Sound sound = new Sound(0, 1000, 200);
        List<Sound> result = new ArrayList<Sound>();
        result.add(sound);
        return result;
    }

    //Create Music call music.wav 
    public static void CreateMusic(List<Sound> sounds, List<Instruments> instruments) {
        //playSound(1500.00, 44100);//frequency 1500Hz, duration 5 seconds ｝just demo  
        playSound(sounds, instruments);
    }

    private static void playSound(List<Sound> sounds, List<Instruments> instruments) {

        //AudioTrack definition
        // float decay_multiplier;
        float time_constant=(float)15000;
        int total_samples;

        int duration = 44100;//number of sample 1 second = 44100 
        int mBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize, AudioTrack.MODE_STREAM);
        mAudioTrack.flush();
        //Sine wave 
        total_samples = (int) Math.ceil((sounds.get(sounds.size() - 1).time + 1) * 44100);
        int[] mBuffer = new int[total_samples];
        short[] mBufferBoosted = new short[total_samples];
        float presound = -1;
        /*
        for (int i = 0; i < sounds.size(); i++) {
            if((Math.abs(sounds.get(i).time-presound))<0.2)
            {
                sounds.remove(i);
                i--;
            }
            else {
                presound = sounds.get(i).time;
            }
        }*/
        for (int i = 0; i < mBufferSize; i++) {
            mBuffer[i] = 0;
            mBufferBoosted[i] = 0;
        }
        for(int in = 0;in<instruments.size();in++) {
            for (int i = 0; i < mBufferSize; i++)
                mBuffer[i] = 0;
            for (int i = 0; i < sounds.size(); i++) {
                if(sounds.get(i).time>instruments.get(in).start_time && sounds.get(i).time<instruments.get(in).end_time)
                for (int j = 0; j < 44100; j++) {
                    int current = 0;
                    if(instruments.get(in).instrument_number == 0) {
                        //mBuffer[(int) Math.ceil(sounds.get(i).time * 44100)] = (short)(Short.MAX_VALUE* Math.sin((2*Math.PI * j / (44100 / sounds.get(i).frequency))));
                        float mSound = (float)Math.sin((2.0 * Math.PI * j / (44100) * sounds.get(i).frequency));
                        current += (short)(mSound * Short.MAX_VALUE)*Math.exp(-1.0*j/time_constant);
                         //decay_multiplier = (float)(Math.exp(-1 * mBufferSize / time_constant));
                    }
                    else if (instruments.get(in).instrument_number == 1)//piano
                    {
                        double mSound=0.47*Math.sin(2.0 * Math.PI * j / (44100 / (sounds.get(i).frequency*1)))+
                                0.23*Math.sin(2.0 * Math.PI * j / (44100 / (sounds.get(i).frequency*5)))+
                                0.07*Math.sin(2.0 * Math.PI * j / (44100 / (sounds.get(i).frequency*7)))+
                                            0.23*Math.sin(2.0 * Math.PI * j / (44100 / (sounds.get(i).frequency*8)));

                        current += (short)((mSound * Short.MAX_VALUE) * Math.exp(-1.0*j/time_constant));

                    }
                    else
                    {//Violin

                        double mSound=0.40*Math.sin(1.0 * Math.PI * j / (44100 / (sounds.get(i).frequency*1)))+
                                0.30*Math.sin(2.0 * Math.PI * j / (44100 / (sounds.get(i).frequency*2)))+
                                        0.18*Math.sin(3.0 * Math.PI * j / (44100 / (sounds.get(i).frequency*3)))+
                                                0.12*Math.sin(4.0 * Math.PI * j / (44100 / (sounds.get(i).frequency*4)));
                        current += (short)((mSound * Short.MAX_VALUE) * Math.exp(-1.0*j/time_constant));

                    }
                            mBuffer[(int) Math.ceil(sounds.get(i).time * 44100) + j] = current;
                }
            }
            int max = 0;
            int min = 0;
            for(int i = 0; i < total_samples; i++)
            {
                if(mBuffer[i]>max)
                    max = mBuffer[i];
                if(mBuffer[i]<min)
                    min = mBuffer[i];
            }
            max = max>Math.abs(min)?max:Math.abs(min);
            float multiplier = ((float)Short.MAX_VALUE/instruments.size()/max);
            for(int i = 0; i < total_samples; i++)
            {
                mBufferBoosted[i] = (short)(mBufferBoosted[i]+((float)mBuffer[i]*multiplier)/instruments.size());//normalize
            }
        }
        //one more boost after combine
        /*
        int max = 0;
        int min = 0;
        for(int i = 0; i < total_samples; i++)
        {
            if(mBufferBoosted[i]>max)
                max = mBufferBoosted[i];
            if(mBufferBoosted[i]<min)
                min = mBufferBoosted[i];
        }
        max = max>Math.abs(min)?max:Math.abs(min);
        float multiplier = ((float)Short.MAX_VALUE/instruments.size()/max);
        for(int i = 0; i < total_samples; i++)
        {
            mBufferBoosted[i] = (short)((float)mBufferBoosted[i]*multiplier*0.9);//normalize
        }*/
        mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
        //mAudioTrack.write(mBuffer, 0, mBuffer.length);
        mAudioTrack.play();
        mAudioTrack.write(mBufferBoosted, 0, mBuffer.length);
        mAudioTrack.stop();
        mAudioTrack.release();
    }
}

