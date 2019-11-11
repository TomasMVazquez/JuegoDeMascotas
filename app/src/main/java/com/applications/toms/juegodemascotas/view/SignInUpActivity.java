package com.applications.toms.juegodemascotas.view;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.view.fragment.SignInFragment;
import com.applications.toms.juegodemascotas.view.fragment.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignInUpActivity extends LogInActivity implements SignInFragment.onSignInNotify, SignUpFragment.onSignUpNotify {

    public static final String SIGN = "sign";

    private static FirebaseAuth mAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);
        //Get application context
        context = getApplicationContext();
        //Firebase Instance for DataBase
        mAuth = FirebaseAuth.getInstance();

        //Get extras from the intent to understand if we need to create Sign in Fragment or Sign up fragment
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //Result from the bundle and creation of the fragment
        if (bundle.getString(SIGN).equals(getString(R.string.key_sign_in))){
            SignInFragment signInFragment = new SignInFragment();
            createFragment(signInFragment);
        }else if (bundle.getString(SIGN).equals(getString(R.string.key_sign_up))){
            SignUpFragment signUpFragment = new SignUpFragment();
            createFragment(signUpFragment);
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setSignInUpResult(false);
    }

    //Metodos

    //Create Fragment
    private void createFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameSignInUp,fragment);
        fragmentTransaction.commit();
    }

    //Handle email Access
    private void handleEmailAccess(final String email, final String pass){
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        setSignInUpResult(true);
                    } else {
                        Toast.makeText(SignInUpActivity.this, context.getString(R.string.no_account_toast), Toast.LENGTH_SHORT).show();
                        setSignInUpResult(false);
                    }
                });
    }

    //Reset pass with email access
    private void resetEmailAccess(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(SignInUpActivity.this, context.getString(R.string.btn_register_toast), Toast.LENGTH_SHORT).show();
                        setSignInUpResult(false);
                    }
                });
    }

    //Create Account with EMAIL
    private void createEmailAccess(final String email, final String pass){
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //Update prifile name when creating
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(email)
                                .build();

                        mAuth.getCurrentUser().updateProfile(profileUpdates);

                        setSignInUpResult(true);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SignInUpActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    //Set result for the intent for result from the login
    private void setSignInUpResult(Boolean result){
        if (result){
            setResult(Activity.RESULT_OK);
            finish();
        }else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    //Interfaces from the Sign in
    //Method to sign in
    @Override
    public void signIn(String email, String pass) {
        handleEmailAccess(email,pass);
    }

    //Reset my pass if forgot it
    @Override
    public void resetPass(String email) {
        resetEmailAccess(email);
    }

    //if we went to sing in by mistake and want to change to sign up
    @Override
    public void goSignUp() {
        SignUpFragment signUpFragment = new SignUpFragment();
        createFragment(signUpFragment);
    }

    //Interfaces from the Sign up
    //Method to sign up
    @Override
    public void signUp(String email, String pass) {
        createEmailAccess(email,pass);
    }

    //if we went to sing up by mistake and want to change to sign in
    @Override
    public void goSignIn() {
        SignInFragment signInFragment = new SignInFragment();
        createFragment(signInFragment);
    }


}
