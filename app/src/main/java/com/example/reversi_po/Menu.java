package com.example.reversi_po;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {
    private Button button_play;
    private Button button_settings;
    private Intent intent;
    private Intent basicIntent;
    private Bundle bundle = new Bundle();
    private Bundle bundle2 = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        button_play = findViewById(R.id.play);
        button_settings = findViewById(R.id.settings);

        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, MainActivity.class);
                Intent basicIntent = new Intent(Menu.this, MainActivity.class);
                try
                {
                    bundle = getIntent().getExtras();
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                catch(Exception e)
                {
                    bundle2.putInt("scale", 80);
                    bundle2.putString("pawn", "Classic");
                    bundle2.putLong("time", 60000);
                    basicIntent.putExtras(bundle2);
                    startActivity(basicIntent);
                }
            }
        });

        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Settings.class);
                startActivity(intent);
            }
        });

    }
}
