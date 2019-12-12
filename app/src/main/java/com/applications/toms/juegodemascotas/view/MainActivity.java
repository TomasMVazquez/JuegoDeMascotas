package com.applications.toms.juegodemascotas.view;

import android.app.Activity;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Chat;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.AdminStorage;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.util.Util;
import com.applications.toms.juegodemascotas.view.adapter.MyViewPagerAdapter;
import com.applications.toms.juegodemascotas.view.fragment.AddPetFragment;
import com.applications.toms.juegodemascotas.view.fragment.ChatFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.ChatRoomFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.FriendsFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.MyPetsFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.PlayDateFragment;
import com.applications.toms.juegodemascotas.view.fragment.ZoomOutPageTransformer;
import com.applications.toms.juegodemascotas.view.menu_fragments.PlaysToGoFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.ProfileFragment;
import com.applications.toms.juegodemascotas.view.menu_fragments.SearchFragment;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ChatRoomFragment.onChatRoomNotify,
        FriendsFragment.FriendsInterface, SearchFragment.SearchInterface, MyPetsFragment.MyPetsInterface,
        ProfileFragment.ProfileFragmentListener {

    public static final int KEY_LOGIN = 101;
    private static final String TAG = "MainActivity";
    private static FirebaseUser currentUser;
    private static FirebaseFirestore db;
    private static FirebaseStorage mStorage;
    private static StorageReference raiz;
    private static Context context;
    private static String userFirestore;
    private static String myPetsFirestore;
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private ActionBar actionBar;

    private MenuItem item_toolbar;

    //Fragment declaration globally
    private FragmentManager fragmentManager;
    private PlaysToGoFragment playsToGoFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;
    private ChatRoomFragment chatRoomFragment;
    private MyPetsFragment myPetsFragment;

    //Update Owner profile Avatar
    public static void updateProfilePicture(String oldPhoto, final String newPhoto, Uri uriTemp) {
        StorageReference nuevaFoto = raiz.child(currentUser.getUid()).child(newPhoto);

        final DocumentReference userRef = db.collection("Owners")
                .document(currentUser.getUid());

        //Delete old photo
        if (oldPhoto != null) {
            StorageReference storageReference = mStorage.getReference().child(currentUser.getUid()).child(oldPhoto);
            storageReference.delete();
        }

        //Update new Photo
        userRef.update("fotoDuenio", newPhoto)
                .addOnSuccessListener(aVoid -> {
                    //TODO updateProfilePicture addOnSuccessListener is it necessary?
                })
                .addOnFailureListener(e -> {
                    //TODO updateProfilePicture addOnFailureListener is it necessary?
                });


        final UploadTask uploadTask = nuevaFoto.putFile(uriTemp);
        uploadTask.addOnSuccessListener(taskSnapshot -> Toast.makeText(context, "Profile Foto OK!", Toast.LENGTH_SHORT).show());
    }

    //Update owner profile info
    public static void updateProfile(final String name, final String dir, final String birth, final String sex, final String about) {
        DocumentReference userRef = db.collection("Owners")
                .document(currentUser.getUid());

        userRef.update(
                "nombre", name,
                "sexo", sex,
                "fechaNacimiento", birth,
                "direccion", dir,
                "infoDuenio", about
        )
                .addOnSuccessListener(aVoid -> {
                    //TODO updateProfile addOnSuccessListener is it necessary?
                })
                .addOnFailureListener(e -> {
                    //TODO updateProfile addOnFailureListener is it necessary?
                });
    }

    //Update pet profile info
    public static void updatePhotoPet(final String idOwner, final String idPet, String oldPhoto, final String newPhoto, Uri uriTemp) {
        StorageReference storageReference = mStorage.getReference().child(idOwner).child(oldPhoto);
        storageReference.delete();

        DocumentReference userRefMasc = db.collection(context.getResources().getString(R.string.collection_users))
                .document(idOwner).collection(context.getResources().getString(R.string.collection_my_pets)).document(idPet);

        userRefMasc.update("fotoMascota", newPhoto).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        mascRef.update("fotoMascota", newPhoto).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    //Delete pet
    public static void deleteProfilePet(final String idOwner, final String idPet) {

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

    //Add pet to database
    public static void addPetToDataBase(final String name, final String raza, final String size, final String birth, final String sex, final String photo, final String info) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference userRefMasc = db.collection(userFirestore)
                .document(currentUser.getUid()).collection(myPetsFirestore);

        final String idPet = userRefMasc.document().getId();
        Pet newPet = new Pet(idPet, name, raza, size, sex, birth, photo, info, currentUser.getUid());

        userRefMasc.document(idPet).set(newPet)
                .addOnSuccessListener(aVoid -> {
                    //TODO
                })
                .addOnFailureListener(e -> {
                    //TODO
                });

        DocumentReference petsRef = db.collection(context.getResources().getString(R.string.collection_pets))
                .document(idPet);

        petsRef.set(newPet).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //TODO
            } else {
                //TODO
            }
        });
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //print hash --
        Util.printHash(this);
        //crush -- this is useful to test the app so a QA can send the error report
//        new UCEHandler.Builder(this).build();

        //Check if service is ok for Maps
        if (Util.isServicesOk(this)) {
            Log.d(TAG, "onCreate: Check Service OK!");
        } // TODO Main Util.isServicesOk add what happend if not

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
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.app_name));
        //TODO Since we have de search view the title is not shown
//        SearchView searchView = findViewById(R.id.searchView);
//        searchView.setQueryHint(getString(R.string.search));

        //Getting de SupportFragmentManager, declared globally
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentTitles currentFragment = (FragmentTitles) fragmentManager.findFragmentById(R.id.mainContainer);
                    int fragmentTitle = currentFragment.getFragmentTitle();
                    myToolbar.setTitle(fragmentTitle);
                } else {
                    myToolbar.setTitle(R.string.app_name);
                }
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
                        Toast.makeText(MainActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.my_profile:
                        Log.d(TAG, "onCreate: Profile Fragment");
                        Bundle bundle = new Bundle();
                        bundle.putString(ProfileFragment.KEY_TYPE, "1");
                        bundle.putString(ProfileFragment.KEY_USER_ID, currentUser.getUid());
                        bundle.putString(ProfileFragment.KEY_PET_ID, "0");
                        actionBar.setTitle(getString(R.string.my_profile));
                        if (profileFragment == null) {
                            profileFragment = new ProfileFragment();
                            Log.d("FRAGMENT CREADO = ", "Profile");
                        }
                        profileFragment.setArguments(bundle);
                        fragments(profileFragment, ProfileFragment.TAG);
                        return true;
                    case R.id.my_pets:
                        Log.d(TAG, "onCreate: MyPets Fragment");
                        actionBar.setTitle(getString(R.string.my_pets));
                        if (myPetsFragment == null) {
                            myPetsFragment = new MyPetsFragment();
                            Log.d("FRAGMENT CREADO = ", "MyPets");
                        }
                        fragments(myPetsFragment, MyPetsFragment.TAG);
                        return true;
                    case R.id.plays:
                        Log.d(TAG, "onCreate: Plays To Go Fragment");
                        actionBar.setTitle(getString(R.string.plays));
                        if (playsToGoFragment == null) {
                            playsToGoFragment = new PlaysToGoFragment();
                            Log.d("FRAGMENT CREADO = ", "PlaysToGo");
                        }
                        fragments(playsToGoFragment, PlaysToGoFragment.TAG);
                        return true;
                    case R.id.chat:
                        Log.d(TAG, "onCreate: Get type 1 -> showRoom");
                        actionBar.setTitle(getString(R.string.collection_chats));
                        if (chatRoomFragment == null) {
                            chatRoomFragment = new ChatRoomFragment();
                            Log.d("FRAGMENT CREADO = ", "ChatRoom");
                        }
                        fragments(chatRoomFragment, ChatRoomFragment.TAG);
                        return true;
                    case R.id.searchDog:
                        Log.d(TAG, "onCreate: -> SearchFragment");
                        actionBar.setTitle(getString(R.string.search));
                        if (searchFragment == null) {
                            searchFragment = new SearchFragment();
                            Log.d("FRAGMENT CREADO = ", "Search");
                        }
                        fragments(searchFragment, SearchFragment.TAG);
                        return true;
                }
            }else {
                goLogIn();
            }
            return false;
        });

        //TABS FRAGMENTS
        List<Fragment> fragmentList = new ArrayList<>();
        Log.d("FRAGMENT CREADO = ", "PlayDate + Firends");
        fragmentList.add(new PlayDateFragment());
        fragmentList.add(new FriendsFragment());

        //Titulos del tab
        List<String> titulos = new ArrayList<>();
        titulos.add("Juegos");
        titulos.add("Amigos");

        //ViewPager
        ViewPager viewPager = findViewById(R.id.viewPager);
        //TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        //Asociar al view pager
        tabLayout.setupWithViewPager(viewPager);
        //Adapter
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragmentList, titulos);
        viewPager.setAdapter(adapter);
        //Inicializado
        viewPager.setCurrentItem(0);
        //Page transformer when you change fragment by sliding
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        //ViewPager Listener TODO ViewPager Lisetner doing nothing, is it necessary?
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

    //Replace fragment and add tags Fragment //TODO CHANGE TO FRAGMENTS 1
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

    //Go to Login
    public void goLogIn() {
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            currentUser = null;
            Toast.makeText(this, "Has salido de tu sesion", Toast.LENGTH_SHORT).show();
            item_toolbar.setIcon(getDrawable(R.drawable.ic_person_black_24dp));
            recreate();
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
    }

    //Create DataBase if first time or not if not
    public void createDataBaseOwner() {

        //TODO createDataBaseOwner FUNCIONA PERO MEJORAR ESTA PARTE ??
        currentUser = mAuth.getCurrentUser();

        String name = "";
        if (currentUser.getDisplayName() != null) {
            name = currentUser.getDisplayName();
        } else {
            name = currentUser.getEmail();
        }
        String photo = null;
        if (currentUser.getPhotoUrl() != null) {
            photo = currentUser.getPhotoUrl().toString() + "?height=500";
        }

        final Owner newOwner = new Owner(currentUser.getUid(), name, currentUser.getEmail(), photo);

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
                        Toast.makeText(MainActivity.this, "Error Log In", Toast.LENGTH_SHORT).show();
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
        if (fragment instanceof FriendsFragment) {
            FriendsFragment friendsFragment = (FriendsFragment) fragment;
            friendsFragment.setFriendsInterface(this);
        }
        if (fragment instanceof SearchFragment) {
            SearchFragment searchFragment = (SearchFragment) fragment;
            searchFragment.setSearchInterface(this);
        }
        if (fragment instanceof MyPetsFragment) {
            MyPetsFragment myPetsFragment = (MyPetsFragment) fragment;
            myPetsFragment.setMyPetsInterface(this);
        }

    }

    //****CHAT FRAGMENT ****
    //GO to Chat with an specific owner
    @Override
    public void enterChat(String chatId) {
        showChat(chatId);
    }

    //Show Chat
    private void showChat(String chatId) {
        Log.d(TAG, "showChat: showing...");
        Bundle bundle = new Bundle();
        bundle.putString(ChatFragment.KEY_ID_CHAT, chatId);
        ChatFragment chatFragment = new ChatFragment();
        Log.d("FRAGMENT CREADO = ", "Chat");
        chatFragment.setArguments(bundle);
        fragments(chatFragment, ChatFragment.TAG);
    }

    //Check Database to see if the chat already exists
    public void checkChatExists(String userToChat) {

        CollectionReference myChatCol = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid()).collection(getString(R.string.collection_my_chats));

        Log.d(TAG, "checkChatExists: " + userToChat);

        myChatCol.document(userToChat).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d(TAG, "checkChatExists: result=yes");
                String idChat = (String) documentSnapshot.getData().get(getString(R.string.id_chat));
                showChat(idChat);
            } else {
                Log.d(TAG, "checkChatExists: result=no -> create");
                createChat(userToChat);
            }
        });

    }

    //If doesnt exists create one
    private void createChat(String userToChat) {
        Log.d(TAG, "createChat: Creating ...");
        //Room Chat collections create new Chat and get the ID
        CollectionReference chatCollection = db.collection(getString(R.string.collection_chats));
        DocumentReference chatRef = chatCollection.document();
        String idNewChat = chatRef.getId();

        Map<String, String> map = new HashMap<String, String>();
        map.put(getString(R.string.id_chat), idNewChat);

        Chat newChat = new Chat(idNewChat, userToChat, currentUser.getUid());

        chatRef.set(newChat);

        Log.d(TAG, "createChat: ChatRomm ID = " + idNewChat);

        //Create on the current user a document with the chatting and set the idchat
        CollectionReference myChatCol = db.collection(getString(R.string.collection_users))
                .document(currentUser.getUid()).collection(getString(R.string.collection_my_chats));
        myChatCol.document(userToChat).set(map);

        //Create the chat in the other user aswell
        CollectionReference otherChatCol = db.collection(getString(R.string.collection_users))
                .document(userToChat).collection(getString(R.string.collection_my_chats));
        otherChatCol.document(currentUser.getUid()).set(map);

        //Go To chat
        showChat(idNewChat);
    }

    //Go to chat from Friends Fragment
    @Override
    public void getChat(String userToChat) {
        checkChatExists(userToChat);
    }

    //****SEARCH FRAGMENT ****
    //Go to Chat from Search Fragment
    @Override
    public void chatFromSearch(String userToChat) {
        checkChatExists(userToChat);
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
        Log.d("FRAGMENT CREADO = ", "AddPet");
        //TODO Title stays fixed even when you go back
        actionBar.setTitle(getString(R.string.add_pet_title));
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
        //TODO: Check if its ok to call the ProfileFragment constructor every time
        ProfileFragment myProfileFragment = new ProfileFragment();
        myProfileFragment.setArguments(bundle);
        fragments(myProfileFragment,myProfileFragment.TAG);
    }
}
