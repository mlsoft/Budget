package net.ddns.mlsoftlaberge.budget.trycorder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import net.ddns.mlsoftlaberge.budget.R;

/**
 * Created by mlsoft on 03/04/16.
 */
public class BeepActivity extends AppCompatActivity {

    private Button mBeepButton;
    private Button mPlayButton;
    private Button mStopButton;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // default layout
        setContentView(R.layout.beep_activity);

        // big cam
        mBeepButton = (Button) findViewById(R.id.smallbeep_button);
        mBeepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beep();
            }
        });

        // big cam
        mPlayButton = (Button) findViewById(R.id.musicplay_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playmusic();
            }
        });

        // big cam
        mStopButton = (Button) findViewById(R.id.musicstop_button);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopmusic();
            }
        });

    }

    private void beep() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.beep);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }


    private void playmusic() {
        if(mMediaPlayer==null) {
            mMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.music);
            mMediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    private void stopmusic() {
        if(mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }



}
