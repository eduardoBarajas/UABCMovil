package com.uabcmovil.uabcmovil.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uabcmovil.uabcmovil.Fragments.SolicitudesAceptadasFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }

    @Override
    public int getItemPosition(Object obj){
        // POSITION_NONE hace que se pueda recargar el pagerAdapter
        return POSITION_NONE;
        //return super.getItemPosition(obj);
    }
}
