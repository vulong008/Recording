package com.example.recording.Adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.recording.Fragment.FileViewerFragment;
import com.example.recording.Fragment.RecordFragment;

public class MyTabAdapter extends FragmentStatePagerAdapter {

    public MyTabAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new RecordFragment();

            case 1:
                return new FileViewerFragment();
        }
        return new RecordFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title="";
     switch (position)
     {
         case 0:
             title="record";
             break;
         case 1: title="saved";
         break;

     }
return title;
    }
}
