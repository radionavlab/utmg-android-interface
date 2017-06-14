package utmg.android_interface;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        setupActionBar();

        final SeekBar quad1Seek = (SeekBar) findViewById(R.id.quad1Seek);
        final SeekBar quad2Seek = (SeekBar) findViewById(R.id.quad2Seek);
        final SeekBar quad3Seek = (SeekBar) findViewById(R.id.quad3Seek);
        final SeekBar quadAllSeek = (SeekBar) findViewById(R.id.quadAllSeek);


//        slider.getLayoutParams().width = (int)(screenHeight * 0.65);
        final Handler seekbarH = new Handler();
        Runnable seekbarR = new Runnable() {
            @Override
            public void run() {

                quadAllSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        quad1Seek.setProgress(progress);
                        quad2Seek.setProgress(progress);
                        quad3Seek.setProgress(progress);
                    }
                });
                seekbarH.postDelayed(this, 1000);
            }
        };
        seekbarR.run();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
