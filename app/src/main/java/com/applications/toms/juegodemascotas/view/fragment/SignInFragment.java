package com.applications.toms.juegodemascotas.view.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.view.LogInActivity;
import com.applications.toms.juegodemascotas.view.SignInUpActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    private Context context;
    private Activity activity;
    private FrameLayout flSignin;
    private EditText etSigInPass;
    private EditText etSigIn;

    public SignInFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        flSignin = view.findViewById(R.id.flSignin);

        //Interface
        onSignInNotify signInNotify = (SignInFragment.onSignInNotify) getContext();

        //Atributos
        Button btnCreateAccountSignIn = view.findViewById(R.id.btnCreateAccountSignIn);
        Button btnRetrieve = view.findViewById(R.id.btnRetrieve);
        Button btnSignIn = view.findViewById(R.id.btnSignIn);
        ImageView ivVisiblePass = view.findViewById(R.id.ivVisiblePass);
        etSigInPass  = view.findViewById(R.id.etSigInPass);
        etSigIn = view.findViewById(R.id.etSigIn);

        //show password while touch image view
        ivVisiblePass.setOnTouchListener((v, event) -> {
            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    etSigInPass.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case MotionEvent.ACTION_UP:
                    etSigInPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etSigInPass.setSelection(etSigInPass.getText().toString().length());
                    break;
            }
            return true;
        });

        //Button SignIn to sign in
        btnSignIn.setOnClickListener(v -> {
            if (!etSigIn.getText().toString().equals("") && !etSigInPass.getText().toString().equals("")) {
                signInNotify.signIn(etSigIn.getText().toString(), etSigInPass.getText().toString());
            }
        });

        //Button to reset password if forgotten
        btnRetrieve.setOnClickListener(v -> {
            if (!etSigIn.getText().toString().equals("")){
                signInNotify.resetPass(etSigIn.getText().toString());
            }else {
                Snackbar.make(flSignin,getString(R.string.error_email_signin),Snackbar.LENGTH_SHORT).show();
            }
        });

        //go to sign up if enter into sign in by mistake
        btnCreateAccountSignIn.setOnClickListener(v -> {
            signInNotify.goSignUp();
            getActivity().getSupportFragmentManager().beginTransaction().remove(SignInFragment.this).commit();
        });

        return view;
    }

    //Metodos

    //Interface
    public interface onSignInNotify {
        void signIn(String email, String pass);
        void resetPass(String email);
        void goSignUp();
    }

}
