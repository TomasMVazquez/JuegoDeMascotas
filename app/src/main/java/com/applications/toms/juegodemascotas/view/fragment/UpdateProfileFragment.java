package com.applications.toms.juegodemascotas.view.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.controller.OwnerController;
import com.applications.toms.juegodemascotas.model.Owner;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private static EditText etUpdateBirth;
    private EditText etUpdateName;
    private EditText etUpdateDireccion;
    private EditText etUpdateAbout;
    private String sex;
    private RadioButton rbSexMasc;
    private RadioButton rbSexFem;
    private RadioButton rbSexOtro;

    public UpdateProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);

        //Instances from DataBase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Views from Layout
        etUpdateBirth = view.findViewById(R.id.etUpdateBirth);
        etUpdateName = view.findViewById(R.id.etUpdateName);
        etUpdateDireccion = view.findViewById(R.id.etUpdateDireccion);
        etUpdateAbout = view.findViewById(R.id.etUpdateAbout);
        rbSexMasc = view.findViewById(R.id.rbSexMasc);
        rbSexFem = view.findViewById(R.id.rbSexFem);
        rbSexOtro = view.findViewById(R.id.rbSexOtro);

        //Check current info from Database
        checkDataBaseInfo(currentUser.getUid());

        //If the login had no name then empty
        if (!currentUser.getDisplayName().equals("")){
            etUpdateName.setText(currentUser.getDisplayName());
        }

        //Birth date
        etUpdateBirth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                showTruitonDatePickerDialog(v);
            }
        });

        //genre
        RadioGroup radioGroup = view.findViewById(R.id.rgSex);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (rbSexMasc.isChecked()){
                sex = Objects.requireNonNull(getContext()).getString(R.string.updateMasculino);
            }
            if (rbSexFem.isChecked()){
                sex = Objects.requireNonNull(getContext()).getString(R.string.updateFemenino);
            }
            if (rbSexOtro.isChecked()){
                sex = Objects.requireNonNull(getContext()).getString(R.string.updateOtro);
            }
        });


        //Boton Update
        Button btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnUpdateProfile.setOnClickListener(v -> {
            OnFragmentNotify onFragmentFNotify= (OnFragmentNotify) getContext();
            onFragmentFNotify.saveAndCompleteProfileUpdates(etUpdateName.getText().toString(),
                    etUpdateDireccion.getText().toString(),
                    etUpdateBirth.getText().toString(),
                    sex,
                    etUpdateAbout.getText().toString());

            getActivity().getSupportFragmentManager().beginTransaction().remove(UpdateProfileFragment.this).commit();
        });
        return view;
    }

    //interface comunication to activity
    public interface OnFragmentNotify{
        void saveAndCompleteProfileUpdates(String name, String dir, String birth, String sex, String about);
    }

    //CheckDataBaseInfo
    public void checkDataBaseInfo(final String userID){
        OwnerController ownerController = new OwnerController();
        ownerController.giveOwnerData(userID,getContext(),resultado -> {
            if (resultado.getSex()!=null) {
                if (resultado.getSex().equals(getContext().getString(R.string.updateMasculino))){
                    rbSexMasc.setChecked(true);
                }
                if (resultado.getSex().equals(getContext().getString(R.string.updateFemenino))){
                    rbSexFem.setChecked(true);
                }
                if (resultado.getSex().equals(getContext().getString(R.string.updateOtro))){
                    rbSexOtro.setChecked(true);
                }
            }
            if (resultado.getBirthDate()!=null){
                etUpdateBirth.setText(resultado.getBirthDate());
            }
            if (resultado.getAddress()!=null){
                etUpdateDireccion.setText(resultado.getAddress());
            }
            if (resultado.getAboutMe()!=null){
                etUpdateAbout.setText(resultado.getAboutMe());
            }
        });
    }


    //TODO UpdateProfileFragment new DATE PICKER??
    //Date picker
    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
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
            etUpdateBirth.setText(dateChoosen);
        }
    }

}
