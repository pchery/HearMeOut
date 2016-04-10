package carlhacks16.hearmeout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import carlhacks16.hearmeout.database.DatabaseHelper;
import carlhacks16.hearmeout.ui.Intro;

public class Result2 extends AppCompatActivity {


    protected Button mVolumeButton;
    protected Button mMovementButton;
    protected DatabaseHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);
        //Intent intent= getIntent();
        //int volume = intent.getIntExtra("volumescore",0);



        mVolumeButton =(Button)findViewById(R.id.volumeButton);
        mMovementButton =(Button)findViewById(R.id.movementButton);

        mDbHelper = new DatabaseHelper(this);

        int volume = mDbHelper.getLatestSession().getVolume();
        int movements = mDbHelper.getLatestSession().getMovements();

        System.out.println("MOVEMENTS: " + movements);


        mVolumeButton.setText("VOLUME: " + Integer.toString(volume) + "/10");
        mMovementButton.setText("MOVEMENT: " + Integer.toString(movements) + "/10");

        Button end=(Button)findViewById(R.id.nextButton);

        end.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(Result2.this, Intro.class));

            }
        });




    }


}