package utmg.android_interface.controller.button;

import android.support.design.widget.Snackbar;
import android.view.View;

import utmg.android_interface.model.util.Trajectory;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

// TODO: Implement this class
public class SendTrajectoryButtonHandler implements View.OnClickListener {

    public SendTrajectoryButtonHandler(
            final Trajectory trajectory) {
    }

    @Override
    public void onClick(
            final View view) {

        // Display a snackbar at the bottom of the screen
        Snackbar.make(view, "Sending trajectory...", Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }
}
