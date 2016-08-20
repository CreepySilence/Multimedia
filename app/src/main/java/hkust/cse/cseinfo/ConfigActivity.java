package hkust.cse.cseinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import Adapter.CustomList;
import Objects.Instruments;
import Objects.Sound;
import Proccess.Proccessing;




public class ConfigActivity extends Activity {

    public static List<Sound> analysisedResult; //suppose only one sequence in the app
    public static List<Instruments> instruments;
    public static ListView list;
    float end_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        analysisedResult = getIntent().getParcelableArrayListExtra("AnalysisedResult");
        end_time = getIntent().getFloatExtra("EndTime",0);
        instruments = new ArrayList<Instruments>(); //initialize list of instruments
        //Test ListView

        final Spinner spinner = (Spinner) findViewById(R.id.instrument_spinner);
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this,

                R.array.menu_instruments, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        Button add_button = (Button) findViewById(R.id.add_button);
        add_button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                add_instruments(spinner.getSelectedItemPosition());
            }
        });
        Button play_all = (Button)findViewById(R.id.playall_button);
        play_all.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<Instruments> array = new ArrayList<Instruments>();
                for (int i = 0; i < instruments.size(); i++) {
                    //int rownumber = spinner.getChildCount();
                    View rowView = list.getChildAt(i);
                    EditText start = (EditText) rowView.findViewById(R.id.EditText_start);
                    EditText end = (EditText) rowView.findViewById(R.id.EditText_end);
                    TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_name_instrument);
                    SeekBar volumn = (SeekBar) rowView.findViewById(R.id.seekBar_Volume);
                    Instruments instrument = new Instruments();
                    //instrument.instrument_number = instruments.get(i).instrument_number;
                    instrument.Name = txtTitle.getText().toString();
                    instrument.volumn = volumn.getProgress();
                    if(instrument.Name.equals("Instrument 1"))
                        instrument.instrument_number =1;
                    if(instrument.Name.equals("Instrument 2"))
                        instrument.instrument_number =2;
                    instrument.start_time = Float.parseFloat(start.getText().toString());
                    instrument.end_time = Float.parseFloat(end.getText().toString());
                    array.add(instrument);
                }
                Proccessing.CreateMusic(ConfigActivity.analysisedResult, array);
            }
        });
    }
    void add_instruments(int selected)
    {
        Instruments instrument = new Instruments();
        instrument.instrument_number = selected;
        instrument.start_time = 0;
        instrument.end_time = ((float)Math.round(end_time*100))/100;
        instrument.volumn = 100;
        if(selected == 0)
            instrument.Name = "Sin Wave";
        else if(selected == 1)
            instrument.Name = "Instrument 1";
        else if(selected == 2)
            instrument.Name = "Instrument 2";

        instruments.add(instrument);
        //Proccessing.CreateMusic(sounds, instruments);
        CustomList adapter = new
                CustomList(this, instruments);
        list=(ListView)findViewById(R.id.mListView);
        list.setAdapter(adapter);
        /*
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(ConfigActivity.this, "You Clicked at " + ((TextView) view.findViewById(R.id.textView_name_instrument)).getText(), Toast.LENGTH_SHORT).show();

            }
        });
        */
    }
}
