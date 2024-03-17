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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditUserDataActivity extends AppCompatActivity {

    private ActivityEditUserDataBinding binding;
    private ActivityResultLauncher<String> getContentLauncher;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private ArrayList contentList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityEditUserDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.imgBtnBack.setOnClickListener(v -> finish());

        getContentLauncher = registerForActivityResult(new GetContentContract(), this::onImageSelected);

        //TODO: Dividir as instâncias de username e imagem de perfil em partes independentes
        //TODO: Adicionar campos com a classe usuario

        binding.btnSelectImage.setOnClickListener(v -> pickImageFromGallery());

    }

    private void collectUserData(){
        contentList.set(6, currentUser.getUid());
        db.collection("users").document(currentUser.getUid())
                // operação de leitura
                .get()
                .addOnCompleteListener(this,task -> {
                        if (task.isSuccessful()) {
                            // o documento existe
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Faça o que precisar com os dados obtidos
                                contentList.set(3,currentUser.getDisplayName());
                            } else {
                                // documento inexistente
                            }
                        } else {
                            // falha ao conectar
                        }
                });
    }

    private void updateUserData(){
        // coletar dados
        // enviar imagem para o firestorage
        // enviar caminho da imagem e dados editados para o firestore
        // update ui
    }
    private void insertUserData() {
        // referência para a coleção "users/{uid}"
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());

        // mapa com os dados a serem inseridos
        Map<String, Object> userData = new HashMap<>();
        userData.put("nome", "");
        userData.put("dataedicao", FieldValue.serverTimestamp());

        // operação de inserção
        userDocRef.set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Dados do usuário atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Falha : " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void pickImageFromGallery() {
        getContentLauncher.launch("");
    }
    private void onImageSelected(Uri imageUri) {
        if (imageUri != null) {
            // A imagem foi selecionada com sucesso
            // preview
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .into(binding.imgSelectPreview);
            binding.imgSelectPreview.setVisibility(View.VISIBLE);
            // Faça o que precisar com a URI da imagem, como um upload para o Firebase Storage
            // uploadImageToFirebase(imageUri);
        } else {
            // A seleção da imagem falhou
            binding.imgSelectPreview.setVisibility(View.GONE);
            Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImageToFirebase(Uri fileUri) {
        String filename = "profile_picture." + getFileExtension(fileUri.toString()),
               userId = currentUser.getUid();

        StorageReference storageRef = storage.getReference()
                .child("users/"+userId+ "/images/" + filename);

        UploadTask uploadTask = storageRef.putFile(fileUri);

        uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // O upload foi bem-sucedido
                    Toast.makeText(this, "Upload de imagem bem-sucedido", Toast.LENGTH_SHORT).show();
                } else {
                    // O upload falhou
                    Toast.makeText(this, "Falha no upload de imagem", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
}