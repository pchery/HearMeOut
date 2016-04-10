package carlhacks16.hearmeout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import carlhacks16.hearmeout.R;

public class Result extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent=getIntent();
        int volumn=intent.getIntExtra("volumescore",0);



        Button volumebtn =(Button)findViewById(R.id.button7);
        volumebtn.setText("VOLUME: " + Integer.toString(volumn) + "/10");


    }


}