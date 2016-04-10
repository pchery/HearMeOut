package carlhacks16.hearmeout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Button;
import android.view.View;
import android.media.MediaPlayer;
import android.widget.TextView;
import android.widget.Toast;



import carlhacks16.hearmeout.database.DatabaseHelper;
import carlhacks16.hearmeout.database.SessionContract;
import carlhacks16.hearmeout.models.Session;

public class StartSpeechRecognitionActivity extends AppCompatActivity {

    protected DatabaseHelper mDbHelper;
    protected Button recordButton;
    protected Chronometer mChronometer;
    protected float mStartTime;
    protected float mEndTime;
    protected int mWordCount;
    protected Button mNextButton;
    protected StringTokenizer tokenizer;
    private static final UUID SPORTS_UUID = UUID.fromString("4403bc13-03db-450f-bdb7-95e3739089b0");
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_speech_recognition);
        mDbHelper = new DatabaseHelper(this);
        recordButton = (Button) findViewById(R.id.recordButton);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mNextButton = (Button) findViewById(R.id.button10);


        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mChronometer.setBase(SystemClock.elapsedRealtime());
                Session session = new Session();
                mDbHelper.createSession(session);

                promptSpeechInput();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StartSpeechRecognitionActivity.this, Result1.class);
                startActivity(intent);

            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();

    }



    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        long timeElapsed = (SystemClock.elapsedRealtime() - mChronometer.getBase())/1000;
        mChronometer.stop();



        switch (requestCode) {
            case VOICE_RECOGNITION_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    int filler = 0;
                    mWordCount = 0;
                    String s = result.get(0);

                    tokenizer = new StringTokenizer(s, " ");
                    while(tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        mWordCount++;
                        if (token.equals("like") || token.equals("so") || token.equals("well") || token.equals("um")) {
                            filler++;
                        }
                    }
                    mDbHelper.updateSession(10 - filler, SessionContract.Session.FILLERS);
                    //}
                    int speed = (int) (mWordCount / timeElapsed) * 60;
                    mDbHelper.updateSession(speed, SessionContract.Session.SPEED);

                    System.out.println("//////////////////////////////////////// TIME: " + timeElapsed + " ///// SPEED: " + speed);
                    System.out.println("//////////////////////////     NUM WORDS: " + mWordCount + "   ////////////" );
                    System.out.println("//////////////////////////////    " + filler+ "    /////////////////");
                    Toast.makeText(this, result.get(0), Toast.LENGTH_LONG).show();
                }
                break;
            }

        }
    }
}
