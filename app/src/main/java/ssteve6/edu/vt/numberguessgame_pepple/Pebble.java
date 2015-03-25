package ssteve6.edu.vt.numberguessgame_pepple;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by b on 3/25/2015.
 */
public class Pebble {
    Context context;
    // TODO: ADD APP UUID
    private final static UUID PEBBLE_APP_UUID = UUID.fromString("56bcf818-ed35-4f91-bc80-2197e4733e0c");
    private final int TIMEOUT = 20;
    int DATA_PAYLOAD = 1;
    int dataReceivedIndex = DATA_PAYLOAD;
    Button progressView;
    int prog = 100;
    int trial = 0;

    private class PebbleConnectionReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.i(MainActivity.TAG, "Pebble connected!");
        }
    }

    private class PebbleDisconnectionReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.i(MainActivity.TAG, "Pebble connected!");
        }
    }

    private class PebbleAckInfoReceiver extends PebbleKit.PebbleAckReceiver {


        protected PebbleAckInfoReceiver(UUID subscribedUuid) {
            super(subscribedUuid);
        }

        @Override
        public void receiveAck(Context context, int transactionId) {
            Log.i(MainActivity.TAG, "Received ack for transaction " + transactionId + " trial:" + trial);
            trial = 0;
            if (transactionId == dataReceivedIndex) {
                dataReceivedIndex++;
            }
        }
    }

    private class PebbleNackInfoReceiver extends PebbleKit.PebbleNackReceiver {

        protected PebbleNackInfoReceiver(UUID subscribedUuid) {
            super(subscribedUuid);
        }

        @Override
        public void receiveNack(Context context, int transactionId) {
            trial++;
            Log.i(MainActivity.TAG, "Received nack for transaction " + transactionId + " trial:" + trial);
        }
    }

    public void initConnection() {
        // TODO: REGISTER DIS/CONNECTED RECEIVER
        PebbleKit.registerPebbleConnectedReceiver(context, new PebbleConnectionReceiver());
        PebbleKit.registerPebbleDisconnectedReceiver(context, new PebbleDisconnectionReceiver());

        // TODO: REGISTER N/ACK HANDLERS
        PebbleKit.registerReceivedAckHandler(context, new PebbleAckInfoReceiver(PEBBLE_APP_UUID));
        PebbleKit.registerReceivedNackHandler(context, new PebbleNackInfoReceiver(PEBBLE_APP_UUID));
    }

    public Pebble(Context context, Button button) {
        this.context = context;
        this.progressView = button;
        initConnection();
    }

    public boolean testConnection() {
        boolean connected = PebbleKit.isWatchConnected(context);
        Log.i(MainActivity.TAG, "Pebble is " + (connected ? "connected" : "not connected"));
        return connected;
    }

    public void sendImage(Bitmap bitmap) {
        // TODO: Launching my app & create datafile
        PebbleKit.startAppOnPebble(context, PEBBLE_APP_UUID);
        byte[] datafile = null;

        if (datafile != null && prog == 100) {
            Log.d(MainActivity.TAG, "PicSize:" + datafile.length);
            // TODO: CREATE PEBBLE DICTIONARY
            PebbleDictionary pdata = new PebbleDictionary();

            // TODO: SEND PEBBLE DATA
            pdata.addBytes(DATA_PAYLOAD, datafile);

            PebbleKit.sendDataToPebbleWithTransactionId(context, PEBBLE_APP_UUID, pdata, DATA_PAYLOAD);
            new SendImageTask().execute(datafile);
        } else {

        }
    }

    private class SendImageTask extends AsyncTask<byte[], Integer, Void> {
        protected Void doInBackground(byte[]... data) {
            int pkgSize = 124;
            byte[] pngdata = data[0];
            int pkgNum = pngdata.length / pkgSize;
            Log.d(MainActivity.TAG, "pkg Nums:" + (pkgNum + 1));
            int i;
            for (i = 0; i < pkgNum; i++) {
                byte[] pck = Arrays.copyOfRange(pngdata, i * pkgSize, (i + 1) * pkgSize);
                sendPkg(pck, i + DATA_PAYLOAD);
                publishProgress(i * 100 / pkgNum);
            }
            byte[] pck = Arrays.copyOfRange(pngdata, i * pkgSize, pngdata.length);
            sendPkg(pck, DATA_PAYLOAD + i);
            publishProgress(100);
            return null;
        }

        private void sendPkg(byte[] dataPkg, int id) {
            while (dataReceivedIndex != id + 1 && trial < TIMEOUT) {
                try {
                    PebbleDictionary pdata = new PebbleDictionary();
                    pdata.addBytes(id, dataPkg);
                    PebbleKit.sendDataToPebbleWithTransactionId(context, PEBBLE_APP_UUID, pdata, id);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (trial >= TIMEOUT) {
                publishProgress(200);
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            prog = progress[0];
            progressView.setText(progress[0] + "%");
            if (prog < 100) {
                progressView.setClickable(false);
            } else {
                if (prog == 200) {
                    progressView.setText("Connection Error!");
                    progressView.setClickable(true);
                    prog = 100;
                    this.cancel(true);
                } else if (prog == 100) {
                    progressView.setClickable(true);
                    //progressView.setText(R.string.button_sendtopebble);
                }
            }
        }

        protected void onPostExecute() {
            Log.d(MainActivity.TAG, "Number Sent!");
        }
    }
}
