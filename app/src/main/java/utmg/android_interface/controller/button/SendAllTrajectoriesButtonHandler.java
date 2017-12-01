package utmg.android_interface.controller.button;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final String hostname;
    private final int port;

    public SendAllTrajectoriesButtonHandler(
            final List<Trajectory> trajectories,
            final Context context,
            final String hostname,
            final int port) {
        this.trajectories = trajectories;
        this.context = context;
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public void onClick(
            final View view) {

        new Thread(() -> {
            try {
                final Socket socket = new Socket(this.hostname, this.port);
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
