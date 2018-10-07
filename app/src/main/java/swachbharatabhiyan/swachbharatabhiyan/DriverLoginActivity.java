package swachbharatabhiyan.swachbharatabhiyan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DriverLoginActivity extends AppCompatActivity {

    EditText ET_email, ET_password;
    private FirebaseAuth Authentication;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    Animation anim_logo, anim_email_pass, anim_login_sign, anim_version;
    ImageView anim1;
    RelativeLayout anim2;
    LinearLayout anim3;
    //SharedPreferences prefs;
    //public static final String preference = "UserData";
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        ET_email = findViewById(R.id.login_email);
        ET_password = findViewById(R.id.login_password);

        anim1 = findViewById(R.id.logo);
        anim2 = findViewById(R.id.relative_layout_1);
        anim3 = findViewById(R.id.linear_layout1);
        anim_version = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation0);
        anim_logo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation1);
        anim_email_pass = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation2);
        anim_login_sign = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation3);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                anim2.setVisibility(View.VISIBLE);
                anim3.setVisibility(View.VISIBLE);
                anim1.startAnimation(anim_logo);
                anim2.startAnimation(anim_email_pass);
                anim3.startAnimation(anim_login_sign);
            }
        }, 3000);

        Authentication = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        Authentication.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Authentication.removeAuthStateListener(firebaseAuthListener);
    }

    public void logIn(View view) {
        final String email = ET_email.getText().toString();
        final String password = ET_password.getText().toString();
        if (!isConnected2()) {
            Toast.makeText(DriverLoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty() || !isEmailValid(email)) {
            alertDialog("Invalid Email Address");
        } else if (password.isEmpty() || password.length() < 5) {
            ET_password.setError("at least 6 character");
            alertDialog("Invalid Password");
        } else {
            loading = ProgressDialog.show(this, "Processing...", "Please wait...", false, false);
            Authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        loading.dismiss();
                        alertDialog("Wrong Email or Password");
                    }
                    loading.dismiss();
                }
            });
        }
    }


    public void signUp(View view) {
        startActivity(new Intent(DriverLoginActivity.this, SignUpActivity.class));
    }

    public void forgetPassword(View view) {
        Toast.makeText(this, "forget password", Toast.LENGTH_SHORT).show();
    }

    boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void alertDialog(String mess) {
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        aBuilder.setMessage(mess);
        aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).create().show();
    }

    public boolean isConnected2() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isAvailable();

    }
}