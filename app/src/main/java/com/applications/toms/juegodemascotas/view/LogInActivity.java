package com.applications.toms.juegodemascotas.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.applications.toms.juegodemascotas.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LogInActivity extends AppCompatActivity {

    public static final int GOOGLE_SIGN_IN = 103;

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    private TextInputLayout tiPassSignIn;
    private EditText etEmailSigIn;
    private EditText etPassSigIn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        TextInputLayout tiEmailSignIn = findViewById(R.id.tiEmailSignIn);
        tiPassSignIn = findViewById(R.id.tiPassSignIn);
        etEmailSigIn = findViewById(R.id.etEmailSigIn);
        etPassSigIn = findViewById(R.id.etPassSigIn);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnForgotPass = findViewById(R.id.btnForgotPass);
        Button btnRegister = findViewById(R.id.btnRegister);
        ImageButton ivPassVisible = findViewById(R.id.ivPassVisible);

//        SignInButton btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        LoginButton loginButton = findViewById(R.id.login_button_facebook);

        //revisar contrasenia
        etPassSigIn.addTextChangedListener(new TextWatcher() {
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

        //Boton visibilidad
        ivPassVisible.setOnTouchListener((v, event) -> {
            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    etPassSigIn.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case MotionEvent.ACTION_UP:
                    etPassSigIn.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassSigIn.setSelection(etPassSigIn.getText().toString().length());
                    break;
            }
            return true;
        });

        //Boton registrar
        btnRegister.setOnClickListener(v -> {
            createEmailAccess(etEmailSigIn.getText().toString(), etPassSigIn.getText().toString());
            etPassSigIn.setText("");
        });

        //Boton olvodar contrasena
        btnForgotPass.setOnClickListener(v -> {
            if (!etEmailSigIn.getText().toString().equals("")){
                resetEmailAccess(etEmailSigIn.getText().toString());
                etPassSigIn.setText("");
            }else {
                Toast.makeText(LogInActivity.this, "Ingrese su Email", Toast.LENGTH_SHORT).show();
            }
        });

        //Boton ingresar
        btnSignIn.setOnClickListener(v -> {
            if (!etEmailSigIn.getText().toString().equals("") && !etPassSigIn.getText().toString().equals("")) {
                handleEmailAccess(etEmailSigIn.getText().toString(), etPassSigIn.getText().toString());
            }
        });

        //SIGN IN CON Facebook
        loginButton.setReadPermissions("email", "public_profile");
        // Callback registration Facebook
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        //TODO SIGNIN GOOGLE

    }

    //Metodos
    public void checkPass(CharSequence sequence){
        if (sequence.length()>5){
            tiPassSignIn.setError(getResources().getString(R.string.pass_error_2));
            String pass = sequence.toString();
            for (int i = 1; i < sequence.length(); i++) {
                char a = pass.charAt(i);
                if (isNumeric(String.valueOf(a))){
                    tiPassSignIn.setError("");
                    break;
                }
            }
        }else {
            tiPassSignIn.setError(getResources().getString(R.string.pass_error_1));
        }
    }

    public static boolean isNumeric(String inputData) {
        return inputData.matches("[-+]?\\d+(\\.\\d+)?");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case GOOGLE_SIGN_IN:
                    Toast.makeText(LogInActivity.this, "Validando cuenta", Toast.LENGTH_SHORT).show();
                    break;
            }
        }else {
            updateUI(null);
        }
    }

    //Actualizar User Interface
    public void updateUI(final FirebaseUser user){
        if (user != null) {
            setResult(Activity.RESULT_OK);
            finish();

        }else {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    //Crear acceso con EMAIL
    private void createEmailAccess(final String email, final String pass){
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //Confirm Email access
                        resetEmailAccess(email);
                        //Update prifile name when creating
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(email)
                                .build();
                        mAuth.getCurrentUser().updateProfile(profileUpdates);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(LogInActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    //Reset pass con acceso con email
    private void resetEmailAccess(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(LogInActivity.this, getResources().getString(R.string.btn_register_toast), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Manejando el resultado del acceso con email
    private void handleEmailAccess(final String email, final String pass){
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(LogInActivity.this, getResources().getString(R.string.no_account_toast), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Manejando acceso con facebook
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        final FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        updateUI(null);
                    }
                });
    }

}
