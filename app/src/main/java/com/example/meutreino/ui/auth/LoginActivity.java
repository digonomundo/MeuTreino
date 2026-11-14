package com.example.meutreino.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meutreino.databinding.ActivityFrmLoginBinding;
import com.example.meutreino.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private ActivityFrmLoginBinding binding;
    private final AuthViewModel authViewModel = new AuthViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFrmLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        authViewModel.getAuthState().observe(this, state -> {
            boolean loading = state.status == AuthViewModel.Status.LOADING;
            binding.btnEntrar.setEnabled(!loading);
            if (state.status == AuthViewModel.Status.SUCCESS) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else if (state.status == AuthViewModel.Status.ERROR) {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show();
            }
        });

        binding.btnEntrar.setOnClickListener(view -> authViewModel.login(
                binding.edtEmail.getText().toString().trim(),
                binding.edtSenha.getText().toString().trim()));
        binding.btnCriarUsuario.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class)));
        binding.btnRecuperarSenha.setOnClickListener(view ->
                startActivity(new Intent(this, PasswordResetActivity.class)));
    }
}
