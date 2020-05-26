package lab.gl.svc_read_gui_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dev.x3m.SettingsInterface;

public class MainActivity extends AppCompatActivity {

    TextView lblBrighness;
    ServiceConnection conn;
    SettingsInterface settingsService;
    String GUI = "SVC-READ-GUI-APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblBrighness = (TextView) findViewById(R.id.lblBrightnessValue);
        Button btn = (Button)findViewById(R.id.btnRefresh);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingsService== null){
                    Log.e(GUI, "bail out");
                    return;
                }

                try {
                    int brightness = settingsService.getBrightness();
                    lblBrighness.setText(""+brightness);

                    Log.e(GUI, "got "+brightness);

                } catch (RemoteException e) {
                    Log.e(GUI, "exception: "+ e.getMessage() );
                    e.printStackTrace();
                }

            }
        });

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                settingsService = SettingsInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                settingsService = null;
            }
        };


        Intent intent = new Intent();
        intent.setClassName("lab.gl.svc_pkg", "dev.x3m.SettingsService");
        try {
            bindService(intent, conn, BIND_AUTO_CREATE);
        } catch(SecurityException e){
            Log.e(GUI, "oops: "+ e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(conn!=null) {
            unbindService(conn);
        }
    }
}
