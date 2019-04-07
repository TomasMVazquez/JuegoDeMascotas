package com.applications.toms.juegodemascotas.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.util.Util;
import com.applications.toms.juegodemascotas.view.adapter.MyViewPagerAdapter;
import com.applications.toms.juegodemascotas.view.fragment.AmigosFragment;
import com.applications.toms.juegodemascotas.view.fragment.JuegosFragment;
import com.applications.toms.juegodemascotas.view.fragment.MascotasFragment;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int KEY_LOGIN=101;

    private FirebaseAuth mAuth;
    private static FirebaseUser currentUser;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //print hash
        Util.printHash(this);
        //crush
//        new UCEHandler.Builder(this).build();

        //Auth
        mAuth = FirebaseAuth.getInstance();

        //Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        //NavigationView
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);

        //Btn Hamburguesa
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.main:
                        Toast.makeText(MainActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.my_profile:
                        goLogIn();
//                        Toast.makeText(MainActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.my_pets:
                        Toast.makeText(MainActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.chat:
                        Toast.makeText(MainActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.plays:
                        Toast.makeText(MainActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });

        //TABS FRAGMENTS
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new JuegosFragment());
        fragmentList.add(new AmigosFragment());
        fragmentList.add(new MascotasFragment());
        //Titulos del tab
        List<String> titulos = new ArrayList<>();
        titulos.add("Juegos");
        titulos.add("Amigos");
        titulos.add("Mascotas");
        //ViewPager
        ViewPager viewPager = findViewById(R.id.viewPager);
        //TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        //Asociar al view pager
        tabLayout.setupWithViewPager(viewPager);
        //Adapter
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(),fragmentList, titulos);
        viewPager.setAdapter(adapter);
        //Inicializado
        viewPager.setCurrentItem(0);
        //viewPager.setPageMargin(30);
        //viewPager.setClipToPadding(false);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    //Ir al Login
    public void goLogIn(){
        if (currentUser!=null){
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            currentUser = null;
            Toast.makeText(this, "Has salido de tu sesion", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivityForResult(intent, KEY_LOGIN);
        }
    }
}
