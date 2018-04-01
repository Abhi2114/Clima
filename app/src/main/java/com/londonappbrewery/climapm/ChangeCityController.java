package com.londonappbrewery.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);

        // link the views to the java code
        final EditText editTextField = (EditText) findViewById(R.id.queryET);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);

        // go back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // add a listener, will be called when the user taps the enter key on
        // the soft-keyboard
        editTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String city = editTextField.getText().toString();
                // create a new intent to pass the city name to the
                // weatherController layout
                Intent cityIntent = new Intent(ChangeCityController.this, WeatherController.class);
                cityIntent.putExtra("city", city);
                startActivity(cityIntent);

                return false;
            }
        });
    }
}
