package com.applications.toms.juegodemascotas.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.applications.toms.juegodemascotas.view.menu_fragments.UsersFragment;
import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.AdminStorage;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.util.Keys;
import com.applications.toms.juegodemascotas.util.Util;
import com.applications.toms.juegodemascotas.view.adapter.MyViewPagerAdapter;
import com.applications.toms.juegodemascotas.view.fragment.AddPetFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.ChatRoomFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.FriendsFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.MyPetsFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.PlayDateFragment;
import com.applications.toms.juegodemascotas.view.fragment.ZoomOutPageTransformer;
import com.applications.toms.juegodemascotas.view.menu_fragments.PlaysToGoFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.ProfileFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.SearchFragment;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

public class MainActivity extends AppCompatActivity implements
        SearchFragment.SearchInterface,
        MyPetsFragment.MyPetsInterface,
        ProfileFragment.ProfileFragmentListener {

    public static final int KEY_LOGIN = 101;
    private static final String TAG = "MainActivity";
    public static final String INTRO = "IntroApp";
    public static final String FIRST_TIME = "FirstTimeStartFlag";
    private static final String SHOWCASE_ID = "sequence main";

    private static FirebaseUser currentUser;
    private static FirebaseFirestore db;
    private static FirebaseStorage mStorage;
    private static StorageReference raiz;
    private static Context context;
    private static String userFirestore;
    private static String myPetsFirestore;
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private static ActionBar actionBar;
    private Toolbar myToolbar;
    private TabLayout tabLayout;
    private static CoordinatorLayout coordinatorSnack;
    private MenuItem item_toolbar;

    //Fragment declaration globally
    private FragmentManager fragmentManager;
    private PlaysToGoFragment playsToGoFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;
    private ChatRoomFragment chatRoomFragment;
    private MyPetsFragment myPetsFragment;

    private UsersFragment usersFragment;

    //Método de cambio de estado en la base de datos
    private void status(String status){
        DocumentReference userRef = db.collection(Keys.KEY_OWNER).document(currentUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Keys.KEY_OWNER_STATUS,status);
        userRef.update(hashMap);
    }

    //Update Owner profile Avatar
    public static void updateProfilePicture(String oldPhoto, final String newPhoto, Uri uriTemp) {
        StorageReference nuevaFoto = raiz.child(currentUser.getUid()).child(newPhoto);

        final DocumentReference userRef = db.collection(Keys.KEY_OWNER)
                .document(currentUser.getUid());

        //Delete old photo
        if (oldPhoto != null) {
            StorageReference storageReference = mStorage.getReference().child(currentUser.getUid()).child(oldPhoto);
            storageReference.delete();
        }

        final UploadTask uploadTask = nuevaFoto.putFile(uriTemp);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }

                return nuevaFoto.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();

                    userRef.update(Keys.KEY_OWNER_IMAGEURL,mUri);
                }else {
                    Snackbar.make(coordinatorSnack,context.getString(R.string.error_image_db),Snackbar.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(coordinatorSnack,e.getMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    //Update owner profile info
    public static void updateProfile(final String name, final String dir, final String birth, final String sex, final String about) {
        DocumentReference userRef = db.collection("Owners")
                .document(currentUser.getUid());

        userRef.update(
                "name", name,
                "sex", sex,
                "birthDate", birth,
                "address", dir,
                "aboutMe", about
        );
    }

    //Update pet profile info
    public static void updatePhotoPet(final String idOwner, final String idPet, String oldPhoto, final String newPhoto, Uri uriTemp) {
        StorageReference storageReference = mStorage.getReference().child(idOwner).child(oldPhoto);
        storageReference.delete();

        DocumentReference userRefMasc = db.collection(Keys.KEY_OWNER)
                .document(idOwner).collection(Keys.KEY_MY_PETS).document(idPet);

        DocumentReference mascRef = db.collection(Keys.KEY_PET)
                .document(idPet);

        StorageReference nuevaFotoPet = mStorage.getReference().child(idOwner).child(newPhoto);
        UploadTask uploadTask = nuevaFotoPet.putFile(uriTemp);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }

                return nuevaFotoPet.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();

                    mascRef.update(Keys.KEY_PET_PHOTO, mUri);
                    userRefMasc.update(Keys.KEY_PET_PHOTO, mUri);
                }else {
                    Snackbar.make(coordinatorSnack,context.getString(R.string.error_image_db),Snackbar.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(coordinatorSnack,e.getMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    //Delete pet
    public static void deleteProfilePet(final String idOwner, final String idPet) {

        DocumentReference userRefMasc = db.collection(Keys.KEY_OWNER)
                .document(idOwner).collection(Keys.KEY_MY_PETS).document(idPet);

        userRefMasc.delete();

        DocumentReference mascRef = db.collection(Keys.KEY_PET)
                .document(idPet);

        mascRef.delete();
    }

    //Add pet to database
    public static void addPetToDataBase(final String name, final String raza, final String size, final String birth, final String sex, final String photo, final String info) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference userRefMasc = db.collection(userFirestore)
                .document(currentUser.getUid()).collection(myPetsFirestore);

        final String idPet = userRefMasc.document().getId();
        Pet newPet = new Pet(idPet, name,name.toLowerCase(), raza, size, sex, birth, photo, info, currentUser.getUid());

        userRefMasc.document(idPet).set(newPet);

        DocumentReference petsRef = db.collection(Keys.KEY_PET)
                .document(idPet);

        petsRef.set(newPet);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        //Delete Old Play Dates
        AdminStorage.deleteOldPlayDates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            //Delete old plays from the user database
            AdminStorage.deleteMyOldPlayDates(context, currentUser.getUid());
            //Cambio del estado del usuario a online estando la app en primer plano
            status(getString(R.string.status_on));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentUser != null) {
            //Cambio del estado del usuario a offline estando la app en segundo plano
            status(getString(R.string.status_off));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //print hash --
        Util.printHash(this);

        coordinatorSnack = findViewById(R.id.coordinatorSnackMain);
        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        if (intent.getExtras()!= null && intent.getExtras().size() > 1){
            Bundle profBundle = intent.getExtras();
            profileChange(profBundle.getString(ProfileFragment.KEY_TYPE),profBundle.getString(ProfileFragment.KEY_USER_ID),profBundle.getString(ProfileFragment.KEY_PET_ID));
        }

        //Check if service is ok for Maps
        if (!Util.isServicesOk(this)) {
            Snackbar.make(coordinatorSnack,getString(R.string.problems_google),Snackbar.LENGTH_LONG).show();
        }

        if (!Util.isOnline(this)) {
            Snackbar.make(coordinatorSnack,getString(R.string.device_online),Snackbar.LENGTH_LONG).show();
        }

        //get instance from Firebase
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        raiz = mStorage.getReference();
        db = FirebaseFirestore.getInstance();
        userFirestore = getResources().getString(R.string.collection_users);
        myPetsFirestore = getResources().getString(R.string.collection_my_pets);

        //Contect from Application
        context = getApplicationContext();

        //Toolbar
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        changeActionBarTitle(getResources().getString(R.string.app_name));

        //Getting de SupportFragmentManager, declared globally
        fragmentManager.addOnBackStackChangedListener(() -> {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                FragmentTitles currentFragment = (FragmentTitles) fragmentManager.findFragmentById(R.id.mainContainer);
                int fragmentTitle = currentFragment.getFragmentTitle();
                myToolbar.setTitle(fragmentTitle);
            } else {
                myToolbar.setTitle(R.string.app_name);
            }
        });

        //NavigationView
        drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation);

        //Btn Hamburguesa
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Setting the navigation View
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();
            if (currentUser != null) {
                switch (menuItem.getItemId()) {
                    case R.id.main:
                            while (fragmentManager.getBackStackEntryCount() > 0) {
                                fragmentManager.popBackStackImmediate();
                            }
                        return true;
                    case R.id.my_profile:
                        Bundle bundle = new Bundle();
                        bundle.putString(ProfileFragment.KEY_TYPE, "1");
                        bundle.putString(ProfileFragment.KEY_USER_ID, currentUser.getUid());
                        bundle.putString(ProfileFragment.KEY_PET_ID, "0");
                        changeActionBarTitle(getString(R.string.my_profile));
                        if (profileFragment == null) {
                            profileFragment = new ProfileFragment();
                        }
                        profileFragment.setArguments(bundle);
                        fragments(profileFragment, ProfileFragment.TAG);
                        return true;
                    case R.id.my_pets:
                        changeActionBarTitle(getString(R.string.my_pets));
                        if (myPetsFragment == null) {
                            myPetsFragment = new MyPetsFragment();
                        }
                        fragments(myPetsFragment, MyPetsFragment.TAG);
                        return true;
                    case R.id.plays:
                        changeActionBarTitle(getString(R.string.plays));
                        if (playsToGoFragment == null) {
                            playsToGoFragment = new PlaysToGoFragment();
                        }
                        fragments(playsToGoFragment, PlaysToGoFragment.TAG);
                        return true;
                    case R.id.chat:
                        changeActionBarTitle(getString(R.string.collection_chats));
                        if (chatRoomFragment == null) {
                            chatRoomFragment = new ChatRoomFragment();
                        }
                        fragments(chatRoomFragment, ChatRoomFragment.TAG);
                        return true;
                    case R.id.searchDog:
                        changeActionBarTitle(getString(R.string.search));
                        if (searchFragment == null) {
                            searchFragment = new SearchFragment();
                        }
                        fragments(searchFragment, SearchFragment.TAG);
                        return true;
                    case R.id.users:
                        changeActionBarTitle(getString(R.string.search));
                        if (usersFragment == null) {
                            usersFragment = new UsersFragment();
                        }
                        fragments(usersFragment, UsersFragment.TAG);
                        return true;

                    case R.id.aboutLibraries:
                        new LibsBuilder()
                                .withActivityTitle(getString(R.string.app_name))
                                .withAboutDescription(getString(R.string.app_desc))
                                .withAboutIconShown(true)
                                .withAutoDetect(true)
                                .withAboutVersionShown(true)
                                .withLicenseDialog(true)
                                .withLicenseShown(true)
                                .start(this);

                        return true;
                }
            }else {
                goLogIn();
            }
            return false;
        });

        //TABS FRAGMENTS
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new PlayDateFragment());
        fragmentList.add(new FriendsFragment());

        //Titulos del tab
        List<String> titulos = new ArrayList<>();
        titulos.add("Juegos");
        titulos.add("Amigos");

        //ViewPager
        ViewPager viewPager = findViewById(R.id.viewPager);
        //TabLayout
        tabLayout = findViewById(R.id.tabLayout);
        //Asociar al view pager
        tabLayout.setupWithViewPager(viewPager);
        //Adapter
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragmentList, titulos);
        viewPager.setAdapter(adapter);
        //Inicializado
        viewPager.setCurrentItem(0);
        //Page transformer when you change fragment by sliding
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        presentShowcaseView();

        getToken();
    }

    //Methods
    private void getToken() {
        //Buscar Token y guardarlo para las notificaciones
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        //LLamar al método para actualizar el token en la base de datos
                        updateTokenDB(token);
                    }
                });
    }

    private void updateTokenDB(String token){
        if (currentUser != null) {
            DocumentReference userRef = db.collection(Keys.KEY_OWNER).document(currentUser.getUid());
            userRef.update(Keys.KEY_OWNER_TOKEN, token);
        }
    }

    void presentShowcaseView() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        //sequence.setConfig(config);

        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(this)
                .corner(30)
                .textColor(getColor(R.color.colorOnBoardText))
                .text(getString(R.string.onboard_main_toolbar_1) + "<b> " + getString(R.string.app_name) + "</b> " +
                        "<br><br>" +
                        getString(R.string.onboard_main_toolbar_2) +
                        "<br><br>" +
                        getString(R.string.onboard_main_toolbar_3) +
                        "<br><br>" +
                        getString(R.string.onboard_main_toolbar_4) +
                        "<br><br>" +
                        getString(R.string.onboard_main_toolbar_5));

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(myToolbar)
                        .setToolTip(toolTip1)
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(10)
                        .setDismissOnTouch(true)
                        .build()
        );

        ShowcaseTooltip toolTip2 = ShowcaseTooltip.build(this)
                .corner(30)
                .textColor(getColor(R.color.colorOnBoardText))
                .text(getString(R.string.onboard_main_tablayout_1) +
                        "<br><br>" +
                        getString(R.string.onboard_main_tablayout_2));

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(tabLayout)
                        .setToolTip(toolTip2)
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(5)
                        .setDismissOnTouch(true)
                        .build()
        );

        sequence.start();


        sequence.setOnItemDismissedListener((itemView, position) -> {
            if (position == 1){
                PlayDateFragment.presentShowcaseView(1000); // one second delay
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }else if (fragmentManager.getBackStackEntryCount() > 0) {
            while (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }
        }else {
            super.onBackPressed();
        }

    }

    //Change ActionBar Title
    public static void changeActionBarTitle(String newTitle){
        actionBar.setTitle(newTitle);
    }

    //Replace fragment and add tags Fragment
    private void fragments(Fragment fragment, String fragmentTag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(TAG).commit();
    }

    //Inflate Menu where is showing the Login btn
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        item_toolbar = menu.findItem(R.id.login_toolbar);

        if (currentUser != null) {
            item_toolbar.setIcon(getDrawable(R.drawable.account_off_36));
        } else {
            item_toolbar.setIcon(getDrawable(R.drawable.ic_person_black_24dp));
        }

        return super.onCreateOptionsMenu(menu);
    }

    //On item Click Menu go to login
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_toolbar:
                goLogIn();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        System.exit(0);
                    } else {
                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }
    }

    //Go to Login
    public void goLogIn() {
        if (currentUser != null) {
            doRestart(this);
        } else {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivityForResult(intent, KEY_LOGIN);
        }
    }

    //Activity Result if succesful create DataBase for the user
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

        //On Activity Result de las fotos
        EasyImage.handleActivityResult(requestCode, resultCode, data, MainActivity.this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource imageSource, int i) {

            }

            @Override
            public void onImagesPicked(@NonNull List<File> list, EasyImage.ImageSource imageSource, int i) {

                if (list.size() > 0) {
                    File file = list.get(0);
                    final Uri uri = Uri.fromFile(file);
                    final Uri uriTemp = Uri.fromFile(new File(uri.getPath()));
                    ProfileFragment.avatarUpdate(i, uri,uriTemp);
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource, int i) {

            }
        });

    }


    //Create DataBase if first time or not if not
    public void createDataBaseOwner() {

        currentUser = mAuth.getCurrentUser();

        String name = "";
        if (currentUser.getDisplayName() != null) {
            name = currentUser.getDisplayName();
        } else {
            String[] emialName = currentUser.getEmail().split("@");
            name = emialName[0];
        }
        String photo = null;
        if (currentUser.getPhotoUrl() != null) {
            photo = currentUser.getPhotoUrl().toString() + "?height=500";
        }else {
            photo = getString(R.string.image_default);
        }

        final Owner newOwner = new Owner(currentUser.getUid(), name, currentUser.getEmail(), photo,name.toLowerCase(),getString(R.string.status_off));

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings
                .Builder()
                .build();

        db.setFirestoreSettings(settings);

        final DocumentReference userRef = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
//              Usuario No Existe Creando...
                userRef.set(newOwner).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recreate();
                    } else {
                        Snackbar.make(coordinatorSnack,getString(R.string.problems_login),Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
//              El usuario ya existe
                recreate();
            }
        });

    }

    //CHECK INSTANCE OF FRAGMENT ***to implement the listenings
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof MyPetsFragment) {
            MyPetsFragment myPetsFragment = (MyPetsFragment) fragment;
            myPetsFragment.setMyPetsInterface(this);
        }
        /* TODO-->
        if (fragment instanceof FriendsFragment) {
            Log.d(TAG, "onAttachFragment: Firends");
            FriendsFragment friendsFragment = (FriendsFragment) fragment;
            //friendsFragment.setFriendsInterface(this);
        }
        if (fragment instanceof SearchFragment) {
            Log.d(TAG, "onAttachFragment: Search");
            SearchFragment searchFragment = (SearchFragment) fragment;
           // searchFragment.setSearchInterface(this);
        }

        if (fragment instanceof UsersFragment){
            Log.d(TAG, "onAttachFragment: Users");
            UsersFragment usersFragment = (UsersFragment) fragment;
        }
         */

    }

    @Override
    public void goToPetProfile(String idOwner, Pet choosenPet) {
        Bundle bundle = new Bundle();
        bundle.putString(ProfileFragment.KEY_TYPE,"2");
        bundle.putString(ProfileFragment.KEY_USER_ID,idOwner);
        bundle.putString(ProfileFragment.KEY_PET_ID,choosenPet.getIdPet());
        ProfileFragment myProfileFragment = new ProfileFragment();
        myProfileFragment.setArguments(bundle);
        fragments(myProfileFragment,ProfileFragment.TAG);
    }

    //****MY PETS FRAGMENT ****
    //Go to Add Pet Fragment
    @Override
    public void goToAddPet() {
        AddPetFragment addPetFragment = new AddPetFragment();
        changeActionBarTitle(getString(R.string.add_pet_title));
        fragments(addPetFragment, AddPetFragment.TAG);
    }

    @Override
    public void petSelectedListener(String idOwner, String petId) {
        Bundle bundle = new Bundle();
        bundle.putString(ProfileFragment.KEY_TYPE,"2");
        bundle.putString(ProfileFragment.KEY_USER_ID,idOwner);
        bundle.putString(ProfileFragment.KEY_PET_ID,petId);
        ProfileFragment myProfileFragment = new ProfileFragment();
        myProfileFragment.setArguments(bundle);
        fragments(myProfileFragment,myProfileFragment.TAG);
    }

    @Override
    public void profileChange(String keyType, String idOwner, String petId) {
        Bundle bundle = new Bundle();
        bundle.putString(ProfileFragment.KEY_TYPE,keyType);
        bundle.putString(ProfileFragment.KEY_USER_ID,idOwner);
        bundle.putString(ProfileFragment.KEY_PET_ID,petId);
        ProfileFragment myProfileFragment = new ProfileFragment();
        myProfileFragment.setArguments(bundle);
        fragments(myProfileFragment,myProfileFragment.TAG);
    }

}
