package com.example.recording.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recording.Database.DBHelper;
import com.example.recording.Fragment.PlayBackFragemnt;
import com.example.recording.Interfaces.OnDatabaseChangeListener;
import com.example.recording.Models.RecordingItem;
import com.example.recording.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileViewerFragmentAdapter extends RecyclerView.Adapter<FileViewerFragmentAdapter.FileViewerHolder>
implements OnDatabaseChangeListener {
    Context context;
    ArrayList<RecordingItem> recordingItemArrayList;
    LinearLayoutManager linearLayoutManager;
    DBHelper dbHelper;

    public FileViewerFragmentAdapter(Context context, ArrayList<RecordingItem> arrayList
            , LinearLayoutManager linearLayoutManager) {
        this.context = context;
        this.recordingItemArrayList = arrayList;
        this.linearLayoutManager = linearLayoutManager;
        this.dbHelper=new DBHelper(context);
        dbHelper.setOnDatabaseChangeListener(this);
    }

    @NonNull
    @Override
    public FileViewerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view, parent, false);
        return new FileViewerHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FileViewerHolder holder, int position) {
        RecordingItem recordingItem = recordingItemArrayList.get(position);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(recordingItem.getLength());
        long second=TimeUnit.MILLISECONDS.toMinutes(recordingItem.getLength())-TimeUnit.MINUTES.toSeconds(minutes);
        holder.name.setText(recordingItem.getName());
        holder.length.setText(String.format("%02d:%02d",minutes,second));
        holder.time.setText(DateUtils.formatDateTime(context,recordingItem.getTime_added(),
                DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_NUMERIC_DATE|
                        DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_SHOW_YEAR));
    }

    @Override
    public int getItemCount() {
        return recordingItemArrayList.size();
    }

    @Override
    public void onNewDatabaseEntryAdd(RecordingItem recordingItem) {
        recordingItemArrayList.add(recordingItem);
        notifyItemInserted(recordingItemArrayList.size()-1);
    }

    public class FileViewerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textFileName)
        TextView name;
        @BindView(R.id.textFileLength)
        TextView length;
        @BindView(R.id.textFileTime)
        TextView time;
        @BindView(R.id.cardView)
        View image;

        public FileViewerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayBackFragemnt playBackFragemnt=new PlayBackFragemnt();
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("item",recordingItemArrayList.get(getAdapterPosition()));
                    playBackFragemnt.setArguments(bundle);
                    FragmentTransaction fragmentTransaction=((FragmentActivity)context)
                            .getSupportFragmentManager().beginTransaction();
                    playBackFragemnt.show(fragmentTransaction,"dialog playback");
                }
            });

        }
    }
}
