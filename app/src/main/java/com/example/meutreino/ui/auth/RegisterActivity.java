package com.example.meutreino.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meutreino.databinding.ActivityFrmCadastroBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityFrmCadastroBinding binding;
    private final AuthViewModel authViewModel = new AuthViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFrmCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        authViewModel.getAuthState().observe(this, state -> {
            boolean loading = state.status == AuthViewModel.Status.LOADING;
            binding.btnCadastrar.setEnabled(!loading);
            if (state.status == AuthViewModel.Status.SUCCESS) {
                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, UserDataInputActivity.class));
                finish();
            } else if (state.status == AuthViewModel.Status.ERROR) {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show();
            }
        });

        binding.btnCadastrar.setOnClickListener(view -> authViewModel.register(
                binding.edtEmail.getText().toString().trim(),
                binding.edtSenha.getText().toString().trim()));
        binding.btnPossuiConta.setOnClickListener(view -> finish());
    }
}
