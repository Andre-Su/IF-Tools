package com.andresc.if_tools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.andresc.if_tools.databinding.ActivityCreateAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    private ActivityCreateAccountBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnCreateAccount.setOnClickListener(v -> validateText());
        binding.imgBtnBack.setOnClickListener(v -> finish());
    }
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    private void validateText(){
        // update ui
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.editEmail.setError(null);
        binding.editUsername.setError(null);
        binding.editPassword.setError(null);
        binding.btnCreateAccount.setClickable(false);

        // get text user credentials
        String emailAddress = binding.editEmail.getText().toString(),
                username = binding.editUsername.getText().toString(),
                password = binding.editPassword.getText().toString();

        // is it valid?
        if(emailAddress.isEmpty()){
            binding.editEmail.setError("Digite o Email");
            binding.progressBar.setVisibility(View.GONE);

        } else if (username.isEmpty()) {
            binding.editUsername.setError("Digite o Nome");
            binding.progressBar.setVisibility(View.GONE);

        } else if (password.isEmpty()) {
            binding.editPassword.setError("Digite a Senha");
            binding.progressBar.setVisibility(View.GONE);

        } else if (password.length()<6) {
            binding.editPassword.setError("Menos de 6 dígitos");
            binding.progressBar.setVisibility(View.GONE);

        } else {
            // request firebase action
            createAccountFirebase(emailAddress,password);
        }
        // update ui if necessary
        binding.btnCreateAccount.setClickable(true);
    }

    private void createAccountFirebase(String emailAddress, String password){
        mAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // success
                        currentUser = mAuth.getCurrentUser();

                        // update ui
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Criado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // failure
                        String message = (Objects.requireNonNull(task.getException()).getMessage());
                        assert message != null;

                        // update ui
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Falha no Login...", Toast.LENGTH_SHORT).show();
                        if(message.equals("The email address is already in use by another account.")){
                            binding.editEmail.setError("Email já está em uso");
                            binding.editEmail.requestFocus();
                        } else if (message.equals("The email address is badly formatted.")) {
                            binding.editEmail.setError("Email Inválido");
                            binding.editEmail.requestFocus();
                        } else {
                            Toast.makeText(this, "error: "+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void insertUserData() {
        // referência para a coleção "users/{uid}"
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());

        // mapa com os dados a serem inseridos
        Map<String, Object> userData = new HashMap<>();
        userData.put("nome", binding.editUsername.getText().toString());
        userData.put("email", currentUser.getEmail());
        userData.put("telefone", "");
        userData.put("fotoperfil", "");
        userData.put("uid", currentUser.getUid());
        userData.put("tipo", 3);
        userData.put("nivel", 3);
        userData.put("datacriacao", FieldValue.serverTimestamp());
        userData.put("dataedicao", FieldValue.serverTimestamp());

        // operação de inserção
        userDocRef.set(userData)
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Toast.makeText(this, "Dados do usuário adicionados com sucesso!").show();
                        } else {
                            //Toast.makeText(this, "Falha ao adicionar dados do usuário: " + task.getException()).show();
                        }
                });
    }
}