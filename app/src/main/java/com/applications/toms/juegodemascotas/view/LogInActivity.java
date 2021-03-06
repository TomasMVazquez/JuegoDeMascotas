package com.applications.toms.juegodemascotas.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LogInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 103;
    public static final int KEY_SIGN_IN = 102;
    private static final String TAG = "LOGIN";
    public static final String KEY_MSG = "msg";

    private RelativeLayout rlLog;

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Calback for Facebook
        callbackManager = CallbackManager.Factory.create();
        //Instance for DataBase
        mAuth = FirebaseAuth.getInstance();

        rlLog = findViewById(R.id.rlLog);

        //Google Sign in
        SignInButton google_sig_in = findViewById(R.id.google_sig_in);
        google_sig_in.setSize(SignInButton.SIZE_STANDARD);

        //FaceBook Sign in
        LoginButton loginButton = findViewById(R.id.login_button_facebook);

        //Terminos y condiciones
        TextView txtTC2 = findViewById(R.id.txtTC2);
        txtTC2.setMovementMethod(LinkMovementMethod.getInstance());


        //email Sign In
        Button btnGoSignUp = findViewById(R.id.btnGoSignUp);
        Button btnGoSignIn = findViewById(R.id.btnGoSignIn);

        //If first time btn to sign up with email
        btnGoSignUp.setOnClickListener(v -> {
            Intent intentSignUp = new Intent(LogInActivity.this,SignInUpActivity.class);
            Bundle bundleSignUp = new Bundle();
            bundleSignUp.putString(SignInUpActivity.SIGN,getString(R.string.key_sign_up));
            intentSignUp.putExtras(bundleSignUp);
            startActivityForResult(intentSignUp,KEY_SIGN_IN);
        });

        //If already user then sign in with email
        btnGoSignIn.setOnClickListener(v -> {
            Intent intentSignIn = new Intent(LogInActivity.this,SignInUpActivity.class);
            Bundle bundleSignIn = new Bundle();
            bundleSignIn.putString(SignInUpActivity.SIGN,getString(R.string.key_sign_in));
            intentSignIn.putExtras(bundleSignIn);
            startActivityForResult(intentSignIn,KEY_SIGN_IN);
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

        //SIGNIN GOOGLE
        google_sig_in.setOnClickListener(v -> signIn());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    //Metodos

    //Activity result GOOGLE SIGN IN
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case RC_SIGN_IN:
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                    break;
                case KEY_SIGN_IN:
                    updateUI(mAuth.getCurrentUser());
                    break;
            }
        }else {
            if (requestCode != KEY_SIGN_IN) {
                updateUI(null);
            }
        }
    }

    //Update User Interface and setResult for the Intent for result fron the main
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

    //Handle facebook access
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

    //handle Google access
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //handle Google access result
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    //handle firebase Google access credentials
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Snackbar.make(rlLog,getString(R.string.error_authentication),Snackbar.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
