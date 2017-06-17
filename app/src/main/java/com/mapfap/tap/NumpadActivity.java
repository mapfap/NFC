package com.mapfap.tap;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by mapfap on 6/17/2017 AD.
 */

public class NumpadActivity extends AppCompatActivity {

    protected String currentText = "";
    protected int maxLength = 10;
    protected TextView numTextView;
    protected Button[] buttons = new Button[10];

    protected Animation createAnimation() {
        return AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numpad);

        View rootView = findViewById(R.id.activity_numpad);
        Snackbar.make(rootView, getIntent().getStringExtra("MESSAGE"), Snackbar.LENGTH_LONG).show();

        Typeface kanitMedium = Typeface.createFromAsset(getAssets(), "Kanit-Medium.ttf");

        numTextView = (TextView) findViewById(R.id.numpad_textview);

        buttons[0] = (Button) findViewById(R.id.num0);
        buttons[1] = (Button) findViewById(R.id.num1);
        buttons[2] = (Button) findViewById(R.id.num2);
        buttons[3] = (Button) findViewById(R.id.num3);
        buttons[4] = (Button) findViewById(R.id.num4);
        buttons[5] = (Button) findViewById(R.id.num5);
        buttons[6] = (Button) findViewById(R.id.num6);
        buttons[7] = (Button) findViewById(R.id.num7);
        buttons[8] = (Button) findViewById(R.id.num8);
        buttons[9] = (Button) findViewById(R.id.num9);


        for (int i = 0; i < 10; i++ ) {
            buttons[i].setOnClickListener(new TypeNumber(i));
            buttons[i].setTypeface(kanitMedium);
        }

        Button enter = (Button) findViewById(R.id.num_enter);
        enter.setTypeface(kanitMedium);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(createAnimation());
                getIntent().putExtra("NUMBER", currentText);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        Button delete = (Button) findViewById(R.id.num_delete);
        delete.setTypeface(kanitMedium);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentText.length() > 0) {
                    v.startAnimation(createAnimation());
                    currentText = currentText.substring(0, currentText.length() - 1);
                    numTextView.setText(currentText);
                } else {
                    numTextView.startAnimation(createAnimation());
                }
            }
        });

        numTextView.setTypeface(kanitMedium);

    }

    private class TypeNumber implements View.OnClickListener {

        private int number;

        public TypeNumber(int number) {
            this.number = number;
        }

        @Override
        public void onClick(View v) {
            if (currentText.length() < maxLength) {
                buttons[number].startAnimation(createAnimation());
                currentText = currentText + number;
                numTextView.setText(currentText);
            } else {
                numTextView.startAnimation(createAnimation());
                Snackbar.make(v, "Text exceeds maximum length.", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
