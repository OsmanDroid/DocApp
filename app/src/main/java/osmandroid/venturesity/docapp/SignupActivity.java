package osmandroid.venturesity.docapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    EditText emailET, passwordET;
    Button signup,signin,forgot;

    FirebaseAuth mAuth;

    MyProgressDialog progressDialog;

    private final static String TAG = "TAG";

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();



        emailET = findViewById(R.id.emailet);
        passwordET = findViewById(R.id.passwordEt);

        signup = findViewById(R.id.sign_up_button);

        progressDialog = new MyProgressDialog(this);




        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();



                if (TextUtils.isEmpty(email)) {
                    emailET.setError("Enter Email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordET.setError("Enter Password");
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                setSignup(email,password);

            }
        });

    }

    void setSignup(String email, String password)
    {
        progressDialog.showPDiialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            uploadDetails();
                            progressDialog.dismissPDialog();
                            updateUI(firebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed: "+ task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            progressDialog.dismissPDialog();
                        }

                        // ...
                    }
                });
    }

    private void uploadDetails() {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference userRef = myRef.child("Doctors").child(uid);
        userRef.child("patientUID").setValue("Htsd7mlpR4WXtTQMX7D1yH9XbXH2");

    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(SignupActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

}
