package com.andresc.if_tools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.andresc.if_tools.databinding.ActivitySplashBinding;

import java.lang.reflect.Array;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler(getMainLooper()).postDelayed(()->{
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }, 2000);
    }
}