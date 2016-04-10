package carlhacks16.hearmeout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import carlhacks16.hearmeout.database.DatabaseHelper;
import carlhacks16.hearmeout.database.SessionContract;
import carlhacks16.hearmeout.ui.StepTwo;

/**
 * Created by paulchery on 4/10/16.
 */
public class Result1 extends AppCompatActivity {

    protected Button mFillersButton;
    protected Button mSpeedButton;
    protected Button mNextButton;
    protected DatabaseHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result1);

        mFillersButton = (Button) findViewById(R.id.fillersButton);
        mSpeedButton = (Button) findViewById(R.id.speedButton);
        mNextButton = (Button) findViewById(R.id.nextButton);

        mDbHelper = new DatabaseHelper(this);



        if(mDbHelper.count(SessionContract.Session.TABLE_NAME) > 1) {
            int speed = mDbHelper.getLatestSession().getSpeed();
            int fillers = mDbHelper.getLatestSession().getFillers();
            mSpeedButton.setText("SPEED: " + Integer.toString(speed) + "/10");
            mFillersButton.setText("FILLERS: " + Integer.toString(fillers) + "/10");
        }
        else{
            mSpeedButton.setText("SPEED: 0/10");
            mFillersButton.setText("FILLERS: 0/10");
        }

        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(Result1.this, StepTwo.class));

            }
        });

        mSpeedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        mFillersButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });




    }
}




