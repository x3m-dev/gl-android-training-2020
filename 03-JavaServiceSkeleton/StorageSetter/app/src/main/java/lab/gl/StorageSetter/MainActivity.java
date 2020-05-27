package lab.gl.StorageSetter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import dev.x3m.SettingsInterface;

public class MainActivity extends AppCompatActivity {

    String TAG = "SETTER GUI APP";
    SettingsInterface settings;
    EditText edBrightness;
    TextView lblStatus;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            settings = SettingsInterface.Stub.asInterface(service);
            try {
                edBrightness.setText("" + settings.getBrightness());
            } catch (RemoteException | NullPointerException e) {
                e.printStackTrace();
                err(e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            settings = null;
        }
    };

    void ok() {
        lblStatus.setText("OK");
    }

    void err(Exception e) {
        Log.e(TAG, "exce: " + e.getMessage());
        lblStatus.setText("exception : " + e.getMessage());

    }

    void err(String err) {
        Log.e(TAG, err);
        lblStatus.setText(err);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.btnSetNewBrightness);
        Button btnRefresh = findViewById(R.id.btnRefresh);

        edBrightness = findViewById(R.id.edBrightness);
        lblStatus = findViewById(R.id.lblStatus);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newValue = edBrightness.getText().toString();
                try {
                    int newBrightness = Integer.parseInt(newValue);
                    settings.setBrightness(newBrightness);
                    ok();

                } catch (NumberFormatException | RemoteException | NullPointerException e) {
                    err("set new value failed " + newValue + " - " + e.getMessage());
                }
            }
        });


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    edBrightness.setText("" + settings.getBrightness());
                    ok();
                } catch (RemoteException | NullPointerException e) {
                    err(e);
                }
            }
        });


        Intent intent = new Intent();
        intent.setClassName("lab.gl.svc_pkg", "dev.x3m.SettingsService");
        try {
            bindService(intent, connection, BIND_AUTO_CREATE);
        } catch (SecurityException e) {
            Log.e(TAG, "oops: " + e.getMessage());
            err(e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
