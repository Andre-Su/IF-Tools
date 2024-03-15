package com.andresc.if_tools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.andresc.if_tools.databinding.ActivityEditUserDataBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class EditUserDataActivity extends AppCompatActivity {

    private ActivityEditUserDataBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityEditUserDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //TODO: Dividir as instâncias de username e imagem de perfil em partes independentes
        //TODO: Adicionar campos com a classe usuario

        binding.imgBtnBack.setOnClickListener(v -> finish());
        binding.btnSendUpdate.setOnClickListener(v -> getData());
    }

    private void getData(){
        binding.progressBar.setVisibility(View.VISIBLE);
        String username = binding.editUserName.getText().toString();
        String photoUrl = binding.editUserPhoto.getText().toString();
        int send = 0;
        switch (send){
            case 1:
                if (username.isEmpty()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.editUserName.setError("Campo em  Branco");
                binding.editUserName.requestFocus();

            } else if (username.length()<3) {
                binding.progressBar.setVisibility(View.GONE);
                binding.editUserName.setError("Menos de 3 dígitos");
                binding.editUserName.requestFocus();
            } else {

                }
                break;
            case 2:
                if (photoUrl.isEmpty()) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.editUserPhoto.setError("Campo em Branco");
                    binding.editUserPhoto.requestFocus();

                } else if (!Patterns.WEB_URL.matcher(photoUrl).matches()) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.editUserPhoto.setError("URL inválido");
                    binding.editUserPhoto.requestFocus();

                } else {
                    sendRequestUpdate(username, photoUrl);
                }
                break;

        }

    }

    private void createUsernameRequest(String username){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
    }
    private void createPhotoRequest(String username){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
    }

    private void sendRequestUpdate(String username, String photoUrl){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(Uri.parse(photoUrl))
                .build();
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener( task -> {
                        if (task.isSuccessful()) {
                            // success
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Dados Atualizados", Toast.LENGTH_SHORT).show();
                        } else {
                            // failure
                            String message = (Objects.requireNonNull(task.getException()).getMessage());
                            assert message != null;

                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "error: "+message, Toast.LENGTH_SHORT).show();
                        }
                });
    }
}