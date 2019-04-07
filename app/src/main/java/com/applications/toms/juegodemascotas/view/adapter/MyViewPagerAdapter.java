package com.applications.toms.juegodemascotas.view.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> titulos;

    public MyViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titulos) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titulos = titulos;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //Metodo que usa el tabLayout para pedir el titulo de las pages
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return titulos.get(position);

       /*
       switch (position){
            case 0:
                return "Rojo";
            case 1:
                return "Verde";
            case 2:
                return "Azul";
        }

        return "Nada";
        */
    }
}
