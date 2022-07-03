package com.example.recording.Interfaces;

import com.example.recording.Models.RecordingItem;

public interface OnDatabaseChangeListener {

    void onNewDatabaseEntryAdd(RecordingItem recordingItem);
}
