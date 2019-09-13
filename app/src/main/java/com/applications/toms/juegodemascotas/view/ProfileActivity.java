package com.applications.toms.juegodemascotas.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;

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
import com.applications.toms.juegodemascotas.model.Duenio;
import com.applications.toms.juegodemascotas.model.Mascota;
import com.applications.toms.juegodemascotas.view.adapter.CirculeOwnerAdapter;
import com.applications.toms.juegodemascotas.view.adapter.CirculePetsAdapter;
import com.applications.toms.juegodemascotas.view.fragment.UpdateProfileFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileActivity extends AppCompatActivity implements UpdateProfileFragment.OnFragmentNotify,
        CirculePetsAdapter.AdapterInterfaceCircule, CirculeOwnerAdapter.AdapterInterfaceCirculeOwner {

    public static final String KEY_TYPE = "type";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_PET_ID = "pet_id";
    public static final int KEY_CAMERA_OWNER_PICTURE = 301;
    public static final int KEY_CAMERA_PET_PICTURE = 302;

    //Atributos
    private static CirculePetsAdapter circulePetsAdapter;
    private static CirculeOwnerAdapter circuleOwnerAdapter;

    private FirebaseStorage mStorage;
    private static FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvDir;
    private TextView tvAboutProfile;
    private TextView tvMyPetsOwner;
    private RecyclerView rvMyPetsOwner;

    private String type;
    private String userId;
    private String petId;
    private String photoPetActual;
    private String photoOwnerActual;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();

        db = FirebaseFirestore.getInstance();

        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvDir = findViewById(R.id.tvDir);
        tvAboutProfile = findViewById(R.id.tvAboutProfile);
        tvMyPetsOwner = findViewById(R.id.tvMyPetsOwner);
        rvMyPetsOwner = findViewById(R.id.rvMyPetsOwner);

        FloatingActionButton fabImageProfile = findViewById(R.id.fabImageProfile);
        FloatingActionButton fabEditProfile = findViewById(R.id.fabEditProfile);

        //intent
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        type = bundle.getString(KEY_TYPE);
        userId = bundle.getString(KEY_USER_ID);
        petId = bundle.getString(KEY_PET_ID);

        //Adapter
        circulePetsAdapter = new CirculePetsAdapter(new ArrayList<Mascota>(),this,this);
        circuleOwnerAdapter = new CirculeOwnerAdapter(new ArrayList<Duenio>(),this,this);

        //Recycler View
        rvMyPetsOwner.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rvMyPetsOwner.setLayoutManager(llm);


        //Check Pet or Owner
        if (type.equals("1")){
            tvMyPetsOwner.setText(getResources().getString(R.string.my_pets));
            fetchOwnerProfile(currentUser);
            //adaptador
            rvMyPetsOwner.setAdapter(circulePetsAdapter);
            //TODO que pasa si quiere ver el profile de otro usuario
        }else if (type.equals("2")){
            fabEditProfile.setImageDrawable(getDrawable(R.drawable.ic_delete_forever_white_24dp));
            tvMyPetsOwner.setText(getResources().getString(R.string.my_owner));
            fetchMascota(userId,petId);
            fetchOwner(userId);
            rvMyPetsOwner.setAdapter(circuleOwnerAdapter);
        }else if (type.equals("3")){
            tvMyPetsOwner.setText(getResources().getString(R.string.my_owner));
            fabEditProfile.setVisibility(View.GONE);
            fabImageProfile.hide();
            fetchMascota(userId,petId);
            fetchOwner(userId);
            rvMyPetsOwner.setAdapter(circuleOwnerAdapter);
        }



        //Boton de foto para cambiarla
        fabImageProfile.setOnClickListener(v -> {
            if (type.equals("1")) {
                EasyImage.openChooserWithGallery(ProfileActivity.this, getResources().getString(R.string.take_profile_picture), KEY_CAMERA_OWNER_PICTURE);
            }else if (type.equals("2")){
                EasyImage.openChooserWithGallery(ProfileActivity.this, getResources().getString(R.string.take_profile_pet_picture), KEY_CAMERA_PET_PICTURE);
            }
        });

        //Boton to edit profile
        fabEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("1")) {
                    UpdateProfileFragment updateProfileFragment = new UpdateProfileFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerProfile, updateProfileFragment);
                    fragmentTransaction.commit();
                }else if (type.equals("2")){
                    deletePetProfile();
                }
            }
        });

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
    public void deletePetProfile(){
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

    //Buscar datos del perfil del Duenio
    public void fetchOwnerProfile(FirebaseUser user){
        if (user.getPhotoUrl()!=null) {
            String photo = user.getPhotoUrl().toString() + "?height=500";
            Glide.with(this).load(photo).into(ivProfile);
        }else {
            //todo
        }
        tvName.setText(user.getDisplayName());
        checkDataBaseInfo(currentUser.getUid());

        final CollectionReference userRefMasc = db.collection(getResources().getString(R.string.collection_users))
                .document(currentUser.getUid()).collection("misMascotas");

        userRefMasc.addSnapshotListener((queryDocumentSnapshots, e) -> {
            List<Mascota> misMascotas = new ArrayList<>();
            misMascotas.addAll(queryDocumentSnapshots.toObjects(Mascota.class));
            circulePetsAdapter.setMascotaList(misMascotas);
        });

        DocumentReference userRef = db.collection(getResources().getString(R.string.collection_users))
                .document(currentUser.getUid());
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            Duenio duenio = documentSnapshot.toObject(Duenio.class);
            photoOwnerActual = duenio.getFotoDuenio();
        });

    }

    //Buscar datos de la mascota
    public void fetchMascota(final String idOwner, final String idPet){
        DocumentReference mascRef = db.collection(getResources().getString(R.string.collection_users))
                .document(idOwner).collection("misMascotas").document(idPet);

        mascRef.get().addOnSuccessListener(documentSnapshot -> {
            Mascota mascota = documentSnapshot.toObject(Mascota.class);
            photoPetActual = mascota.getFotoMascota();
            StorageReference storageReference = mStorage.getReference().child(idOwner).child(mascota.getFotoMascota());
            storageReference.getDownloadUrl()
                    .addOnSuccessListener(uri ->
                    Glide.with(ProfileActivity.this).load(uri).into(ivProfile))
                    .addOnFailureListener(e ->
                            Glide.with(ProfileActivity.this).load(getDrawable(R.drawable.shadow_dog)).into(ivProfile));
            tvName.setText(mascota.getNombre());
            String additionalInfo = mascota.getRaza() + " - " + mascota.getTamanio() + " - " + mascota.getSexo();
            tvDir.setText(additionalInfo);
            tvAboutProfile.setText(mascota.getInfoMascota());
        });

    }

    //Buscar Duenio
    public void fetchOwner(final String idOwner){
        DocumentReference userRef = db.collection(getResources().getString(R.string.collection_users))
                .document(idOwner);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            Duenio duenio = documentSnapshot.toObject(Duenio.class);
            List<Duenio> miDuenio = new ArrayList<>();
            miDuenio.add(duenio);
            circuleOwnerAdapter.setDuenio(miDuenio);
        });

    }

    //CheckDataBaseInfo
    public void checkDataBaseInfo(final String userID){
        final DocumentReference userRef = db.collection(getString(R.string.collection_users))
                .document(userID);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Duenio duenio = document.toObject(Duenio.class);
                    if (duenio.getDireccion()!=null){
                        tvDir.setText(duenio.getDireccion());
                    }else {
                        tvDir.setText(getResources().getString(R.string.address_profile));
                    }
                    if (duenio.getInfoDuenio()!=null){
                        tvAboutProfile.setText(duenio.getInfoDuenio());
                    }else {
                        tvAboutProfile.setText(getResources().getString(R.string.about_profile));
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "No Existe", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Task Unsuccesful", Toast.LENGTH_SHORT).show();
            }
        });

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
                            currentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Cambiamos un metodo local por uno en el main
                                        MainActivity.updateProfilePicture(photoOwnerActual,nuevaFoto.getName(),uriTemp);
                                    }
                                }
                            });

                            break;

                        case KEY_CAMERA_PET_PICTURE:
                            final StorageReference nuevaFotoPet = raiz.child(userId).child(uriTemp.getLastPathSegment());
                            //Poner nueva foto
                            Glide.with(ProfileActivity.this).load(uri).into(ivProfile);
                            MainActivity.updatePhotoPet(userId,petId,photoPetActual,uriTemp.getLastPathSegment(),uriTemp);

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
    public void goToProfile(String idOwner,String idPet) {
        
        if (type.equals("1")) {
            Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ProfileActivity.KEY_TYPE,"2"); //TODO POR AHI ACA PONER 3 PARA CUANDO HAGA click en el duenio vaya goback
            bundle.putString(ProfileActivity.KEY_USER_ID,idOwner);
            bundle.putString(ProfileActivity.KEY_PET_ID,idPet);
            intent.putExtras(bundle);
            startActivity(intent);
        }else if (type.equals("2")){
            Toast.makeText(this, "En construccion", Toast.LENGTH_SHORT).show();
        }
    }
}
