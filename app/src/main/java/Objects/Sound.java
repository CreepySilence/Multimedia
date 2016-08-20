package Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Pan on 11/9/2015.
 */

//will pass a List<Sound> to create music
public class Sound implements Parcelable {
    public float time; //Start time = 0, first 1s = 1
    public float amplitude;
    public float frequency;
    public float prob;

    public static final Parcelable.Creator<Sound> CREATOR
            = new Parcelable.Creator<Sound>() {
        public Sound createFromParcel(Parcel p) {
            return new Sound(p);
        }

        @Override
        public Sound[] newArray(int size) {
            return new Sound[size];
        }
    };
    public Sound(Parcel p)
    {
        this.time = p.readFloat();
        this.amplitude = p.readFloat();
        this.frequency = p.readFloat();
        this.prob = p.readFloat();
    }
    public Sound(float time, float amplitude, float frequency)
    {
        this.time = time;
        this.amplitude = amplitude;
        this.frequency = frequency;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(time);
        dest.writeFloat(amplitude);
        dest.writeFloat(frequency);
        dest.writeFloat(prob);
    }
}
