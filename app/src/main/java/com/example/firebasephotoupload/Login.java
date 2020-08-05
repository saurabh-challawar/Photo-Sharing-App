package com.example.firebasephotoupload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    FirebaseAuth fAuth;
    private EditText email,password;
    private Button login,register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=(EditText)findViewById(R.id.emailL);
        password=(EditText)findViewById(R.id.passwordL);
        login=(Button)findViewById(R.id.loginL);
        register=(Button)findViewById(R.id.registerL);
        fAuth= FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=email.getText().toString().trim();
                String pass=password.getText().toString();

                if(TextUtils.isEmpty(pass)){
                    password.setError("Please enter you full name.");
                    return;
                }
                if(pass.length()<6)
                {
                    password.setError("Password should atleast contain 6 characters.");
                }

                fAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if (fAuth.getCurrentUser().isEmailVerified()) {
                                startActivity(new Intent(Login.this, MainActivity.class));
                                Toast.makeText(Login.this, "Successfully Signed In.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Please verify you Email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(Login.this,"Enter correct credentials.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Register.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(Login.this,MainActivity.class));
        }

    }
}
