package utmg.android_interface.controller.button;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class ApplySettingsButtonHandler implements View.OnClickListener {

    private final SharedPreferences.Editor sharedPreferenceEditor;
    private final EditText arenaWidthSettingsBox, arenaLengthSettingsBox, maxAltitudeSettingsBox, numQuadsSettingsBox;

    public ApplySettingsButtonHandler(
            SharedPreferences.Editor sharedPreferenceEditor,
            EditText numQuadsSettingsBox,
            EditText arenaWidthSettingsBox,
            EditText arenaLengthSettingsBox,
            EditText maxAltitudeSettingsBox) {
        this.sharedPreferenceEditor = sharedPreferenceEditor;
        this.numQuadsSettingsBox = numQuadsSettingsBox;
        this.arenaWidthSettingsBox = arenaWidthSettingsBox;
        this.arenaLengthSettingsBox = arenaLengthSettingsBox;
        this.maxAltitudeSettingsBox = maxAltitudeSettingsBox;
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

        Snackbar.make(view, "Applied new settings!", Snackbar.LENGTH_LONG).show();
    }
}
