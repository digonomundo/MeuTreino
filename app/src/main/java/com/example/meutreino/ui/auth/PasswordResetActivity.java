package com.example.meutreino.ui.auth;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meutreino.databinding.ActivityTelaRecuperarSenhaBinding;

public class PasswordResetActivity extends AppCompatActivity {
    private ActivityTelaRecuperarSenhaBinding binding;
    private final AuthViewModel authViewModel = new AuthViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelaRecuperarSenhaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        authViewModel.getAuthState().observe(this, state -> {
            boolean loading = state.status == AuthViewModel.Status.LOADING;
            binding.btnRecuperar.setEnabled(!loading);
            if (state.status == AuthViewModel.Status.PASSWORD_RESET_SENT) {
                Toast.makeText(this, "E-mail de recuperação enviado com sucesso!",
                        Toast.LENGTH_LONG).show();
                finish();
            } else if (state.status == AuthViewModel.Status.ERROR) {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show();
            }
        });

        binding.btnRecuperar.setOnClickListener(view ->
                authViewModel.resetPassword(binding.edtEmail.getText().toString().trim()));
        binding.btnCancelar.setOnClickListener(view -> finish());
    }
}
