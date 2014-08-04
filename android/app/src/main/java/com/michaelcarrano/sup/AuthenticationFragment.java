package com.michaelcarrano.sup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AuthenticationFragment extends Fragment {

    public static final String ARG_PAGE_POSITION = "ARG_PAGE_POSITION";

    private int mPagePosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_PAGE_POSITION)) {
            mPagePosition = getArguments().getInt(ARG_PAGE_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_authentication, container, false);
        String[] greetings = getResources().getStringArray(R.array.viewpager);
        TextView logo = (TextView) rootView.findViewById(R.id.logo);
        logo.setText(greetings[mPagePosition]);
        return rootView;
    }
}
