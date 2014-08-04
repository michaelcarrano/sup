package com.michaelcarrano.sup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AuthenticationFragmentAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 4;

    public AuthenticationFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        AuthenticationFragment fragment = new AuthenticationFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(AuthenticationFragment.ARG_PAGE_POSITION, position);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
