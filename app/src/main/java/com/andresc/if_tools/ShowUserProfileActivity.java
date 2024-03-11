package com.andresc.if_tools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.andresc.if_tools.databinding.ActivityShowUserProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ShowUserProfileActivity extends AppCompatActivity {

    private ActivityShowUserProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        binding.imgBtnBack.setOnClickListener(v -> finish());
        binding.btnEditData.setOnClickListener(v -> startActivity(new Intent(this, EditUserDataActivity.class)));
        binding.btnReload.setOnClickListener(v -> getUserData());

        getUserData();
    }

    private void getUserData(){
        binding.progressBar.setVisibility(View.VISIBLE);
        String userName = currentUser.getDisplayName();
        String userEmail = currentUser.getEmail();
        String userPhoneNumber = currentUser.getPhoneNumber();
        Uri photoUrl = currentUser.getPhotoUrl();

        assert userEmail != null;
        updateUserUI(userEmail, userName,userPhoneNumber,photoUrl);

        binding.progressBar.setVisibility(View.GONE);
    }
    private void updateUserUI(String userEmail, String userName, String userPhoneNumber, Uri photoUrl){
        if(!userEmail.isEmpty()) binding.textUserEmail.setText(userEmail);
        else binding.textUserEmail.setText("sem dados");

        if(!userName.isEmpty()) binding.textUserName.setText(userName);
        else binding.textUserName.setText("sem dados");

        if (!userPhoneNumber.isEmpty()) binding.textUserPhone.setText(userPhoneNumber);
        else binding.textUserPhone.setText("sem dados");

        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.icon_account_circle)
                .error(R.drawable.icon_account_circle)
                .into(binding.imgProfilePicture);

        binding.progressBar.setVisibility(View.GONE);
    }
}