package carlhacks16.hearmeout;

import android.app.*;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

/**
 * Created by gimminju on 4/9/16.
 */
public class ShowPopUp extends Activity {

    PopupWindow popUp;
    LinearLayout layout;
    TextView tv;
    LayoutParams params;
    LinearLayout mainLayout;
    Button speedBut;
    Button fillerBut;
    Button volumeBut;
    Button movementBut;
    boolean click = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popUp = new PopupWindow(this);
        layout = new LinearLayout(this);
        mainLayout = new LinearLayout(this);
        tv = new TextView(this);
        speedBut = (Button) findViewById(R.id.button);
        fillerBut = (Button) findViewById(R.id.button8);
        volumeBut = (Button) findViewById(R.id.button6);
        movementBut = (Button) findViewById(R.id.button7);

        //Speed
        speedBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click) {
                    popUp.showAtLocation(mainLayout, Gravity.BOTTOM, 10, 10);
                    popUp.update(50, 50, 300, 80);
                    click = false;
                } else {
                    popUp.dismiss();
                    click = true;
                }
            }
        });
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        tv.setText("message");
        layout.addView(layout);
        mainLayout.addView(speedBut, params);
        setContentView(mainLayout);

        //Filler
        fillerBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click) {
                    popUp.showAtLocation(mainLayout, Gravity.BOTTOM, 10, 10);
                    popUp.update(50, 50, 300, 80);
                    click = false;
                } else {
                    popUp.dismiss();
                    click = true;
                }
            }
        });
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        tv.setText("message");
        layout.addView(layout);
        mainLayout.addView(speedBut, params);
        setContentView(mainLayout);

        //Volume
        volumeBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click) {
                    popUp.showAtLocation(mainLayout, Gravity.BOTTOM, 10, 10);
                    popUp.update(50, 50, 300, 80);
                    click = false;
                } else {
                    popUp.dismiss();
                    click = true;
                }
            }
        });
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        tv.setText("message");
        layout.addView(layout);
        mainLayout.addView(speedBut, params);
        setContentView(mainLayout);

        //Movement
        movementBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click) {
                    popUp.showAtLocation(mainLayout, Gravity.BOTTOM, 10, 10);
                    popUp.update(50, 50, 300, 80);
                    click = false;
                } else {
                    popUp.dismiss();
                    click = true;
                }
            }
        });
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        tv.setText("message");
        layout.addView(layout);
        mainLayout.addView(speedBut, params);
        setContentView(mainLayout);
    }
}
