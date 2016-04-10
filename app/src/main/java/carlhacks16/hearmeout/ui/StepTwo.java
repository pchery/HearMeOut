package carlhacks16.hearmeout.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import carlhacks16.hearmeout.R;
import carlhacks16.hearmeout.StartSpeechActivity;

public class StepTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_two);


        ImageButton btn2=(ImageButton)findViewById(R.id.imageButton3);

        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(StepTwo.this, StartSpeechActivity.class));

            }
        });


    }

}
