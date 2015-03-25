package ssteve6.edu.vt.numberguessgame_pepple;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.Random;
import java.util.UUID;


public class MainActivity extends ActionBarActivity  {

    public static final String TAG = "PebbleProject";

    private Button higherButton;
    private Button lowerButton;
    private Button guessNumber;
    private TextView theGuessView;
    private TextView numberView;

    private final static UUID PEBBLE_APP_UUID = UUID.fromString("56bcf818-ed35-4f91-bc80-2197e4733e0c");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        higherButton = (Button) findViewById(R.id.higherButton);
        lowerButton = (Button) findViewById(R.id.lowerButton);
        guessNumber = (Button) findViewById(R.id.guessButton);
        theGuessView = (TextView) findViewById(R.id.theGuessView);
        numberView = (TextView) findViewById(R.id.numberView);

        boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
        Log.i(getLocalClassName(), "Pebble is" + (connected ? "connected" : "not connected"));

        Random rand = new Random();
        final int number = rand.nextInt(100) + 1;

        numberView.setText(Integer.toString(number));

        guessNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PebbleDictionary data = new PebbleDictionary();
                data.addUint8(0, (byte) number);
                PebbleKit.sendDataToPebble(getApplicationContext(), PEBBLE_APP_UUID, data);

                guessNumber.setText("Correct!!");
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
