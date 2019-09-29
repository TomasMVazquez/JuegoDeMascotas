package com.applications.toms.juegodemascotas.view.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.view.SignInUpActivity;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private EditText etPassSigUp;
    private EditText etEmailSigUp;
    private TextInputLayout tiPassSignUp;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        onSignUpNotify signUpNotify = (SignUpFragment.onSignUpNotify) getContext();

        //Atributos
        Button btnSignInFromSignUp = view.findViewById(R.id.btnSignInFromSignUp);
        Button btnSignUp = view.findViewById(R.id.btnSignUp);
        ImageView ivVisiblePassUp = view.findViewById(R.id.ivVisiblePassUp);
        etPassSigUp = view.findViewById(R.id.etPassSigUp);
        etEmailSigUp = view.findViewById(R.id.etEmailSigUp);
        tiPassSignUp = view.findViewById(R.id.tiPassSignUp);

        //show pass on while touch
        ivVisiblePassUp.setOnTouchListener((v, event) -> {
            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    etPassSigUp.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case MotionEvent.ACTION_UP:
                    etPassSigUp.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassSigUp.setSelection(etPassSigUp.getText().toString().length());
                    break;
            }
            return true;
        });

        //Check Pass
        etPassSigUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPass(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Boton registrar
        btnSignUp.setOnClickListener(v -> {
            signUpNotify.signUp(etEmailSigUp.getText().toString(), etPassSigUp.getText().toString());
            etPassSigUp.setText("");
        });

        //go Sign In
        btnSignInFromSignUp.setOnClickListener(v -> {
            signUpNotify.goSignIn();
            getActivity().getSupportFragmentManager().beginTransaction().remove(SignUpFragment.this).commit();
        });
        return view;
    }

    //Metodo
    private void checkPass(CharSequence sequence){
        if (sequence.length()>5){
            tiPassSignUp.setError(getResources().getString(R.string.pass_error_2));
            String pass = sequence.toString();
            for (int i = 1; i < sequence.length(); i++) {
                char a = pass.charAt(i);
                if (isNumeric(String.valueOf(a))){
                    tiPassSignUp.setError("");
                    break;
                }
            }
        }else {
            tiPassSignUp.setError(getResources().getString(R.string.pass_error_1));
        }
    }

    private static boolean isNumeric(String inputData) {
        return inputData.matches("[-+]?\\d+(\\.\\d+)?");
    }

    //interface
    public interface onSignUpNotify{
        public void signUp(String email, String pass);
        public void goSignIn();
    }

}
