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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    TextView textView4;
    EditText inputEmail, inputUsername, inputPassword, inputConfirmPassword;
    AppCompatButton btnRegister;
    String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser mUser;


    private LinearLayout topLinearLayout;
    private TextView textView3;
    private CardView cardView, cardView2, cardView3, cardView4, cardView5;
    private ImageView googleImageView, fbImageView, apppImageView;
    private ConstraintLayout loginLayout;

    private Animation fadeInAnimation, bottomDownAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Initialize views
        textView4 = findViewById(R.id.textView4);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        inputUsername = findViewById(R.id.inputUsername);
        btnRegister = findViewById(R.id.btnRegister);

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
        loginLayout = findViewById(R.id.login_layout);


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
        loginLayout.setVisibility(ConstraintLayout.VISIBLE);

        topLinearLayout.startAnimation(fadeInAnimation);
        textView3.startAnimation(fadeInAnimation);
        cardView.startAnimation(bottomDownAnimation);
        cardView2.startAnimation(bottomDownAnimation);
        cardView3.startAnimation(bottomDownAnimation);
        cardView4.startAnimation(bottomDownAnimation);
        cardView5.startAnimation(bottomDownAnimation);
        loginLayout.startAnimation(bottomDownAnimation);

        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,MainActivity.class));
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerforAuth();
            }
        });
    }

    private void PerforAuth() {
        String email = inputEmail.getText().toString();
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter Correct Email");
        }else if(username.isEmpty() || username.length()<3)
        {
            inputUsername.setError("Username must contain 4 characters");
        }else if (password.isEmpty() || password.length()<6)
        {
            inputPassword.setError("Enter Valid Password");
        } else if (!password.equals(confirmPassword))
        {
            inputConfirmPassword.setError("Password Does Not Match!!");
        }else
        {


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        sendUserToNextActivity();
                        Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(Register.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(Register.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
