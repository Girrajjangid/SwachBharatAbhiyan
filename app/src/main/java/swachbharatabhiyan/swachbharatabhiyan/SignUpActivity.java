package swachbharatabhiyan.swachbharatabhiyan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText email,password;
    String valid_email,valid_password;
    private FirebaseAuth Authentication;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email   = findViewById(R.id.sign_up_email);
        password= findViewById(R.id.signup_password);
        Authentication = FirebaseAuth.getInstance();
    }

    public void register(View view) {
        valid_email = email.getText().toString().trim();
        valid_password = password.getText().toString().trim();
        if (!isConnected2()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        } else if (valid_email.isEmpty() || !isEmailValid(valid_email)) {
            alertDialog("Invalid Email Address");
        } else if (valid_password.isEmpty() || valid_password.length() < 5) {
            password.setError("at least 6 character");
            alertDialog("Invalid Password");
        } else {
            dialog = ProgressDialog.show(this,"Processing...","Please wait...",false,false);
            Authentication.createUserWithEmailAndPassword(valid_email, valid_password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        String user_id = Authentication.getCurrentUser().getUid();
                        DatabaseReference current_user_db    = firebaseDatabase.getReference("Users").child("Drivers").child(user_id);
                        current_user_db.setValue(true);
                        dialog.dismiss();
                        try{
                            Intent intent = new Intent(SignUpActivity.this, DriverMapsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
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
