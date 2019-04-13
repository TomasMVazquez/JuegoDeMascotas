package com.applications.toms.juegodemascotas.view.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.applications.toms.juegodemascotas.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfileFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

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

        etUpdateBirth = view.findViewById(R.id.etUpdateBirth);
        etUpdateName = view.findViewById(R.id.etUpdateName);
        etUpdateDireccion = view.findViewById(R.id.etUpdateDireccion);
        etUpdateAbout = view.findViewById(R.id.etUpdateAbout);
        rbSexMasc = view.findViewById(R.id.rbSexMasc);
        rbSexFem = view.findViewById(R.id.rbSexFem);
        rbSexOtro = view.findViewById(R.id.rbSexOtro);

        etUpdateBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    showTruitonDatePickerDialog(v);
                }
            }
        });

//        etUpdateBirth.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        showTruitonDatePickerDialog(v);
//                        return true;
//                }
//                return false;
//            }
//        });

        RadioGroup radioGroup = view.findViewById(R.id.rgSex);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbSexMasc.isChecked()){
                    sex = getContext().getResources().getString(R.string.updateMasculino);
                }
                if (rbSexFem.isChecked()){
                    sex = getContext().getResources().getString(R.string.updateFemenino);
                }
                if (rbSexOtro.isChecked()){
                    sex = getContext().getResources().getString(R.string.updateOtro);
                }
            }
        });


        Button btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnFragmentNotify onFragmentFNotify= (OnFragmentNotify) getContext();
                onFragmentFNotify.saveAndCompleteProfileUpdates(etUpdateName.getText().toString(),
                        etUpdateDireccion.getText().toString(),
                        etUpdateBirth.getText().toString(),
                        sex,
                        etUpdateAbout.getText().toString());

                getActivity().getSupportFragmentManager().beginTransaction().remove(UpdateProfileFragment.this).commit();
            }
        });
        return view;
    }

    //interface comunication to activity
    public interface OnFragmentNotify{
        void saveAndCompleteProfileUpdates(String name, String dir, String birth, String sex, String about);
    }

    //Date picker
    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    //Date Picker Comands - for purchase date
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
