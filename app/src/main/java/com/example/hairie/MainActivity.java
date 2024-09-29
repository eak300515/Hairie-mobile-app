package com.example.hairie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView textView4;

    EditText inputEmail, inputPassword;
    AppCompatButton btnlogin;
    String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser mUser;


    private LinearLayout topLinearLayout;
    private TextView textView3;
    private CardView cardView, cardView2, cardView3, cardView4, cardView5;
    private ImageView googleImageView, fbImageView, apppImageView;
    private ConstraintLayout registerLayout;

    private Animation fadeInAnimation, bottomDownAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Initialize views
        textView4 = findViewById(R.id.textView4);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnlogin = findViewById(R.id.btnlogin);


        topLinearLayout = findViewById(R.id.top_linearLayout);
        textView3 = findViewById(R.id.textView3);
        cardView = findViewById(R.id.cardView);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        googleImageView = findViewById(R.id.imagegoogle);
        fbImageView = findViewById(R.id.imagefb);
        apppImageView = findViewById(R.id.imageapp);
        registerLayout = findViewById(R.id.register_layout);

        // Load animations
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        bottomDownAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_down);

        // Apply animations to views and set visibility before starting the animation
        textView3.setVisibility(TextView.VISIBLE);
        cardView.setVisibility(CardView.VISIBLE);
        cardView2.setVisibility(CardView.VISIBLE);
        cardView3.setVisibility(CardView.VISIBLE);
        cardView4.setVisibility(CardView.VISIBLE);
        cardView5.setVisibility(CardView.VISIBLE);
        registerLayout.setVisibility(ConstraintLayout.VISIBLE);

        topLinearLayout.startAnimation(fadeInAnimation);
        textView3.startAnimation(fadeInAnimation);
        cardView.startAnimation(bottomDownAnimation);
        cardView2.startAnimation(bottomDownAnimation);
        cardView3.startAnimation(bottomDownAnimation);
        cardView4.startAnimation(bottomDownAnimation);
        cardView5.startAnimation(bottomDownAnimation);
        registerLayout.startAnimation(bottomDownAnimation);

        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Register.class));
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perforLogin();
            }
        });
    }

    private void perforLogin() {
        String email=inputEmail.getText().toString();
        String password=inputPassword.getText().toString();

        if (!email.matches(emailPattern))
        {
            inputEmail.setError("Enter Correct Email");
        } else if (password.isEmpty() || password.length()<6)
        {
            inputPassword.setError("Enter Valid Password");
        }else
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        sendUserToNextActivity();
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(MainActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(MainActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}