package com.applications.toms.juegodemascotas.view.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.util.Keys;
import com.applications.toms.juegodemascotas.view.MainActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import pl.aprilapps.easyphotopicker.EasyImage;

/*
 * A simple {@link Fragment} subclass.
 */
public class AddPetFragment extends Fragment implements FragmentTitles {

    public static final String TAG = "AddPetFragment";
    private static final int KEY_CAMERA_PET = 301;

    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;

    private String size="";
    private String sex="";
    private ImageView ivAddPetPhoto;
    private String photo="";
    private EditText etAddPetName;
    private static EditText etAddPetBirth;
    private EditText etAddPetRaza;
    private EditText etAddPetInfo;
    private Boolean uploadingPhoto;
    private LinearLayout llAddPet;

    public AddPetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_pet, container, false);
        //instances from DataBase
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //view from layout
        etAddPetName = view.findViewById(R.id.etAddPetName);
        etAddPetBirth = view.findViewById(R.id.etAddPetBirth);
        etAddPetRaza = view.findViewById(R.id.etAddPetRaza);
        etAddPetInfo = view.findViewById(R.id.etAddPetInfo);
        llAddPet = view.findViewById(R.id.llAddPet);

        //Birth date picker
        etAddPetBirth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                showTruitonDatePickerDialog();
            }
        });

        //Spinner to select the size of the Dog
        Spinner spinnerAddPetSize = view.findViewById(R.id.spinnerAddPetSize);
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getResources().getString(R.string.add_pet_size));
        spinnerArray.add(getResources().getString(R.string.spinner_small));
        spinnerArray.add(getResources().getString(R.string.spinner_medium));
        spinnerArray.add(getResources().getString(R.string.spinner_large));
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddPetSize.setAdapter(adapterSpinner);
        spinnerAddPetSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                size =  parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Genre Radio Btn
        final RadioButton rbAddPetSexMasc = view.findViewById(R.id.rbAddPetSexMasc);
        final RadioButton rbAddPetSexFem = view.findViewById(R.id.rbAddPetSexFem);

        RadioGroup radioGroup = view.findViewById(R.id.rgAddPetSex);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (rbAddPetSexMasc.isChecked()){
                sex = Objects.requireNonNull(getContext()).getResources().getString(R.string.add_pet_sex_m);
            }
            if (rbAddPetSexFem.isChecked()){
                sex = Objects.requireNonNull(getContext()).getResources().getString(R.string.add_pet_sex_h);
            }
        });

        //Pet Avatar
        ivAddPetPhoto = view.findViewById(R.id.ivAddPetPhoto);
        ivAddPetPhoto.setOnClickListener(v -> addPetAvatar());
        FloatingActionButton fabAddPetPhoto = view.findViewById(R.id.fabAddPetPhoto);
        fabAddPetPhoto.setOnClickListener(v -> addPetAvatar());

        //Btn Add pet
        Button btnAddPet = view.findViewById(R.id.btnAddPet);
        btnAddPet.setOnClickListener(v -> {
            hideSoftKeyboard(view);
            if (checkCompleteData()){
                MainActivity.addPetToDataBase(etAddPetName.getText().toString(),etAddPetRaza.getText().toString(),size,etAddPetBirth.getText().toString(),sex,photo,etAddPetInfo.getText().toString());
                getActivity().getSupportFragmentManager().beginTransaction().remove(AddPetFragment.this).commit();
            } else {
                Snackbar.make(llAddPet,getString(R.string.error_missing_data),Snackbar.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    //Esconder el teclado
    private void hideSoftKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Add Pet Avatar
    private void addPetAvatar(){
        EasyImage.openChooserWithGallery(AddPetFragment.this,"Elige la mejor foto", KEY_CAMERA_PET);
    }

    //Verification that all data is complete
    public Boolean checkCompleteData(){

        if (etAddPetName.getText().equals("")){
            return false;
        }
        if (etAddPetBirth.getText().equals("")){
            return false;
        }
        if (etAddPetRaza.getText().equals("")){
            return false;
        }
        if (etAddPetInfo.getText().equals("")){
            return false;
        }
        if (sex.equals("")){
            return false;
        }
        if (size.equals("")){
            return false;
        }
        if (photo.equals("")){
            return false;
        }
        return true;
    }

    //Avatar image Activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final StorageReference raiz = mStorage.getReference();

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource imageSource, int i) {

            }

            @Override
            public void onImagesPicked(@NonNull List<File> list, EasyImage.ImageSource imageSource, int i) {
                if (list.size() > 0) {
                    File file = list.get(0);
                    final Uri uri = Uri.fromFile(file);
                    final Uri uriTemp = Uri.fromFile(new File(uri.getPath()));

                    switch (i) {
                        case KEY_CAMERA_PET:
                            Glide.with(AddPetFragment.this).load(uri).into(ivAddPetPhoto);
                            photo = uriTemp.getLastPathSegment();

                            final StorageReference mascRef = raiz.child(mAuth.getCurrentUser().getUid()).child(uriTemp.getLastPathSegment());
                            UploadTask uploadTask = mascRef.putFile(uriTemp);

                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()){
                                        throw task.getException();
                                    }

                                    return mascRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()){
                                        Uri downloadUri = task.getResult();
                                        photo = downloadUri.toString();
                                    }else {
                                        Snackbar.make(llAddPet,getString(R.string.error_image_db),Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(llAddPet,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource, int i) {

            }
        });

    }

    //Date picker
    public void showTruitonDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public int getFragmentTitle() {
        return R.string.add_pet_title;
    }

    //Date Picker Comands
    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String dateChoosen=(dayOfMonth + "/" + (month + 1) + "/" + year);
            etAddPetBirth.setText(dateChoosen);
        }
    }

}
