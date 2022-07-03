package com.example.recording.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recording.R;
import com.example.recording.Services.RecordService;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordFragment extends Fragment {
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.recording_status)
    TextView recording_status;
    @BindView(R.id.btnPause)
    Button btnPause;
    @BindView(R.id.btnRecord)
    FloatingActionButton btnRecord;
    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;
    long timeWhenPause = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPause.setVisibility(View.GONE);
        btnRecord.setColorPressed(getResources().getColor(R.color.purple_200));
    }

    @OnClick(R.id.btnRecord)
    public void record() {
        onReCord(mStartRecording);
        mStartRecording = !mStartRecording;
    }

    private void onReCord(boolean start) {
        Intent intent = new Intent(getActivity(), RecordService.class);
        if (start) {
            btnRecord.setImageResource(R.drawable.ic_white_stop);
            Toast.makeText(getContext(), "Recording Start", Toast.LENGTH_LONG).show();

            File folder = new File(Environment.getExternalStorageDirectory() + "/MyShowRecord");
            if (!folder.exists()) {
                folder.mkdir();
            }

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
                getActivity().startService(new Intent(getActivity(), RecordService.class));
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            recording_status.setText("Recording...");
        } else {
            btnRecord.setImageResource(R.drawable.ic_white_mic);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPause = 0;
            recording_status.setText("Tap the button to start recording");
            getActivity().stopService(intent);

        }
    }
}
