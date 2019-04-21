package com.applications.toms.juegodemascotas.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.DuenioController;
import com.applications.toms.juegodemascotas.controller.PetsFromOwnerController;
import com.applications.toms.juegodemascotas.model.Duenio;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.util.Util;
import com.applications.toms.juegodemascotas.view.adapter.MyViewPagerAdapter;
import com.applications.toms.juegodemascotas.view.fragment.AmigosFragment;
import com.applications.toms.juegodemascotas.view.fragment.JuegosFragment;
import com.applications.toms.juegodemascotas.view.fragment.MascotasFragment;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int KEY_LOGIN=101;
    public static final String KEY_DUENIO = "duenio";

    private FirebaseAuth mAuth;
    private static FirebaseUser currentUser;
    private FirebaseDatabase mDatabase;
    private static DatabaseReference mReference;
    private static FirebaseStorage mStorage;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Menu menuToolbar;
    private MenuItem item_toolbar;

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
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();

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
                        if (currentUser!=null){
                            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(ProfileActivity.KEY_TYPE,"1");
                            bundle.putString(ProfileActivity.KEY_USER_ID,"0");
                            bundle.putString(ProfileActivity.KEY_PET_ID,"0");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else {
                            goLogIn();
                        }
                        return true;
                    case R.id.my_pets:
                        if (currentUser!=null){
                            Intent intent = new Intent(MainActivity.this,MyPetsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(MyPetsActivity.KEY_DUENIO_ID,currentUser.getUid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else {
                            goLogIn();
                        }
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

    //Inflar Menu para ver el boton de ir al Login
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menuToolbar = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);
        item_toolbar = menu.findItem(R.id.login_toolbar);

        if (currentUser!=null){
            item_toolbar.setIcon(getDrawable(R.drawable.account_off_36));
        }else {
            item_toolbar.setIcon(getDrawable(R.drawable.ic_person_black_24dp));
        }

        return super.onCreateOptionsMenu(menu);
    }
    //On item Click del Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.login_toolbar:
                goLogIn();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Ir al Login
    public void goLogIn(){
        if (currentUser!=null){
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            currentUser = null;
            Toast.makeText(this, "Has salido de tu sesion", Toast.LENGTH_SHORT).show();
            item_toolbar.setIcon(getDrawable(R.drawable.ic_person_black_24dp));
        }else {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivityForResult(intent, KEY_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case KEY_LOGIN:
                    item_toolbar.setIcon(getDrawable(R.drawable.account_off_36));
                    createDataBaseOwner();
                    break;
            }
        }
    }

    public void createDataBaseOwner(){
        DuenioController duenioController = new DuenioController();
        duenioController.giveDuenios(this, new ResultListener<List<Duenio>>() {
            @Override
            public void finish(List<Duenio> resultado) {
                currentUser = mAuth.getCurrentUser();
                String name = "";
                if (currentUser.getDisplayName() != null) {
                    name = currentUser.getDisplayName();
                } else {
                    name = currentUser.getEmail();
                }
                String photo = "";
                if (currentUser.getPhotoUrl() != null) {
                    photo = currentUser.getPhotoUrl().toString() + "?height=500";
                }

                Duenio newDuenio = new Duenio(currentUser.getUid(), name, currentUser.getEmail(), photo);

                if (resultado!=null) {
                    for (Duenio duenioDB : resultado) {
                        if (!duenioDB.getUserId().equals(currentUser.getUid())) {
                            DatabaseReference idOwnerDB = mReference.push(); //mReference.child(newDuenio.getUserId()).push();
                            idOwnerDB.setValue(newDuenio);
                        }
                    }
                }else {
                    DatabaseReference idOwnerDB = mReference.push();
                    idOwnerDB.setValue(newDuenio);
                }
            }
        });
    }

    public static void updateProfilePicture(final String userID, final String newPhoto){
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                    Duenio duenio = childSnapShot.getValue(Duenio.class);
                    if (duenio.getUserId().equals(userID)){
                        mReference.child(childSnapShot.getKey()).child("fotoDuenio").setValue(newPhoto);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void updateProfile(final String name, final String dir, final String birth, final String sex, final String about){
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                    Duenio duenio = childSnapShot.getValue(Duenio.class);
                    if (duenio.getUserId().equals(currentUser.getUid())){
                        mReference.child(childSnapShot.getKey()).child("nombre").setValue(name);
                        mReference.child(childSnapShot.getKey()).child("sexo").setValue(sex);
                        mReference.child(childSnapShot.getKey()).child("fechaNacimiento").setValue(birth);
                        mReference.child(childSnapShot.getKey()).child("direccion").setValue(dir);
                        mReference.child(childSnapShot.getKey()).child("infoDuenio").setValue(about);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void updatePhotoPet(final String idOwner, final String idPet, String oldPhoto , final String newPhoto){
        StorageReference storageReference = mStorage.getReference().child(idOwner).child(oldPhoto);
        storageReference.delete();

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                    Duenio duenio = childSnapShot.getValue(Duenio.class);
                    if (duenio.getUserId().equals(idOwner)){
                        List<Mascota> mascotas = duenio.getMisMascotas();
                        for (Mascota m:mascotas) {
                            if (m.getIdPet().equals(idPet)){
                                mReference.child(childSnapShot.getKey()).child("misMascotas").child(String.valueOf(mascotas.indexOf(m))).child("fotoMascota").setValue(newPhoto);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void deleteProfilePet(final String idOwner, final String idPet){
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                    Duenio duenio = childSnapShot.getValue(Duenio.class);
                    if (duenio.getUserId().equals(idOwner)){
                        List<Mascota> mascotas = duenio.getMisMascotas();
                        for (Mascota m:mascotas) {
                            if (m.getIdPet().equals(idPet)){
                                mascotas.remove(m);
                                StorageReference storageReference = mStorage.getReference().child(idOwner).child(m.getFotoMascota());
                                storageReference.delete();
                                mReference.child(childSnapShot.getKey()).child("misMascotas").setValue(mascotas);
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
