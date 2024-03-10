package com.andresc.if_tools;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.andresc.if_tools.databinding.ActivityResetPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnResetPassw.setOnClickListener(v -> validateText());
        binding.imgBtnBack.setOnClickListener(v -> finish());
    }

    private void validateText() {
        // update ui
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.editEmail.setError(null);
        binding.btnResetPassw.setClickable(false);

        String email = binding.editEmail.getText().toString().trim();

        if(email.isEmpty()){
            binding.editEmail.setError("Digite o Email");
            binding.editEmail.requestFocus();
            binding.progressBar.setVisibility(View.GONE);
        }else{
            resetLinkFirebase(email);
        }
        binding.btnResetPassw.setClickable(true);
    }

    private void resetLinkFirebase(String emailAddress){
        mAuth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener(this, task -> {
                if(task.isSuccessful()){
                    // success
                    // update ui
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Link de recuperação enviado no Email", Toast.LENGTH_SHORT).show();
                    finish();
                } else{
                    // failure
                    // update ui
                    binding.progressBar.setVisibility(View.GONE);
                    String message = Objects.requireNonNull(task.getException()).getMessage();
                    assert message != null;
                    if(message.equals("The email address is badly formatted.")){
                        binding.editEmail.setError("Email Inválido");
                        binding.editEmail.requestFocus();
                    }else{
                        Toast.makeText(this, "error: "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}