package com.wurfel.yaqeen.inst.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wurfel.yaqeen.inst.Models.User;
import com.wurfel.yaqeen.inst.R;
import com.wurfel.yaqeen.inst.utils.FieldShakerAnimation;

public class SigninActivity extends AppCompatActivity implements android.widget.EditText.OnFocusChangeListener {

    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPassword;
    TextView textViewForgotPassword;
    TextView textViewSignup;
    TextView textViewSignin;
    Button btnSigninSignup;

    Boolean signinFlag=true;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignInActivity";

    Dialog loadingDialog;

    FieldShakerAnimation shakerObj = new FieldShakerAnimation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }
        setContentView(R.layout.activity_signin);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        initializeControls();

        setListeners();

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus == false) {  // lost focus
            android.widget.EditText editText = (android.widget.EditText) v;
            editText.setSelection(0,0);
        }
    }

    void initializeControls(){
        editTextName = (EditText) findViewById(R.id.editText_name);
        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        textViewForgotPassword = (TextView) findViewById(R.id.textView_forgot_password);
        textViewSignup = (TextView) findViewById(R.id.textView_signup);
        textViewSignin = (TextView) findViewById(R.id.textView_signin);
        btnSigninSignup = (Button) findViewById(R.id.btn_signin_signup);
    }

    void setListeners(){
        editTextEmail.setOnFocusChangeListener(this);
        editTextName.setOnFocusChangeListener(this);
        editTextPassword.setOnFocusChangeListener(this);

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialogAddressShow("");
            }
        });

        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signinFlag) {
                    signinFlag = false;
                    editTextName.setVisibility(View.VISIBLE);
                    editTextName.requestFocus();
                    textViewForgotPassword.setVisibility(View.GONE);
                    btnSigninSignup.setText("SIGN UP");
                    textViewSignin.setVisibility(View.VISIBLE);
                    textViewSignup.setVisibility(View.GONE);
                    emptyAllEditTextFields();
                }
            }
        });

        textViewSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinFlag = true;
                editTextName.setVisibility(View.GONE);
                editTextEmail.requestFocus();
                textViewForgotPassword.setVisibility(View.VISIBLE);
                btnSigninSignup.setText("SIGN IN");
                textViewSignin.setVisibility(View.GONE);
                textViewSignup.setVisibility(View.VISIBLE);
                emptyAllEditTextFields();
            }
        });

        btnSigninSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(signinFlag==false && editTextName.getText().toString().length()<3){
                    Toast.makeText(SigninActivity.this, "Name should be at least 3 character long", Toast.LENGTH_SHORT).show();
                    shakerObj.startShaking(SigninActivity.this,editTextName);
                    editTextName.requestFocus();
                }else if(editTextEmail.getText().toString().equals("") || !isValidEmail(editTextEmail.getText().toString())){
                    Toast.makeText(SigninActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    shakerObj.startShaking(SigninActivity.this,editTextEmail);
                    editTextEmail.requestFocus();
                }else if((editTextPassword.getText().toString().length()<6) || (!checkContainsUpperCase(editTextPassword.getText().toString())) ||
                        (!containsDigit(editTextPassword.getText().toString()))){
                    Toast.makeText(SigninActivity.this, "Invalid Password: Password must contain a minimum of six characters " +
                            "with at least one uppercase letter and one number", Toast.LENGTH_LONG).show();
                    shakerObj.startShaking(SigninActivity.this,editTextPassword);
                    editTextPassword.requestFocus();
                }else {
                    if(signinFlag){
                        signIn();
                    }
                    else {
                        signUp();
                    }
                }
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean checkContainsUpperCase(String str){
        Boolean hasUppercase = !str.equals(str.toLowerCase());
        return hasUppercase;
    }

    public static boolean containsDigit(String str){
        Boolean hasDigit = str.matches(".*\\d+.*");
        return hasDigit;
    }

    void emptyAllEditTextFields(){
        editTextName.setText("");
        editTextPassword.setText("");
        editTextEmail.setText("");
    }

    void editDialogAddressShow(String preFill){

        MaterialDialog editDialogAddress = new MaterialDialog.Builder(SigninActivity.this)
//                .positiveText(R.string.agree)
                .title("Reset Password")
                .titleColor(getResources().getColor(R.color.colorMain))
                .widgetColor(getResources().getColor(R.color.colorMain))
                .positiveColor(getResources().getColor(R.color.colorMain))
                .negativeColor(getResources().getColor(R.color.colorMain))
                .positiveText("Ok")
                .negativeText("Cancel")
                .cancelable(false)
                .content("Please enter your E-Mail Address")
//                .inputRangeRes(6, 200, R.color.colorRed)
//                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .input("Email Address",/* pre fill */ preFill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        if(isValidEmail(input)) {
                            resetPassword(input.toString());
                        }
                        else{
                            Toast.makeText(SigninActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccessSignin(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");

        showLoadingDialoag(SigninActivity.this);
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        dismissLoadingDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccessSignin(task.getResult().getUser());
                        } else {
                            Toast.makeText(SigninActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");

        showLoadingDialoag(SigninActivity.this);
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
//                        dismissLoadingDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccessSignup(task.getResult().getUser());
                        } else {
                            dismissLoadingDialog();
                            Toast.makeText(SigninActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccessSignin(FirebaseUser user) {

        // Go to MainActivity
        startActivity(new Intent(SigninActivity.this, MainActivity.class));
        finish();
    }

    private void onAuthSuccessSignup(FirebaseUser user) {

//        // Write new user
//        writeNewUser(user.getUid(), username, user.getEmail());
        updateUserProfile(user);
    }

    void updateUserProfile(final FirebaseUser user){
        String username = editTextName.getText().toString();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dismissLoadingDialog();
                        if (task.isSuccessful()) {
                            onAuthSuccessSignin(user);
                        } else {
                            Toast.makeText(SigninActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void resetPassword(String emailAddress){

        showLoadingDialoag(SigninActivity.this);
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dismissLoadingDialog();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(SigninActivity.this, "An email has been sent to your account.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SigninActivity.this, "Failed to reset Password. Please enter Correct Email Address.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    void showLoadingDialoag(Context context){
        loadingDialog = new Dialog(context);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setContentView(R.layout.dialogbox_loading);
        final ImageView imgViewLoader = (ImageView) loadingDialog.findViewById(R.id.imageview_loader);
        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.clockwise_rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        imgViewLoader.startAnimation(rotation);

        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    void dismissLoadingDialog(){
//        handler.removeCallbacks(runnable);
        loadingDialog.dismiss();
    }

}
