package carlhacks16.hearmeout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import carlhacks16.hearmeout.database.DatabaseHelper;
import carlhacks16.hearmeout.database.SessionContract;
import carlhacks16.hearmeout.models.Session;

public class StartSpeechActivity extends Activity {



    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    public static final int SAMPLE_RATE = 16000;
    private Chronometer mydChronometer;
    private PebbleKit.PebbleDataReceiver pebbleReceiver;
    private AudioRecord mRecorder;
    private File mRecording;
    private short[] mBuffer;
    protected TextView movementDisplay;
    private final String startRecordingLabel = "Start";
    private final String stopRecordingLabel = "Stop";
    private boolean mIsRecording = false;
    private ProgressBar mProgressBar;
    private static final UUID SPORTS_UUID = UUID.fromString("4403bc13-03db-450f-bdb7-95e3739089b0");


    private Button rbutton;
    private List<Integer> ampArray;
    private int counter;
    private int total;
    //private int averagescore;
    protected DatabaseHelper mDbHelper;
    public int averageAmplitude;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_speech);
        rbutton=(Button)findViewById(R.id.button10);
        movementDisplay = (TextView) findViewById(R.id.movementDisplay);
        mDbHelper = new DatabaseHelper(this);
        averageAmplitude=0;
        ampArray=new ArrayList<Integer>();
        counter=0;
        total=0;
        //averagescore=0;


        rbutton.setVisibility(View.INVISIBLE);

        initRecorder();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mydChronometer=(Chronometer) findViewById(R.id.chronometer1);

        final Button button = (Button) findViewById(R.id.button);
        button.setText(startRecordingLabel);



        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(final View v) {
                                          PebbleKit.startAppOnPebble(v.getContext(), SPORTS_UUID);
                                          if (!mIsRecording) {
                                              button.setText(stopRecordingLabel);
                                              mIsRecording = true;
                                              mRecorder.startRecording();
                                              mRecording = getFile("raw");
                                              startBufferedWrite(mRecording);
                                              mydChronometer.setBase(SystemClock.elapsedRealtime());
                                              mydChronometer.start();

                                          } else {
                                              PebbleKit.closeAppOnPebble(v.getContext(), SPORTS_UUID);
                                              button.setText(startRecordingLabel);
                                              mIsRecording = false;
                                              mRecorder.stop();
                                              File waveFile = getFile("wav");
                                              try {
                                                  rawToWave(mRecording, waveFile);
                                              } catch (IOException e) {
                                                  Toast.makeText(StartSpeechActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                              }
                                              mydChronometer.stop();
                                              if (rbutton.getVisibility() == View.INVISIBLE) {
                                                  rbutton.setVisibility(View.VISIBLE);
                                              }

                                              Toast.makeText(StartSpeechActivity.this, "Recorded to " + waveFile.getName(),
                                                      Toast.LENGTH_SHORT).show();


                                          }
                                      }
                                  }


        );
        rbutton.setOnClickListener(new View.OnClickListener()
                                   {
                                       @Override
                                       public void onClick(final View v) {
                                           Intent i=new Intent(StartSpeechActivity.this, Result2.class);
                                           i.putExtra("volumescore", mDbHelper.getLatestSession().getVolume());
                                           startActivity(i);




                                       }


                                   }
        );
    }

    @Override
    protected void onResume(){
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
    @Override
    public void onDestroy() {
        mRecorder.release();
        super.onDestroy();
    }

    private void initRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    }

    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                    while (mIsRecording) {

                        double sum = 0;
                        int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);

                            sum += mBuffer[i] * mBuffer[i];
                        }
                        if (readSize > 0) {
                            final double amplitude = sum / readSize;
                            counter=counter+1;
                            total=total+(int)(Math.sqrt(amplitude / 2));
                            mProgressBar.setProgress((int) Math.sqrt(amplitude / 2));


                        }
                        // to do: a way to calculate the average and then return a score -> amplitude maximum: 4000



                    }
                    calculateAverage(total, counter);

                } catch (IOException e) {
                    Toast.makeText(StartSpeechActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    mProgressBar.setProgress(0);
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            Toast.makeText(StartSpeechActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Toast.makeText(StartSpeechActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                }
            }
        }).start();
    }

//    public int getScore(){
//        return averagescore;
//    }


    private void calculateAverage(int int1, int int2){
        averageAmplitude=int1/int2;
        //Log.v("*******","+"+averageAmplitude);
        int averagescore=(int)(10-Math.abs((1000-averageAmplitude)/100));
        //Log.v("*******","+"+averagescore);

        Session session = new Session();
        mDbHelper.createSession(session);
        mDbHelper.updateSession(averagescore, SessionContract.Session.VOLUME);

    }

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, SAMPLE_RATE); // sample rate
            writeInt(output, SAMPLE_RATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {

                bytes.putShort(s);
            }
            output.write(bytes.array());
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private File getFile(final String suffix) {
        Time time = new Time();
        time.setToNow();
        return new File(Environment.getExternalStorageDirectory(), time.format("%Y%m%d%H%M%S") + "." + suffix);
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

}