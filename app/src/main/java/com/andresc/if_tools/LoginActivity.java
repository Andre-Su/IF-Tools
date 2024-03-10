package com.andresc.if_tools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.andresc.if_tools.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(v -> validateText());
        binding.textCreateAccount.setOnClickListener(v -> startActivity(new Intent(this, CreateAccountActivity.class)));
        binding.textResetPassw.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void validateText(){
        // update ui
        binding.loadingBar.setVisibility(View.VISIBLE);
        binding.editEmail.setError(null);
        binding.editPassw.setError(null);
        binding.btnLogin.setClickable(false);

        // get text user credentials
        String emailAddress = binding.editEmail.getText().toString(),
                password = binding.editPassw.getText().toString();

        // is it valid?
        if(emailAddress.isEmpty()){
            binding.editEmail.setError("Digite o Email");
            binding.loadingBar.setVisibility(View.GONE);
        } else if (password.isEmpty()) {
            binding.editPassw.setError("Digite a Senha");
            binding.loadingBar.setVisibility(View.GONE);
        } else {
            // request firebase action
            signInAccountFirebase(emailAddress,password);
        }
        // update ui if necessary
        binding.btnLogin.setClickable(true);
    }

    private void signInAccountFirebase(String emailAddress,String password){
        mAuth.signInWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this,task -> {
                   if(task.isSuccessful()){
                       // success
                       currentUser = mAuth.getCurrentUser();

                       // update UI
                       Toast.makeText(this, "Login com sucesso!", Toast.LENGTH_SHORT).show();
                       binding.loadingBar.setVisibility(View.GONE);
                   } else {
                       // failure
                       String message = (Objects.requireNonNull(task.getException()).getMessage());
                       assert message != null;

                       // update UI
                       Toast.makeText(this, "Falha no Login...", Toast.LENGTH_SHORT).show();
                       binding.loadingBar.setVisibility(View.GONE);
                       if(message.equals("The supplied auth credential is incorrect, malformed or has expired.")){
                           binding.editEmail.setError("Email ou senha Incorretos");
                           binding.editEmail.requestFocus();
                       } else {
                           Toast.makeText(this, "error: "+message, Toast.LENGTH_SHORT).show();
                       }
                   }
                });
    }
}