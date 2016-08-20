package Adapter;

/**
 * Created by Pan on 11/15/2015.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import Objects.Instruments;
import Proccess.Proccessing;
import hkust.cse.cseinfo.ConfigActivity;
import hkust.cse.cseinfo.R;

public class CustomList extends ArrayAdapter<Instruments>{

    private final Activity context;
    private List<Instruments> instrumentsList;
    public CustomList(Activity context,
                      List<Instruments> instrumentsList) {
        super(context, R.layout.list_view_child, instrumentsList);
        this.context = context;
        this.instrumentsList = instrumentsList;
    }
    @Override
    public View getView(final int position, final View view, final ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView= inflater.inflate(R.layout.list_view_child, null, true);
        final EditText start = (EditText) rowView.findViewById(R.id.EditText_start);
        final EditText end = (EditText) rowView.findViewById(R.id.EditText_end);
        final TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_name_instrument);
        final SeekBar volumn = (SeekBar) rowView.findViewById(R.id.seekBar_Volume);
        Instruments selected_instrument = instrumentsList.get(position);
        txtTitle.setText(selected_instrument.Name);
        start.setText(selected_instrument.start_time+"");
        end.setText(selected_instrument.end_time+"");
        Button play_single = (Button) rowView.findViewById(R.id.button_play_single);
        volumn.setProgress(selected_instrument.volumn);
        play_single.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<Instruments> array = new ArrayList<Instruments>();
                Instruments instrument = new Instruments();
                instrument.Name = txtTitle.getText().toString();
                instrument.instrument_number =0;
                if(instrument.Name.equals("Instrument 1"))
                    instrument.instrument_number =1;
                if(instrument.Name.equals("Instrument 2"))
                    instrument.instrument_number =2;
                instrument.volumn = volumn.getProgress();
                instrument.start_time = Float.parseFloat(start.getText().toString());
                instrument.end_time = Float.parseFloat(end.getText().toString());
                array.add(instrument);
                Proccessing.CreateMusic(ConfigActivity.analysisedResult, array);
            }
        });
        Button delete_single = (Button) rowView.findViewById(R.id.button_delete_single);
        delete_single.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConfigActivity.instruments.remove(position);
                //Proccessing.CreateMusic(sounds, instruments);
                CustomList adapter = new
                        CustomList(context, ConfigActivity.instruments);
                ConfigActivity.list=(ListView)context.findViewById(R.id.mListView);
                ConfigActivity.list.setAdapter(adapter);
            }
        });
        return rowView;
    }
}