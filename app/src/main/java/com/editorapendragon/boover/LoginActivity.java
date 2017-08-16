package com.editorapendragon.boover;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.StringSearch;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private Button buttonRegister;
    private Button buttonSignIn;
    private SignInButton signInGoogleButton;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView txtEsqueciSenha;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 0x0;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestScopes(new Scope(Scopes.PLUS_LOGIN)).
                requestIdToken(getString(R.string.server_client_id)).
                requestEmail().
                build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                enableAutoManage(this, this).
                addApi(Auth.GOOGLE_SIGN_IN_API, gso).
                build();

        signInGoogleButton = (SignInButton) findViewById(R.id.btn_SignInGoogle);
        signInGoogleButton.setSize(SignInButton.SIZE_STANDARD);
        signInGoogleButton.setOnClickListener(this);


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.btn_SignInFacebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.setText("Entre com o Facebook");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                progressDialog.setMessage(getResources().getString(R.string.verificando_usuario));
                progressDialog.show();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException e) {
                // App code
            }
        });

        if (firebaseAuth.getCurrentUser() != null){
            finish();
            Intent secondActivity = new Intent(this, MainActivity.class);
            startActivity(secondActivity);
        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.btn_Register);
        buttonSignIn = (Button) findViewById(R.id.btn_SignIn);

        editTextEmail = (EditText) findViewById(R.id.txtEmail);
        editTextPassword = (EditText) findViewById(R.id.txtPassword);
        txtEsqueciSenha = (TextView) findViewById(R.id.txtesquecisenha);

        buttonRegister.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);
        txtEsqueciSenha.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent secondActivity = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(secondActivity);
                    finish();
                }
            }
        };

    }

    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,getResources().getString(R.string.digite_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,getResources().getString(R.string.digite_senha), Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(getResources().getString(R.string.registrando_usuario));
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent secondActivity = new Intent(getApplicationContext(), ActivityFotoUser.class);
                            startActivity(secondActivity);
                        }else{
                            Log.e("FIREBASE", "Sign-in Failed: " + task.getException().getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,getResources().getString(R.string.nao_foi_possivel_registrar), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getResources().getString(R.string.digite_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getResources().getString(R.string.digite_senha), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length()<6) {
            Toast.makeText(this, getResources().getString(R.string.tamanho_senha), Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(getResources().getString(R.string.verificando_usuario));
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            finish();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String name = user.getDisplayName();
                                if (name == null) {
                                    Intent secondActivity = new Intent(getApplicationContext(), ActivityFotoUser.class);
                                    startActivity(secondActivity);
                                } else {
                                    Intent secondActivity = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(secondActivity);
                                }
                            }
                        }else{
                            Log.e("FIREBASE", "Sign-in Failed: " + task.getException().getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_invalido), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            registerUser();
        }
        if (v == buttonSignIn) {
            userLogin();
        }
        if (v == signInGoogleButton){
            googlesignIn();
        }
        if (v == txtEsqueciSenha){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.ThemeDialogCustom);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            input.setWidth(1280);
            input.setHeight(150);
            input.setPadding(15,0,15,0);
            input.setTextColor(getResources().getColor(R.color.blue_main));
            input.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
            builder.setView(input);
            builder.setIcon(R.drawable.ic_boover_rounded);
            builder.setTitle(getResources().getString(R.string.atualizacao));
            builder.setMessage(getResources().getString(R.string.esqueci_senha_msg));
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = input.getText().toString();
                    if (emailAddress!=null && !emailAddress.isEmpty()) {
                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_enviado), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_invalido), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_invalido), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alerta = builder.create();
            alerta.show();
        }
    }

    private void googlesignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            progressDialog.setMessage(getResources().getString(R.string.verificando_usuario));
            progressDialog.show();
            if (acct!=null) {
                firebaseAuthWithGoogle(acct);
            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.erro_recuperar_credenciais),
                        Toast.LENGTH_SHORT).show();
                updateUI(false);
            }
            updateUI(true);
        } else {

            // Signed out, show unauthenticated UI.
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.falha_autenticacao) +"\n" +result.getStatus().toString(),
                    Toast.LENGTH_SHORT).show();
            updateUI(false);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Facebook", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Facebook", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Facebook", "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                        }

                        // ...
                    }
                });
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.btn_SignInGoogle).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.btn_SignInGoogle).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("GOOGLE", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
        firebaseAuth.addAuthStateListener(mAuthListener);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        if (acct.getIdToken()!=null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.falha_autenticacao),
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                    progressDialog.dismiss();
                                    Intent secondActivity = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(secondActivity);
                            }
                        }
                    });
        }
    }

}
