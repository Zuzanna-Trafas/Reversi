package com.example.reversi_po;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class Settings extends AppCompatActivity {
    private Spinner spinner;
    Intent intent;
    Bundle bundle = new Bundle();
    Button button;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView time;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radioGroup = findViewById(R.id.radioGroup);
        time = findViewById(R.id.displayTime);
        seekBar = findViewById(R.id.seekBar);
        button = findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 int radioId = radioGroup.getCheckedRadioButtonId();
                 radioButton = findViewById(radioId);
                 long seekValue = seekBar.getProgress() * 1000 + 30000;
                 bundle.putLong("time", seekValue);
                 bundle.putString("pawn", (String) radioButton.getText());
                 intent.putExtras(bundle);
                 startActivity(intent);
             }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long seekValue = seekBar.getProgress() * 1000 + 30000;
                int minutes = (int) (seekValue / 1000) / 60;
                int seconds = (int) (seekValue / 1000) % 60;
                String timeLeftFormat = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
                time.setText("" + timeLeftFormat);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        intent = new Intent(getApplicationContext(), Menu.class);

        spinner = findViewById(R.id.scales);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Settings.this,
            android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.scales));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    bundle.putInt("scale", 90);
                } else if (position == 2) {
                    bundle.putInt("scale", 80);
                } else if (position == 3) {
                    bundle.putInt("scale", 50);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
