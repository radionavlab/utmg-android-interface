package utmg.android_interface.controller.button;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import utmg.android_interface.model.util.Trajectory;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class SendAllTrajectoriesButtonHandler implements View.OnClickListener {

    private final List<Trajectory> trajectories;
    private final Context context;
    private static final ObjectMapper mapper = new ObjectMapper();

    public SendAllTrajectoriesButtonHandler(
            final List<Trajectory> trajectories,
            final Context context) {
        this.trajectories = trajectories;
        this.context = context;
    }

    @Override
    public void onClick(
            final View view) {

        new Thread(() -> {
            try {
                // TODO: This is hardcoded. Change it in config file.
                final Socket socket = new Socket("192.168.1.3", 8080);
                PrintWriter socketWriter = new PrintWriter(socket.getOutputStream());

                for (Trajectory trajectory : trajectories) {
                    final String json = mapper.writeValueAsString(trajectory);
                    socketWriter.println(json);
                    socketWriter.flush();
                    Thread.sleep(1);
                }

                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ).start();

        // Display a toast at the bottom of the screen
        Toast.makeText(context, "Sent all trajectories.", Toast.LENGTH_SHORT).show();


    }
}
