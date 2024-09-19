package wynn.automation.com;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;

import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    AudioManager audioManager;

    Switch alarm;
    String[] esiTerrs = {
            "Llevigar Entrance",
            "Volcano Lower",
            "Bloody Beach",
            "Avos Temple",
            "Avos Workshop",
            "Corkus Outskirts",
            "Corkus Countryside",
            "Corkus Mountain",
            "Corkus Docks",
            "Corkus Statue",
            "Corkus Castle",
            "Corkus City",
            "Corkus Forest North",
            "Corkus City Mine",
            "Road To Mine",
            "Corkus City South",
            "Corkus Forest South",
            "Factory Entrance",
            "Fallen Factory",
            "Ruined Houses",
            "Corkus Sea Port",
            "Lighthouse Plateau",
            "Phinas Farm",
            "Southern Outpost",
            "Legendary Island",
            "Royal Gate"
    };
    Boolean allGood = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ISON", "program is on");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarm = (Switch) findViewById(R.id.switch1);
    }

    private Runnable alarmRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("ISON", "Switch is on");
            tryAlarm();
            handler.postDelayed(this, 120000); //120000 = 2 min
        }
    };

    public void onClicked (View view) {

        boolean isOn = alarm.isChecked();

        if (isOn) {
            Log.i("ISON", "got clicked");
            handler.postDelayed(alarmRunnable, 0);
        } else {
            handler.removeCallbacks(alarmRunnable);
        }

    }

    private void tryAlarm () {
        if (terrChecker()) {
            Log.i("ISON", "All is well, no alarm");
        } else {
            Log.i("ISON", "ALARM");

            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

            // Get the ringer maximum volume
            int max_volume_level = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

            // Set the ringer volume
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max_volume_level-5, AudioManager.FLAG_SHOW_UI);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            MediaPlayer player = MediaPlayer.create(this, notification);
            player.setLooping(true);
            player.start();
        }

    }

    private Boolean terrChecker () {
        Log.i("ISON", "checking terrs");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api-legacy.wynncraft.com/public_api.php?action=territoryList";

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject allTerrs = response.getJSONObject("territories");

                        for (String terr : esiTerrs) {
                            JSONObject curTerr = allTerrs.getJSONObject(""+terr);

                            if (!(curTerr.get("guild").equals("Empire of Sindria"))) {
                                allGood = false;
                            }

                        }

                    } catch (JSONException e) {
                        Log.i("ISON", "CONVERSION ERROR");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("ISON", "API ERROR");
                }
            }
        );

        queue.add(objectRequest);

        return allGood;
    }
}

// how to make program remember that its on?
// how to make program run without closing?