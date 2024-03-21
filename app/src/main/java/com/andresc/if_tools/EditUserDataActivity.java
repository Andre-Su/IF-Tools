package com.andresc.if_tools;

import static com.google.common.io.Files.getFileExtension;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.andresc.if_tools.databinding.ActivityEditUserDataBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditUserDataActivity extends AppCompatActivity {

    private ActivityEditUserDataBinding binding;
    private ActivityResultLauncher<String> getContentLauncher;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityEditUserDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        user = new User();

        binding.imgBtnBack.setOnClickListener(v -> finish());

        binding.btnSelectImage.setOnClickListener(v -> openFileChooser());
        getContentLauncher = registerForActivityResult(new GetContentContract(), this::onImageSelected);


        //TODO: Dividir as instâncias de username e imagem de perfil em partes independentes
        //TODO: Adicionar campos com a classe usuario


    }

    private void collectDataFromDatabase(){
        user.setUserId(currentUser.getUid());
        user.setPhotoUrl(Objects.requireNonNull(currentUser.getPhotoUrl()).toString());
        db.collection("users").document(currentUser.getUid())
                // operação de leitura
                .get()
                .addOnCompleteListener(this,task -> {
                        if (task.isSuccessful()) {
                            // o documento existe
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // utilizar os dados obtidos
                                user.setNome(document.getString("nome"));
                                binding.editUserName.setText(user.getNome());
                            } else {
                                // documento inexistente
                                Toast.makeText(this, "Os dados do usuário ainda não foram criados!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // falha ao conectar
                            Toast.makeText(this, "Falha em obter dados: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                });
    }

    private void updateUserData(){
        // coletar dados
        // enviar imagem para o firebase storage
        // coletar url de download da imagem
        // enviar url de download da imagem e dados editados para o firestore
        // update ui
    }

    private void collectData(){

    }

    private void uploadUserData() {
        // referência para a coleção "users/{uid}"
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());

        // mapa com os dados a serem atualizados
        Map<String, Object> userData = new HashMap<>();
        userData.put("nome", "");
        userData.put("fotoperfil", user.getPhotoUrl());
        userData.put("dataedicao", FieldValue.serverTimestamp());

        // operação de upload
        userDocRef.set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Dados do usuário atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Falha : " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openFileChooser() {
        getContentLauncher.launch("image/*");
    }

    private void onImageSelected(Uri selectedImageUri) {
        if (selectedImageUri != null) {
            user.setPhotoUri(selectedImageUri);
            Picasso.get().load(user.getPhotoUri()).into(binding.imgSelectPreview);
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadImageToFirebase() {
        Uri fileUri = user.getPhotoUri();
        String filename = "profile_picture." + getFileExtension(fileUri.toString()),
               userId = currentUser.getUid();

        StorageReference storageRef = storage.getReference()
                .child("users/"+userId+ "/images/" + filename);

        UploadTask uploadTask = storageRef.putFile(fileUri);
        uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // O upload foi bem-sucedido
                    Toast.makeText(this, "Upload de imagem bem-sucedido", Toast.LENGTH_SHORT).show();
                    // coletando url de download
                    storageRef.getDownloadUrl().addOnCompleteListener(task1 -> user.setPhotoUrl(task1.getResult().toString()));
                } else Toast.makeText(this, "Falha no upload de imagem", Toast.LENGTH_SHORT).show();
            }
        );
    }
}