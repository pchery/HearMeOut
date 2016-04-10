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

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import carlhacks16.hearmeout.database.DatabaseHelper;
import carlhacks16.hearmeout.database.SessionContract;
import carlhacks16.hearmeout.models.Session;

public class StartSpeechRecognitionActivity extends AppCompatActivity {

    private PebbleKit.PebbleDataReceiver pebbleReceiver;
    protected DatabaseHelper mDbHelper;
    protected Button recordButton;
    protected Button pebbleLaunch;
    protected Chronometer mChronometer;
    protected Button pebbleStop;
    protected TextView movementDisplay;
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
        pebbleLaunch = (Button) findViewById(R.id.pebbleLaunch);
        pebbleStop = (Button) findViewById(R.id.pebbleStop);
        movementDisplay = (TextView) findViewById(R.id.movementDisplay);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mNextButton = (Button) findViewById(R.id.button10);


        pebbleLaunch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //check this
                PebbleKit.startAppOnPebble(v.getContext(), SPORTS_UUID);

                //movementDisplay.setText("HELLO");

            }

        });

        pebbleStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                PebbleKit.closeAppOnPebble(v.getContext(), SPORTS_UUID);


            }

        });

        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                PebbleKit.startAppOnPebble(v.getContext(), SPORTS_UUID);
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

        boolean isConnected = PebbleKit.isWatchConnected(this);
        Toast.makeText(this, "Pebble " + (isConnected ? "is" : "is not") + " connected!", Toast.LENGTH_LONG).show();


        if (pebbleReceiver == null) {
            pebbleReceiver = new PebbleKit.PebbleDataReceiver(SPORTS_UUID) {
                @Override
                public void receiveData(Context context, int id, PebbleDictionary data) {
                    // Always ACKnowledge the last message to prevent timeouts
                    PebbleKit.sendAckToPebble(getApplicationContext(), id);
                    final int AppKey = 0;
                    // Get action and display
                    int state = data.getInteger(AppKey).intValue();

                    System.out.println("////////////////////////////////////////////////" + state);
                    movementDisplay.setText(String.valueOf(state));
                    mDbHelper.updateSession(state, SessionContract.Session.MOVEMENT);

                }

            };
        }
        PebbleKit.registerReceivedDataHandler(this, pebbleReceiver);
    }



    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        mStartTime = SystemClock.currentThreadTimeMillis();
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

        mEndTime = SystemClock.currentThreadTimeMillis();
        mChronometer.stop();
        PebbleKit.closeAppOnPebble(this, SPORTS_UUID);

        float timeElapsed = mEndTime - mStartTime;
        int speed = (int) (mWordCount / timeElapsed);

        mDbHelper.updateSession(speed, SessionContract.Session.SPEED);

        System.out.println("//////////////////////////////////////// TIME: " + timeElapsed + " ///// SPEED: " + speed);
        switch (requestCode) {
            case VOICE_RECOGNITION_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    int filler = 0;
                    mWordCount = 0;
                    //for(String s: result){
                    String s = result.get(0);

                    tokenizer = new StringTokenizer(s, " ");
                    while(tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        mWordCount++;
                        if (token.equals("like") || token.equals("so") || token.equals("well") || token.equals("um")) {
                            filler++;
                        }
                    }
                    mDbHelper.updateSession(filler, SessionContract.Session.FILLERS);
                    //}
                    System.out.println("//////////////////////////     NUM WORDS: " + mWordCount + "   ////////////" );
                    System.out.println("//////////////////////////////    " + filler+ "    /////////////////");
                    Toast.makeText(this, result.get(0), Toast.LENGTH_LONG).show();
                }
                break;
            }

        }
    }
}
