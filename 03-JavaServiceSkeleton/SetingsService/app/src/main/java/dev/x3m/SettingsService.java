package dev.x3m;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsService extends Service {
    public SharedPreferences preferences;
    int brightness = 0;
    String SVC = "GL-LAB-SVC";

    SettingsInterface.Stub svc = new SettingsInterface.Stub(){

        @Override
        public void setBrightness(int level) throws RemoteException {
            Log.e(SVC,"client sets brightness: "+ level);
            brightness = level;
        }

        @Override
        public int getBrightness() throws RemoteException {
            Log.e(SVC,"client gets brightness: "+brightness);
            return brightness;
        }
    };

    public SettingsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return svc;
    }

    @Override
    public void onCreate() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        brightness = preferences.getInt("brightness", 0);
        Log.e(SVC,"restored brightness "+brightness);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e(SVC, "bye. storing brightness "+brightness);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt("brightness", brightness);
        edit.apply();
        super.onDestroy();
    }
}
