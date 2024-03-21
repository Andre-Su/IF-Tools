package com.andresc.if_tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GetContentContract extends ActivityResultContract<String, Uri> {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String input) {
        return new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*");
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (intent == null || resultCode != AppCompatActivity.RESULT_OK) {
            return null;
        }
        return intent.getData();
    }
}
