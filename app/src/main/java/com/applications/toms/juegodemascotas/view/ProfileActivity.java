package com.applications.toms.juegodemascotas.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.OwnerController;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.ResultListener;
import com.applications.toms.juegodemascotas.view.adapter.CirculeOwnerAdapter;
import com.applications.toms.juegodemascotas.view.adapter.CirculePetsAdapter;
import com.applications.toms.juegodemascotas.view.fragment.UpdateProfileFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfileActivity extends AppCompatActivity implements UpdateProfileFragment.OnFragmentNotify,
        CirculePetsAdapter.AdapterInterfaceCircule, CirculeOwnerAdapter.AdapterInterfaceCirculeOwner {

    public static final String KEY_TYPE = "type";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_PET_ID = "pet_id";
    public static final int KEY_CAMERA_OWNER_PICTURE = 301;
    public static final int KEY_CAMERA_PET_PICTURE = 302;
    private static final String TAG = "PROFILE";

    //Atributos
    private static CirculePetsAdapter circulePetsAdapter;
    private static CirculeOwnerAdapter circuleOwnerAdapter;

    private OwnerController ownerController;
    private PetController petController;

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;

    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvDir;
    private TextView tvAboutProfile;
    private TextView tvMyPetsOwner;
    private RecyclerView rvMyPetsOwner;
    private FloatingActionButton fabImageProfile;
    private FloatingActionButton fabEditProfile;

    private String type;
    private String petId;
    private String idUser;
    private List<Pet> petsList = new ArrayList<>();
    private String photoPetActual;
    private String photoOwnerActual;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Controllers
        ownerController = new OwnerController();
        petController = new PetController();

        //Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();

        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvDir = findViewById(R.id.tvDir);
        tvAboutProfile = findViewById(R.id.tvAboutProfile);
        tvMyPetsOwner = findViewById(R.id.tvMyPetsOwner);
        rvMyPetsOwner = findViewById(R.id.rvMyPetsOwner);

        fabImageProfile = findViewById(R.id.fabImageProfile);
        fabEditProfile = findViewById(R.id.fabEditProfile);

        //Adapter
        circulePetsAdapter = new CirculePetsAdapter(new ArrayList<Pet>(),this,this);
        circuleOwnerAdapter = new CirculeOwnerAdapter(new ArrayList<Owner>(),this,this);

        //Recycler View
        rvMyPetsOwner.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rvMyPetsOwner.setLayoutManager(llm);

        //intent
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        type = bundle.getString(KEY_TYPE);
        idUser = bundle.getString(KEY_USER_ID);
        petId = bundle.getString(KEY_PET_ID);

        //Get Owner Data
        fetchOwnerData(idUser);

        //Boton de foto para cambiarla
        fabImageProfile.setOnClickListener(v -> {
            if (type.equals("1")) {
                EasyImage.openChooserWithGallery(ProfileActivity.this, getResources().getString(R.string.take_profile_picture), KEY_CAMERA_OWNER_PICTURE);
            }else if (type.equals("2")){
                EasyImage.openChooserWithGallery(ProfileActivity.this, getResources().getString(R.string.take_profile_pet_picture), KEY_CAMERA_PET_PICTURE);
            }
        });

        //Boton to edit profile
        fabEditProfile.setOnClickListener(v -> {
            if (type.equals("1")) {
                UpdateProfileFragment updateProfileFragment = new UpdateProfileFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerProfile, updateProfileFragment);
                fragmentTransaction.commit();
            }else if (type.equals("2")){
                deletePetProfile(idUser);
            }
        });

    }

    //Check if user is current user
    private boolean checkCurrentUser(String userId){
        if (!currentUser.getUid().equals(userId)){
           return false;
        }
        return true;
    }

    //Fetch Owner data
    private void fetchOwnerData(String userId){
        ownerController.giveOwnerData(userId, this, resultado -> {
            //Check if user is current user
            if (checkCurrentUser(userId)){
                //Traer info del usuario y botones para modificar
                initCurrentUser(resultado);
            }else {
                initOtherUser(resultado);
            }
        });

        petController.giveOwnerPets(userId,this,resultado -> {
            petsList.addAll(resultado);
        });

    }

    //Init profile user = current user
    private void initCurrentUser(Owner owner){
        completeDataInProfile(owner);
    }

    //Init profile if is diferent user
    @SuppressLint("RestrictedApi")
    private void initOtherUser(Owner user){
        //No mostramos botones para editar o elimiar
        fabImageProfile.setVisibility(View.GONE);
        fabEditProfile.setVisibility(View.GONE);

        completeDataInProfile(user);
    }

    //Bring Data to profile
    private void completeDataInProfile(Owner profileData){
        if (type.equals("1")){
            //Text MyOwner or MyPets
            tvMyPetsOwner.setText(getResources().getString(R.string.my_pets));
            //adaptador
            rvMyPetsOwner.setAdapter(circulePetsAdapter);
            //My Pets
            circulePetsAdapter.setPetList(petsList);
            //Name
            tvName.setText(profileData.getName());
            //info
            tvAboutProfile.setText(profileData.getAboutMe());
            tvDir.setText(profileData.getAddress());
            //Photo Profile
            if (profileData.getAvatar() != null ) {
                String [] avatar = profileData.getAvatar().split("=");
                if (avatar.length > 1){
                    Glide.with(this).load(profileData.getAvatar()).into(ivProfile);
                }else {
                    ownerController.giveOwnerAvatar(profileData.getUserId(), profileData.getAvatar(), this, result -> {
                        Log.d(TAG, "completeDataInProfile: uri: " + result);
                        Glide.with(ProfileActivity.this).load(result).into(ivProfile);
                    });
                }
                photoOwnerActual = profileData.getAvatar();
            }else {
                //TODO
            }

        }else if (type.equals("2")){
            //If is a pet and Im the owner I can delete, this is the icon
            fabEditProfile.setImageDrawable(getDrawable(R.drawable.ic_delete_forever_white_24dp));
            //Text MyOwner or MyPets
            tvMyPetsOwner.setText(getResources().getString(R.string.my_owner));
            //Adapter
            rvMyPetsOwner.setAdapter(circuleOwnerAdapter);
            //Busco la mascota elegida
            for (Pet pet : petsList){
                if (pet.getIdPet().equals(petId)){
                    //Name
                    tvName.setText(pet.getNombre());
                    //Info
                    String additionalInfo = pet.getRaza() + " - " + pet.getTamanio() + " - " + pet.getSexo();
                    tvDir.setText(additionalInfo);
                    tvAboutProfile.setText(pet.getInfoMascota());
                    //Adapter
                    List<Owner> ownerList = new ArrayList<>();
                    ownerList.add(profileData);
                    circuleOwnerAdapter.setOwner(ownerList);
                    //Photo
                    photoPetActual = pet.getFotoMascota();
                    petController.givePetAvatar(profileData.getUserId(),pet.getFotoMascota(),this,result -> Glide.with(ProfileActivity.this).load(result).into(ivProfile));
                }
            }
        }
    }

    //Actualizar el profile y salvarlo
    public void saveAndCompleteProfileUpdates(String name, String dir, String birth, String sex, String about){
        tvName.setText(name);
        tvDir.setText(dir);
        tvAboutProfile.setText(about);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        currentUser.updateProfile(profileUpdates);

        MainActivity.updateProfile( name,  dir,  birth,  sex,  about);
    }

    //Eliminar perfil (solo para mascotas)
    public void deletePetProfile(String userId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("Por favor confirmar que usted quiere eliminar este perfil");
        builder.setCancelable(false);
        builder.setIcon(this.getDrawable(R.drawable.ic_delete_forever_white_24dp));
        builder.setPositiveButton("Si", (dialog, which) -> {
            Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
            MainActivity.deleteProfilePet(userId, petId);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) ->
                Toast.makeText(getApplicationContext(), "El perfil NO se elimino", Toast.LENGTH_SHORT).show());

        builder.show();
    }

    //On Activity Result de las fotos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, ProfileActivity.this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource imageSource, int i) {

            }

            @Override
            public void onImagesPicked(@NonNull List<File> list, EasyImage.ImageSource imageSource, int i) {
                StorageReference raiz = mStorage.getReference();
                if (list.size() > 0) {
                    File file = list.get(0);
                    final Uri uri = Uri.fromFile(file);
                    final Uri uriTemp = Uri.fromFile(new File(uri.getPath()));

                    switch (i) {
                        case KEY_CAMERA_OWNER_PICTURE:
                            final StorageReference nuevaFoto = raiz.child(currentUser.getUid()).child(uriTemp.getLastPathSegment());

                            //Poner nueva foto
                            Glide.with(ProfileActivity.this).load(uri).into(ivProfile);
                            //Actualizar foto de Firebase User
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();
                            currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    //Cambiamos un metodo local por uno en el main
                                    MainActivity.updateProfilePicture(photoOwnerActual,nuevaFoto.getName(),uriTemp);
                                }
                            });

                            break;

                        case KEY_CAMERA_PET_PICTURE:
                            final StorageReference nuevaFotoPet = raiz.child(idUser).child(uriTemp.getLastPathSegment());
                            //Poner nueva foto
                            Glide.with(ProfileActivity.this).load(uri).into(ivProfile);
                            MainActivity.updatePhotoPet(idUser,petId,photoPetActual,uriTemp.getLastPathSegment(),uriTemp);

                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource, int i) {

            }
        });

    }

    //Ir al perfil de la fotito
    @Override
    public void goToUserProfile(String idOwner) {
        Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProfileActivity.KEY_TYPE,"1");
        bundle.putString(ProfileActivity.KEY_USER_ID,idOwner);
        bundle.putString(ProfileActivity.KEY_PET_ID,"0");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void goToPetProfile(String idOwner, String idPet) {
        Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProfileActivity.KEY_TYPE,"2");
        bundle.putString(ProfileActivity.KEY_USER_ID,idOwner);
        bundle.putString(ProfileActivity.KEY_PET_ID,idPet);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
