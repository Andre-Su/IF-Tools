package com.andresc.if_tools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.andresc.if_tools.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        binding.btnSignOut.setOnClickListener(v -> signOutAccountFirebase());
        binding.btnReadUserData.setOnClickListener(v -> startActivity(new Intent(this, ShowUserProfileActivity.class)));
        binding.btnEditUserData.setOnClickListener(v -> startActivity(new Intent(this, EditUserDataActivity.class)));
    }
    @Override
    public void onStart() {
        super.onStart();
        if(currentUser == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            updateUserUI(email, name, photoUrl);
        }
    }

    private void signOutAccountFirebase(){
        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.signOut();

        Toast.makeText(this, "Usuário deslogado", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        binding.progressBar.setVisibility(View.GONE);
    }

    private void updateUserUI(String userEmail, String userName, Uri photoUrl){
        binding.progressBar.setVisibility(View.VISIBLE);
        if(!userEmail.isEmpty()) binding.textUserEmail.setText(userEmail);
        else binding.textUserEmail.setText("");

        if(!userName.isEmpty()) binding.textUsername.setText(userName);
        else binding.textUsername.setText("");
        binding.progressBar.setVisibility(View.GONE);

        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.icon_account_circle)
                .error(R.drawable.icon_account_circle)
                .into(binding.imgUserProfile);
    }
}