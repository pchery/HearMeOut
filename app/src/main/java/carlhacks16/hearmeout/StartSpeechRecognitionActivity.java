package carlhacks16.hearmeout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

import android.content.ActivityNotFoundException;
import android.content.Context;
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

public class StartSpeechRecognitionActivity extends AppCompatActivity {

    protected MediaRecorder mediaRecorder;
    protected MediaPlayer mediaPlayer;

    private static String audioFilePath;
    private PebbleKit.PebbleDataReceiver pebbleReceiver;
    protected Button stopButton;
    protected Button playButton;
    protected Button recordButton;
    protected Button pebbleLaunch;
    protected Button pebbleStop;
    protected TextView movementDisplay;
    protected StringTokenizer tokenizer;
    private static final UUID SPORTS_UUID = UUID.fromString("4403bc13-03db-450f-bdb7-95e3739089b0");
    private static SpeechRecognitionHelper speechRecognitionHelper;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_speech_recognition);

        recordButton = (Button) findViewById(R.id.recordButton);
        playButton = (Button) findViewById(R.id.playButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        pebbleLaunch = (Button) findViewById(R.id.pebbleLaunch);
        pebbleStop = (Button) findViewById(R.id.pebbleStop);
        movementDisplay = (TextView) findViewById(R.id.movementDisplay);

        if (!hasMicrophone()) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
//            try {
//                mediaRecorder = new MediaRecorder();
//                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                mediaRecorder.setOutputFile(audioFilePath);
//                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                mediaRecorder.prepare();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else {
            playButton.setEnabled(false);
            stopButton.setEnabled(false);
        }

        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/myaudio.3gp";
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
                /*try {
                    recordAudio(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                promptSpeechInput();
//                stopButton.setEnabled(true);
//                try {
//                    mediaRecorder = new MediaRecorder();
//                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                    mediaRecorder.setOutputFile(audioFilePath);
//                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                    mediaRecorder.prepare();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                mediaRecorder.start();

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopAudio(v);
//                stopButton.setEnabled(false);
//                playButton.setEnabled(true);
//
//                if (isRecording) {
//                    recordButton.setEnabled(false);
//                    mediaRecorder.stop();
//                    mediaRecorder.release();
//                    mediaRecorder = null;
//                    isRecording = false;
//                } else {
//                    //mediaPlayer.release();
//                    mediaPlayer = null;
//                    recordButton.setEnabled(true);
//                }
//
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    playAudio(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                playButton.setEnabled(false);
//                recordButton.setEnabled(false);
//                stopButton.setEnabled(true);
//
//                try {
//                    mediaPlayer = new MediaPlayer();
//                    mediaPlayer.setDataSource(audioFilePath);
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
       });
    }
    @Override
    protected void onResume() {
        super.onResume();

        boolean isConnected = PebbleKit.isWatchConnected(this);
        Toast.makeText(this, "Pebble " + (isConnected ? "is" : "is not") + " connected!", Toast.LENGTH_LONG).show();

        boolean appMessageSupported = PebbleKit.areAppMessagesSupported(this);
        Toast.makeText(this, "AppMessage supported: " + (appMessageSupported ? "true" : "false"), Toast.LENGTH_LONG).show();

        if (pebbleReceiver == null) {
            pebbleReceiver = new PebbleKit.PebbleDataReceiver(SPORTS_UUID) {
                @Override
                public void receiveData(Context context, int id, PebbleDictionary data) {
                    // Always ACKnowledge the last message to prevent timeouts
                    PebbleKit.sendAckToPebble(getApplicationContext(), id);
                    final int AppKey = 0;
                    // Get action and display
                    int state = data.getInteger(AppKey).intValue();

                    System.out.println("////////////////////////////////////////////////"+state);
                    movementDisplay.setText(String.valueOf(state));//String.valueOf(state == Constants.SPORTS_STATE_PAUSED ? "Resumed!" : "Paused!"));

                    Toast.makeText(getApplicationContext(),
                            (state == Constants.SPORTS_STATE_PAUSED ? "Resumed!" : "Paused!"), Toast.LENGTH_SHORT).show();
                }

            };
        }
        PebbleKit.registerReceivedDataHandler(this, pebbleReceiver);
    }


    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }


    public void recordAudio (View view) throws IOException {
        isRecording = true;
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
        //speechRecognitionHelper.run(this);

    }


    public void stopAudio (View view) {
        stopButton.setEnabled(false);
        playButton.setEnabled(true);

        if (isRecording) {
            recordButton.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
        }
    }

    public void playAudio (View view) throws IOException {
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
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

        switch (requestCode) {
            case VOICE_RECOGNITION_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    int filler = 0;
                    int count = 0;
                    //for(String s: result){
                    String s = result.get(0);

                    tokenizer = new StringTokenizer(s, " ");
                    while(tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        count++;
                        if (token.equals("like") || token.equals("so") || token.equals("well") || token.equals("um")) {
                            filler++;
                        }
                    }
                    //}
                    System.out.println("//////////////////////////     NUM WORDS: " + count + "   ////////////" );
                    System.out.println("//////////////////////////////    " + filler+ "    /////////////////");
                    Toast.makeText(this, result.get(0), Toast.LENGTH_LONG).show();
                }
                break;
            }

        }
    }
}
