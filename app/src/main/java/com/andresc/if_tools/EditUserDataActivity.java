package com.andresc.if_tools;

import static com.google.common.io.Files.getFileExtension;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.andresc.if_tools.databinding.ActivityEditUserDataBinding;
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

        binding.btnSendUpdate.setOnClickListener(v -> validateData());

        binding.btnSelectImage.setOnClickListener(v -> openFileChooser());
        getContentLauncher = registerForActivityResult(new GetContentContract(), this::onImageSelected);


        //TODO: Dividir as instâncias de username e imagem de perfil em partes independentes
        //TODO: Adicionar campos com a classe usuario


    }

    @Override
    protected void onStart() {
        super.onStart();
        collectFromDatabase();
    }

    private void collectFromDatabase(){
        user.setUserId(currentUser.getUid());

        db.collection("users").document(user.getUserId())
                // operação de leitura
                .get()
                .addOnCompleteListener(this,task -> {
                        if (task.isSuccessful()) {
                            // documento existe
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // utilizar os dados obtidos
                                user.setNome(document.getString("nome"));
                                if (!user.getNome().isEmpty())
                                    binding.editUserName.setText(user.getNome());

                                user.setPhone(document.getString("telefone"));
                                if (!user.getPhone().isEmpty())
                                    binding.editUserPhone.setText(user.getPhone());

                                user.setPhotoUrl(document.getString("fotoperfil"));
                                if (!user.getPhotoUrl().isEmpty()){
                                    Picasso.get()
                                            .load(user.getPhotoUrl())
                                            .into(binding.imgSelectPreview);
                                    user.setPhotoUri(Uri.parse(user.getPhotoUrl()));
                                }
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
        // coletar dados --
        // validar dados --
        // enviar imagem para o firebase storage
        uploadImageToFirebase();
        // coletar url de download da imagem --
        // enviar url de download da imagem e dados editados para o firestore
        uploadUserData();
        // update ui
    }

    private void validateData(){
        user.setNome(binding.editUserName.getText().toString());
        user.setPhone(binding.editUserPhone.getText().toString());

        if (user.getNome().isEmpty()){
            binding.editUserName.setError("Nome em branco");
            binding.editUserName.requestFocus();

        } else if (user.getPhone().isEmpty()) {
            binding.editUserPhone.setError("Nome em branco");
            binding.editUserPhone.requestFocus();

        } else {
            updateUserData();
        }
    }

    private void uploadUserData() {
        // referência para a coleção "users/{uid}"
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        // mapa dos dados a serem atualizados
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUserId());
        userData.put("nome", user.getNome());
        userData.put("telefone", user.getPhone());
        userData.put("fotoperfil", user.getPhotoUrl());
        userData.put("dataedicao", FieldValue.serverTimestamp());

        // operação de upload
        userDocRef.update(userData)
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
            Picasso.get().load(user.getPhotoUri()).placeholder(R.drawable.ic_placeholder_image).into(binding.imgSelectPreview);
            binding.imgSelectPreview.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Imagem não selecionada", Toast.LENGTH_SHORT).show();
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
                    storageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                        user.setPhotoUrl(task1.getResult().toString());
                        Toast.makeText(this, user.getPhotoUrl(), Toast.LENGTH_SHORT).show();
                    });
                } else Toast.makeText(this, "Falha no upload de imagem", Toast.LENGTH_SHORT).show();
            }
        );
    }
}