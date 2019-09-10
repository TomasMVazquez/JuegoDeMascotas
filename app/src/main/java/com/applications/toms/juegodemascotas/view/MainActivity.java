package com.applications.toms.juegodemascotas.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Duenio;
import com.applications.toms.juegodemascotas.util.Util;
import com.applications.toms.juegodemascotas.view.adapter.MyViewPagerAdapter;
import com.applications.toms.juegodemascotas.view.fragment.FriendsFragment;
import com.applications.toms.juegodemascotas.view.fragment.PlayDateFragment;
import com.applications.toms.juegodemascotas.view.fragment.PetsFragment;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    public static final int KEY_LOGIN=101;
    public static final String KEY_DUENIO = "duenio";

    private FirebaseAuth mAuth;
    private static FirebaseUser currentUser;
    private static FirebaseFirestore db;
    private static FirebaseStorage mStorage;
    private static StorageReference raiz;
    private static Context context;

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

        if (isServicesOk()){
            Toast.makeText(this, "finish checking", Toast.LENGTH_SHORT).show();
        }

        //Auth
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        raiz = mStorage.getReference();

        db = FirebaseFirestore.getInstance();
        context = getApplicationContext();

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
                        Intent intentMap = new Intent(MainActivity.this,MapTest.class);
                        startActivity(intentMap);
                        Toast.makeText(MainActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });

        //TABS FRAGMENTS
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new PlayDateFragment());
        fragmentList.add(new FriendsFragment());
        fragmentList.add(new PetsFragment());
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

    //Resultado del activity Result
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

    //Crear la base de datos
    public void createDataBaseOwner(){

        //TODO FUNCIONA PERO MEJORAR ESTA PARTE
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

        final Duenio newDuenio = new Duenio(currentUser.getUid(), name, currentUser.getEmail(), photo);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final DocumentReference userRef = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
//                    Toast.makeText(MainActivity.this, "Usuario No Existe Creando...", Toast.LENGTH_SHORT).show();
                    userRef.set(newDuenio).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "LogInSuccesful", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, userRef.getId(), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Error Log In", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
//                    Toast.makeText(MainActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Actualizar la foto de perfil de la persona
    public static void updateProfilePicture(String oldPhoto,final String newPhoto, Uri uriTemp){
        StorageReference nuevaFoto = raiz.child(currentUser.getUid()).child(newPhoto);

        final DocumentReference userRef = db.collection("Owners")
                .document(currentUser.getUid());

        StorageReference storageReference = mStorage.getReference().child(currentUser.getUid()).child(oldPhoto);
        storageReference.delete();

        userRef.update("fotoDuenio",newPhoto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //TODO
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO
                    }
                });


        final UploadTask uploadTask = nuevaFoto.putFile(uriTemp);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Profile Foto OK!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Actualizar el perfil de la persona
    public static void updateProfile(final String name, final String dir, final String birth, final String sex, final String about){
        DocumentReference userRef = db.collection("Owners")
                .document(currentUser.getUid());

        userRef.update(
                "nombre",name,
                "sexo", sex,
                "fechaNacimiento",birth,
                "direccion",dir,
                "infoDuenio",about
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //TODO
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO
                    }
                });
    }

    //Actualizar la foto de perfil de la mascota
    public static void updatePhotoPet(final String idOwner, final String idPet, String oldPhoto , final String newPhoto,Uri uriTemp){
        StorageReference storageReference = mStorage.getReference().child(idOwner).child(oldPhoto);
        storageReference.delete();

        DocumentReference userRefMasc = db.collection(context.getResources().getString(R.string.collection_users))
                .document(idOwner).collection(context.getResources().getString(R.string.collection_my_pets)).document(idPet);

        userRefMasc.update("fotoMascota",newPhoto).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //TODO
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO
            }
        });

        DocumentReference mascRef = db.collection(context.getResources().getString(R.string.collection_pets))
                .document(idPet);

        mascRef.update("fotoMascota",newPhoto).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //TODO
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO
            }
        });

        StorageReference nuevaFotoPet = mStorage.getReference().child(idOwner).child(newPhoto);
        final UploadTask uploadTaskPet = nuevaFotoPet.putFile(uriTemp);
        uploadTaskPet.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Todo
            }
        });

    }

    //Borrar el perfil de la mascota
    public static void deleteProfilePet(final String idOwner, final String idPet){

        DocumentReference userRefMasc = db.collection(context.getResources().getString(R.string.collection_users))
                .document(idOwner).collection(context.getResources().getString(R.string.collection_my_pets)).document(idPet);

        userRefMasc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //todo
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //todo
            }
        });

        DocumentReference mascRef = db.collection(context.getResources().getString(R.string.collection_pets))
                .document(idPet);

        mascRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //todo
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //todo
            }
        });
    }

    //MAPS


    //CHECK VERSION FOR MAPS
    public boolean isServicesOk (){
        Log.d(TAG, "isServicesOk: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS){
            //Everything is find and user can do maps request
            Log.d(TAG, "isServicesOk: Google Play services is working");
            Toast.makeText(this, " Service ok ", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //Error occurred but can be resolved
            Log.d(TAG, "isServicesOk: an error occured but can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this, "You cannot make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
