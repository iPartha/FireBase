package com.example.ipartha.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int GOOGLE_SIGN_IN = 1;
    private static Context context=null;
    private final String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText editUserName;
    EditText editPwd;
    EditText editNewUserName;
    EditText editNewPassword;
    EditText editConfirmPassword;
    CallbackManager mCallbackManager;
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        editUserName = (EditText)findViewById(R.id.editUserName);
        editPwd      = (EditText)findViewById(R.id.editPwd);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                       AlertMsg.showToast(getContext(), "Login cancel.");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        AlertMsg.showToast(getContext(), exception.getMessage(), Toast.LENGTH_LONG);

                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                });

        ImageView facebookButton=(ImageView) findViewById(R.id.facebook);

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this,  this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        ImageView googleButton=(ImageView) findViewById(R.id.googleplus);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            }
        });


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void onClickForgotPwd(View v) {

        String email = editUserName.getText().toString();

        do {
            if (email.isEmpty()){
                AlertMsg.showToast(getContext(), "Enter valid email.");
                break;
            }
            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                          AlertMsg.showToast(getContext(), "EMail sent successfully.");
                        }


                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertMsg.showToast(getContext(), "Unable to sent EMail.");
                }

            });
        }while(false);
    }

    public void onClickNeedAccount(View v) {

        setContentView(R.layout.activity_register);

        editNewUserName = (EditText)findViewById(R.id.editNewUserName);
        editNewPassword =  (EditText)findViewById(R.id.editNewPwd);;
        editConfirmPassword =  (EditText)findViewById(R.id.editConfirmPwd);

    }

    public void onClickSignUp(View v) {

        do{

            if (editNewUserName.getText().toString().isEmpty()) {
//                AlertMsg msg = new AlertMsg(getContext(), getString(R.string.alert_msg_title), "Enter user name", null, null);
//                msg.showAlertMsg();
                AlertMsg.showToast(getContext(), "Enter user name.");
                break;
            }

            if (editNewPassword.getText().toString().isEmpty()) {
//                AlertMsg msg = new AlertMsg(getContext(), getString(R.string.alert_msg_title), "Enter password", null, null);
//                msg.showAlertMsg();
                AlertMsg.showToast(getContext(), "Enter password.");
                break;
            }

            if (editConfirmPassword.getText().toString().isEmpty()) {
//                AlertMsg msg = new AlertMsg(getContext(), getString(R.string.alert_msg_title), "Enter password", null, null);
//                msg.showAlertMsg();
                AlertMsg.showToast(getContext(), "Enter confirm password.");
                break;
            }

            if (!(editConfirmPassword.getText().toString().equals(editNewPassword.getText().toString()))) {
//                AlertMsg msg = new AlertMsg(getContext(), getString(R.string.alert_msg_title), "Password and confirm password doesn't match", null, null);
//                msg.showAlertMsg();
                AlertMsg.showToast(getContext(), "Password and confirm password doesn't match.");
                break;
            }

            signUp(editNewUserName.getText().toString(), editNewPassword.getText().toString());

        }while(false);

    }

    public void onClickSignIn(View v) {
        do{

            if (editUserName.getText().toString().isEmpty()) {
//                AlertMsg msg = new AlertMsg(getContext(), getString(R.string.alert_msg_title), "Enter user name", null, null);
//                msg.showAlertMsg();
                AlertMsg.showToast(getContext(), "Enter user name.");
                break;
            }

            if (editPwd.getText().toString().isEmpty()) {
//                AlertMsg msg = new AlertMsg(getContext(), getString(R.string.alert_msg_title), "Enter password", null, null);
//                msg.showAlertMsg();
                AlertMsg.showToast(getContext(), "Enter password.");
                break;
            }

            signIn(editUserName.getText().toString(), editPwd.getText().toString());

        }while(false);
    }

    public static Context getContext(){
        return context;
    }

    public void signUp(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            AlertMsg.showToast(getContext(), "Authentication failed.");
                        } else {
                            AlertMsg.showToast(getContext(), "Authentication success.");

                        }

                        // ...
                    }
                });
    }

    public void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            AlertMsg.showToast(getContext(), "Authentication failed.");
                        } else {
                            AlertMsg.showToast(getContext(), "Authentication success.");
                            startUserActivity();
                        }

                        // ...
                    }
                });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleAccessToken(result);
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            AlertMsg.showToast(getContext(), "Authentication failed.");
                        } else {
                           AlertMsg.showToast(getContext(), "Authentication success.");
                            startUserActivity();
                        }

                        // ...
                    }
                });
    }

    private void handleGoogleAccessToken(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleAccessToken:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            AlertMsg.showToast(getContext(), "Authentication success.");
            startUserActivity();

        } else {
            AlertMsg.showToast(getContext(), "Authentication failed.");
        }
    }

    private void startUserActivity() {
        Intent intent = new Intent(LoginActivity.this, UserView.class);
        startActivity(intent);
    }


}


