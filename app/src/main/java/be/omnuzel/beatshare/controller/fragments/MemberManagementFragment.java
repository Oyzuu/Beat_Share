package be.omnuzel.beatshare.controller.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.omnuzel.beatshare.R;

public class MemberManagementFragment extends Fragment {

    public interface MemberManagementListener {
        void save(View view);

        void cancel(View view);
    }

    public static MemberManagementFragment newInstance() {

        Bundle args = new Bundle();
        
        MemberManagementFragment fragment = new MemberManagementFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_member_management, container, false);
    }
}
