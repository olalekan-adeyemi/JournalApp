package com.lakeside.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lakeside.journalapp.Auth.GoogleAuth;

public class SignInActivity extends GoogleAuth implements View.OnClickListener {

    private SignInButton mGoogleSignInBtn;
    public static final String TAG = SignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //setup all views
        mGoogleSignInBtn = (SignInButton) findViewById(R.id.googleSignInBtn);
        mGoogleSignInBtn.setSize(SignInButton.SIZE_WIDE);
        mGoogleSignInBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.googleSignInBtn) {
            showProgressDialog();
            //Check if internet is available before calling signIn. Please implement
            signIn();
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient) ;
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Handle the result of the sign in action by overriding onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                //With success it means sign in is successful, so next we authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);

            }else {
                //Google Sign in was not successful, we update the UI
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredentail:onComplete: " + task.isSuccessful());

                        if(!task.isSuccessful()){
                            Log.w(TAG, "signInWithCredentail " + task.getException());
                            hideProgressDialog();
                            Toast.makeText(SignInActivity.this, R.string.sign_in_failed, Toast.LENGTH_LONG).show();
                        }else {
                            finish();
                        }
                    }
                });

        hideProgressDialog();
    }
}
