package utmg.android_interface.controller.button;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import utmg.android_interface.model.util.Trajectory;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

// TODO: Comments!
public class SendAllTrajectoriesButtonHandler implements View.OnClickListener {

    private final List<Trajectory> trajectories;

    public SendAllTrajectoriesButtonHandler(
            final List<Trajectory> trajectories) {
        this.trajectories = trajectories;
    }

    @Override
    public void onClick(
            final View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Socket socket = new Socket("localhost", 8080);
                    PrintWriter socketWriter = new PrintWriter(socket.getOutputStream());

                    for (Trajectory trajectory : trajectories) {
                        socketWriter.println(trajectory.toString());
                        socketWriter.flush();
                        Thread.sleep(1);
                    }

                    socket.close();

                    // Display a snackbar at the bottom of the screen
                    Snackbar.make(view, "Sent all trajectories!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();


    }
}
