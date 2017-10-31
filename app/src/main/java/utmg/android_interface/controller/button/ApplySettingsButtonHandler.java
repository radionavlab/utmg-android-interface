package utmg.android_interface.controller.button;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class ApplySettingsButtonHandler implements View.OnClickListener {

    private final SharedPreferences.Editor sharedPreferenceEditor;
    private final EditText arenaWidthSettingsBox, arenaLengthSettingsBox, maxAltitudeSettingsBox, numQuadsSettingsBox, hostnameSettingsBox, portSettingsBox;
    private final Context context;
    private final Activity activity;

    public ApplySettingsButtonHandler(
            final Activity activity,
            final SharedPreferences.Editor sharedPreferenceEditor,
            final EditText numQuadsSettingsBox,
            final EditText arenaWidthSettingsBox,
            final EditText arenaLengthSettingsBox,
            final EditText maxAltitudeSettingsBox,
            final EditText hostnameSettingsBox,
            final EditText portSettingsBox,
            final Context context) {
        this.activity = activity;
        this.sharedPreferenceEditor = sharedPreferenceEditor;
        this.numQuadsSettingsBox = numQuadsSettingsBox;
        this.arenaWidthSettingsBox = arenaWidthSettingsBox;
        this.arenaLengthSettingsBox = arenaLengthSettingsBox;
        this.maxAltitudeSettingsBox = maxAltitudeSettingsBox;
        this.hostnameSettingsBox = hostnameSettingsBox;
        this.portSettingsBox = portSettingsBox;
        this.context = context;
    }

    @Override
    public void onClick(
            final View view) {
        final int numQuads = Integer.valueOf(numQuadsSettingsBox.getText().toString());
        final float arenaWidth = Float.valueOf(arenaWidthSettingsBox.getText().toString());
        final float arenaLength = Float.valueOf(arenaLengthSettingsBox.getText().toString());
        final float maxAltitude = Float.valueOf(maxAltitudeSettingsBox.getText().toString());
        final String hostname = hostnameSettingsBox.getText().toString();
        final int port = Integer.valueOf(portSettingsBox.getText().toString());

        sharedPreferenceEditor.putInt("numQuads", numQuads);
        sharedPreferenceEditor.putFloat("arenaWidthMeters", arenaWidth);
        sharedPreferenceEditor.putFloat("arenaLengthMeters", arenaLength);
        sharedPreferenceEditor.putFloat("maxAltitude", maxAltitude);
        sharedPreferenceEditor.putString("hostname", hostname);
        sharedPreferenceEditor.putInt("port", port);
        sharedPreferenceEditor.commit();

        Toast.makeText(this.context, "Applied settings!", Toast.LENGTH_SHORT).show();
        activity.finish();
    }
}
