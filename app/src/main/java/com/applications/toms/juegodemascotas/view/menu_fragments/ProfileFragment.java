package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.OwnerController;
import com.applications.toms.juegodemascotas.controller.PetController;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.model.Pet;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.view.MainActivity;
import com.applications.toms.juegodemascotas.view.adapter.CirculeOwnerAdapter;
import com.applications.toms.juegodemascotas.view.adapter.CirculePetsAdapter;
import com.applications.toms.juegodemascotas.view.fragment.UpdateProfileFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.CircleShape;

/**
 * A simple {@link Fragment} subclass.
 */
public class    ProfileFragment extends Fragment implements UpdateProfileFragment.OnFragmentNotify,
        CirculePetsAdapter.AdapterInterfaceCircule, CirculeOwnerAdapter.AdapterInterfaceCirculeOwner,
        FragmentTitles {

    public static final String TAG = "ProfileFragment";
    private static final String SHOWCASE_ID = "simple profile";

    public static final String KEY_TYPE = "type";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_PET_ID = "pet_id";
    public static final int KEY_CAMERA_OWNER_PICTURE = 301;
    public static final int KEY_CAMERA_PET_PICTURE = 302;
    private static CirculePetsAdapter circulePetsAdapter;
    private static CirculeOwnerAdapter circuleOwnerAdapter;
    private static FirebaseUser currentUser;
    private ProfileFragmentListener profileFragmentListener;
    //Atributos
    private Activity activity;
    private Context context;
    private OwnerController ownerController;
    private PetController petController;
    private FirebaseStorage mStorage;
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
    private List<Pet> petsList = new ArrayList<Pet>();
    private String photoPetActual;
    private String photoOwnerActual;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        profileFragmentListener = (ProfileFragmentListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfile = view.findViewById(R.id.ivProfile);
        tvName = view.findViewById(R.id.tvName);
        tvDir = view.findViewById(R.id.tvDir);
        tvAboutProfile = view.findViewById(R.id.tvAboutProfile);
        tvMyPetsOwner = view.findViewById(R.id.tvMyPetsOwner);
        rvMyPetsOwner = view.findViewById(R.id.rvMyPetsOwner);
        fabImageProfile = view.findViewById(R.id.fabImageProfile);
        fabEditProfile = view.findViewById(R.id.fabEditProfile);

        context = getContext();
        activity = getActivity();

        //Controllers
        ownerController = new OwnerController();
        petController = new PetController();

        //Instance of FireBase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();

        //Adapter
        circulePetsAdapter = new CirculePetsAdapter(new ArrayList<>(), context, this);
        circuleOwnerAdapter = new CirculeOwnerAdapter(new ArrayList<>(), context, this);

        //Recycler View
        rvMyPetsOwner.hasFixedSize();
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvMyPetsOwner.setLayoutManager(llm);

        //intent and bundle
        Bundle bundle = getArguments();
        type = bundle.getString(KEY_TYPE);
        idUser = bundle.getString(KEY_USER_ID);
        petId = bundle.getString(KEY_PET_ID);

        //Get Owner Data
        fetchOwnerData(idUser);

        //edit btn for avatar
        fabImageProfile.setOnClickListener(v -> {
            if (type.equals("1")) {
                EasyImage.openChooserWithGallery(getActivity(), getResources().getString(R.string.take_profile_picture), KEY_CAMERA_OWNER_PICTURE);
            } else if (type.equals("2")) {
                EasyImage.openChooserWithGallery(getActivity(), getResources().getString(R.string.take_profile_pet_picture), KEY_CAMERA_PET_PICTURE);
            }
        });

        //edit btn profile info
        fabEditProfile.setOnClickListener(v -> {
            if (type.equals("1")) {
                UpdateProfileFragment updateProfileFragment = new UpdateProfileFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerProfile, updateProfileFragment);
                fragmentTransaction.commit();
            } else if (type.equals("2")) {
                deletePetProfile(idUser);
            }
        });

        presentShowcaseView(1000);

        return view;
    }

    @Override
    public void goToUserProfile(String keyType, String idOwner, String idPet) {
        profileFragmentListener.profileChange(keyType, idOwner, idPet);
    }

    @Override
    public void goToPetProfile(String keyType, String idOwner, String idPet) {
        profileFragmentListener.profileChange(keyType, idOwner, idPet);
    }

    //Actualizar el profile y salvarlo
    @Override
    public void saveAndCompleteProfileUpdates(String name, String dir, String birth, String sex, String about) {
        tvName.setText(name);
        tvDir.setText(dir);
        tvAboutProfile.setText(about);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        currentUser.updateProfile(profileUpdates);

        MainActivity.updateProfile(name, dir, birth, sex, about);
    }

    //Check if user is current user
    private boolean checkCurrentUser(String userId) {
        if (!currentUser.getUid().equals(userId)) {
            return false;
        }
        return true;
    }

    //Fetch Owner data
    private void fetchOwnerData(String userId) {
        ownerController.giveOwnerData(userId, context, resultado -> {
            //Check if user is current user
            if (checkCurrentUser(userId)) {
                //Traer info del usuario y botones para modificar
                initCurrentUser(resultado);
            } else {
                initOtherUser(resultado);
            }
        });

        petController.giveOwnerPets(petsList,userId, context, resultado -> {
            if(resultado != null) {
                petsList.addAll(resultado);
            }
        });

    }

    //Init profile user = current user
    private void initCurrentUser(Owner owner) {
        completeDataInProfile(owner);
    }

    //Init profile if is diferent user
    @SuppressLint("RestrictedApi")
    private void initOtherUser(Owner user) {
        //No mostramos botones para editar o elimiar
        fabImageProfile.setVisibility(View.GONE);
        fabEditProfile.setVisibility(View.GONE);

        completeDataInProfile(user);
    }

    //Bring Data to profile
    private void completeDataInProfile(Owner profileData) {
        if (type.equals("1")) {
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
            if (profileData.getAvatar() != null) {
                String[] avatar = profileData.getAvatar().split("=");
                if (avatar.length > 1) {
                    Glide.with(this).load(profileData.getAvatar()).into(ivProfile);
                } else {
                    ownerController.giveOwnerAvatar(profileData.getUserId(), profileData.getAvatar(), context, result -> {
                        Log.d(TAG, "completeDataInProfile: uri: " + result);
                        Glide.with(getActivity()).load(result).into(ivProfile);
                    });
                }
                photoOwnerActual = profileData.getAvatar();
            }

        } else if (type.equals("2")) {
            //If is a pet and Im the owner I can delete, this is the icon
            fabEditProfile.setImageDrawable(context.getDrawable(R.drawable.ic_delete_forever_white_24dp));
            //Text MyOwner or MyPets
            tvMyPetsOwner.setText(getResources().getString(R.string.my_owner));
            //Adapter
            rvMyPetsOwner.setAdapter(circuleOwnerAdapter);
            //Busco la mascota elegida
            for (Pet pet : petsList) {
                if (pet.getIdPet().equals(petId)) {
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
                    petController.givePetAvatar(profileData.getUserId(), pet.getFotoMascota(), context, result -> Glide.with(getActivity()).load(result).into(ivProfile));
                }
            }
        }
    }

    //Eliminar perfil (solo para mascotas)
    public void deletePetProfile(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmación");
        builder.setMessage("Por favor confirmar que usted quiere eliminar este perfil");
        builder.setCancelable(false);
        builder.setIcon(context.getDrawable(R.drawable.ic_delete_forever_white_24dp));
        builder.setPositiveButton("Si", (dialog, which) -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            MainActivity.deleteProfilePet(userId, petId);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) ->
                Toast.makeText(context, "El perfil NO se elimino", Toast.LENGTH_SHORT).show());

        builder.show();
    }

    //On Activity Result de las fotos
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
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
                            Glide.with(getActivity()).load(uri).into(ivProfile);
                            //Actualizar foto de Firebase User
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();
                            currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    //Cambiamos un metodo local por uno en el main
                                    MainActivity.updateProfilePicture(photoOwnerActual, nuevaFoto.getName(), uriTemp);
                                }
                            });

                            break;

                        case KEY_CAMERA_PET_PICTURE:
                            final StorageReference nuevaFotoPet = raiz.child(idUser).child(uriTemp.getLastPathSegment());
                            //Poner nueva foto
                            Glide.with(getActivity()).load(uri).into(ivProfile);
                            MainActivity.updatePhotoPet(idUser, petId, photoPetActual, uriTemp.getLastPathSegment(), uriTemp);

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

    @Override
    public int getFragmentTitle() {
        return R.string.my_profile;
    }

    public interface ProfileFragmentListener {
        void profileChange(String keyType, String idOwner, String petId);
    }

    private void presentShowcaseView(int withDelay) {
        new MaterialShowcaseView.Builder(activity)
                .setTarget(fabEditProfile)
                .setShape(new CircleShape())
                .setDismissText(context.getString(R.string.onboard_click))
                .setContentText(context.getString(R.string.onboard_profile_edit_fab))
                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .useFadeAnimation() // remove comment if you want to use fade animations for Lollipop & up
                .show();
    }
}
