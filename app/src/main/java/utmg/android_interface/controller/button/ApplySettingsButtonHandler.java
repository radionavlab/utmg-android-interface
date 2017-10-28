package utmg.android_interface.controller.button;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class ApplySettingsButtonHandler implements View.OnClickListener {

    private final SharedPreferences.Editor sharedPreferenceEditor;
    private final EditText arenaWidthSettingsBox, arenaLengthSettingsBox, maxAltitudeSettingsBox, numQuadsSettingsBox;
    private final Context context;

    public ApplySettingsButtonHandler(
            final SharedPreferences.Editor sharedPreferenceEditor,
            final EditText numQuadsSettingsBox,
            final EditText arenaWidthSettingsBox,
            final EditText arenaLengthSettingsBox,
            final EditText maxAltitudeSettingsBox,
            final Context context) {
        this.sharedPreferenceEditor = sharedPreferenceEditor;
        this.numQuadsSettingsBox = numQuadsSettingsBox;
        this.arenaWidthSettingsBox = arenaWidthSettingsBox;
        this.arenaLengthSettingsBox = arenaLengthSettingsBox;
        this.maxAltitudeSettingsBox = maxAltitudeSettingsBox;
        this.context = context;
    }

    @Override
    public void onClick(
            final View view) {
        final float numQuads = Float.valueOf(numQuadsSettingsBox.getText().toString());
        final float arenaWidth = Float.valueOf(arenaWidthSettingsBox.getText().toString());
        final float arenaLength = Float.valueOf(arenaLengthSettingsBox.getText().toString());
        final float maxAltitude = Float.valueOf(maxAltitudeSettingsBox.getText().toString());

        sharedPreferenceEditor.putFloat("numQuads", numQuads);
        sharedPreferenceEditor.putFloat("arenaWidthMeters", arenaWidth);
        sharedPreferenceEditor.putFloat("arenaLengthMeters", arenaLength);
        sharedPreferenceEditor.putFloat("maxAltitude", maxAltitude);
        sharedPreferenceEditor.commit();

        Toast.makeText(this.context, "Applied settings!.", Toast.LENGTH_SHORT).show();
    }
}
