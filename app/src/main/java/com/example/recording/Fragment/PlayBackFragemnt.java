package com.example.recording.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.recording.Models.RecordingItem;
import com.example.recording.R;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayBackFragemnt extends DialogFragment {

    @BindView(com.example.recording.R.id.fileNameTXT)
    TextView fileNameTXT;
    @BindView(com.example.recording.R.id.file_length_txt)
    TextView lengthTXT;
    @BindView(com.example.recording.R.id.current_progess)
    TextView current_progess;
    @BindView(com.example.recording.R.id.seekBar)
    SeekBar seekBar;
    @BindView(com.example.recording.R.id.fab_play)
    FloatingActionButton fab_play;
    private RecordingItem recordingItem;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    long minutes = 0;
    long sencond = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordingItem = (RecordingItem) getArguments().getSerializable("item");
        minutes = TimeUnit.MILLISECONDS.toMinutes(recordingItem.getLength());
        sencond = TimeUnit.MILLISECONDS.toMinutes(recordingItem.getLength()) - TimeUnit.MINUTES.toSeconds(minutes);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(com.example.recording.R.layout.fragment_playback, null);
        ButterKnife.bind(this, view);
        setSeekBar();

        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onPlayy(isPlaying);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlaying = !isPlaying;
            }
        });

        fileNameTXT.setText(recordingItem.getName());
        lengthTXT.setText(String.format("%02d:%02d", minutes, sencond));
        builder.setView(view);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return builder.create();
    }

    private void onPlayy(boolean isPlaying) throws IOException {
        if (!isPlaying) {
            if (mediaPlayer == null) {
                startPlaying();
            }
        } else {
            pausePlaying();
        }
    }

    private void pausePlaying() {
        fab_play.setImageResource(R.drawable.ic_white_play);
        handler.removeCallbacks(mRunable);
        mediaPlayer.pause();
    }

    private void startPlaying() throws IOException {
        fab_play.setImageResource(R.drawable.ic_white_pause);
        mediaPlayer= new MediaPlayer();
        mediaPlayer.setDataSource(recordingItem.getPath());
        mediaPlayer.prepare();
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlaying();
            }
        });

        updateSeekBar();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



    }

    private void setSeekBar() {
        ColorFilter colorFilter = new LightingColorFilter(getResources().getColor(com.example.recording.R.color.purple_500),
                getResources().getColor(com.example.recording.R.color.purple_200));
        seekBar.getProgressDrawable().setColorFilter(colorFilter);
        seekBar.getThumb().setColorFilter(colorFilter);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(i);
                    handler.removeCallbacks(mRunable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
                    long second = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    current_progess.setText(String.format("02d:%02d", minutes, second));
                    updateSeekBar();
                } else if (mediaPlayer == null && b) {
                    try {
                        prepareMeidaPlayerFromPoint(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateSeekBar();

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void prepareMeidaPlayerFromPoint(int i) throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(recordingItem.getPath());
        mediaPlayer.prepare();
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlaying();
            }
        });
    }

    private void stopPlaying() {
        fab_play.setImageResource(R.drawable.ic_white_play);
        handler.removeCallbacks(mRunable);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        seekBar.setProgress(seekBar.getMax());
        isPlaying = !isPlaying;
        current_progess.setText(lengthTXT.getText());
        seekBar.setProgress(seekBar.getMax());
    }

    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                int mCurrenPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrenPosition);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrenPosition);
                long second = TimeUnit.MILLISECONDS.toSeconds(mCurrenPosition)
                        - TimeUnit.MINUTES.toSeconds(minutes);
                current_progess.setText(String.format("02d:%02d", minutes, second));
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        handler.postDelayed(mRunable, 1000);
    }
}
