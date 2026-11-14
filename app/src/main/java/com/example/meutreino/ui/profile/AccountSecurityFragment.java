package com.example.meutreino.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.meutreino.databinding.FragmentAccountSecurityBinding;
import com.example.meutreino.ui.auth.AuthViewModel;
import com.example.meutreino.ui.auth.LoginActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AccountSecurityFragment extends Fragment {
    private FragmentAccountSecurityBinding binding;
    private final AccountSecurityViewModel viewModel = new AccountSecurityViewModel();
    private final AuthViewModel authViewModel = new AuthViewModel();
    private boolean deletingAccount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountSecurityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String currentEmail = viewModel.getCurrentEmail();
        binding.txtEmailAtual.setText(currentEmail.isEmpty()
                ? "E-mail da conta indisponível" : "E-mail atual: " + currentEmail);

        binding.btnAlterarEmail.setOnClickListener(v -> requestEmailChange());
        binding.btnAlterarSenha.setOnClickListener(v -> requestPasswordChange());
        binding.btnExcluirConta.setOnClickListener(v -> confirmAccountDeletion());

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            boolean loading = state.status == AccountSecurityViewModel.Status.LOADING;
            binding.progressSecurity.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnAlterarEmail.setEnabled(!loading);
            binding.btnAlterarSenha.setEnabled(!loading);
            binding.btnExcluirConta.setEnabled(!loading);

            if (state.status == AccountSecurityViewModel.Status.SUCCESS) {
                clearPasswordFields();
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show();
                if (deletingAccount) {
                    deletingAccount = false;
                    authViewModel.logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                }
            } else if (state.status == AccountSecurityViewModel.Status.ERROR) {
                deletingAccount = false;
                clearPasswordFields();
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestEmailChange() {
        String newEmail = textOf(binding.edtNovoEmail).trim();
        String currentPassword = textOf(binding.edtSenhaAtualEmail);
        binding.tilNovoEmail.setError(null);
        binding.tilSenhaAtualEmail.setError(null);
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            binding.tilNovoEmail.setError("Digite um e-mail válido.");
            return;
        }
        if (currentPassword.isEmpty()) {
            binding.tilSenhaAtualEmail.setError("Digite sua senha atual.");
            return;
        }
        deletingAccount = false;
        viewModel.changeEmail(currentPassword, newEmail);
        binding.edtSenhaAtualEmail.setText("");
    }

    private void requestPasswordChange() {
        String currentPassword = textOf(binding.edtSenhaAtualSenha);
        String newPassword = textOf(binding.edtNovaSenha);
        String confirmation = textOf(binding.edtConfirmarSenha);
        binding.tilSenhaAtualSenha.setError(null);
        binding.tilNovaSenha.setError(null);
        binding.tilConfirmarSenha.setError(null);
        if (currentPassword.isEmpty()) {
            binding.tilSenhaAtualSenha.setError("Digite sua senha atual.");
        } else if (newPassword.length() < 6) {
            binding.tilNovaSenha.setError("Use no mínimo 6 caracteres.");
        } else if (!newPassword.equals(confirmation)) {
            binding.tilConfirmarSenha.setError("As senhas não coincidem.");
        } else {
            deletingAccount = false;
            viewModel.changePassword(currentPassword, newPassword);
            binding.edtSenhaAtualSenha.setText("");
            binding.edtNovaSenha.setText("");
            binding.edtConfirmarSenha.setText("");
        }
    }

    private void confirmAccountDeletion() {
        String currentPassword = textOf(binding.edtSenhaExcluir);
        binding.tilSenhaExcluir.setError(null);
        if (currentPassword.isEmpty()) {
            binding.tilSenhaExcluir.setError("Digite sua senha atual.");
            return;
        }
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Excluir conta permanentemente?")
                .setMessage("Seu perfil e seus dados de treino serão removidos. "
                        + "Esta ação não pode ser desfeita.")
                .setNegativeButton("Cancelar", (ignored, which) ->
                        clearDeletePassword())
                .setPositiveButton("Excluir conta", (ignored, which) -> {
                    deletingAccount = true;
                    viewModel.deleteAccount(currentPassword);
                    clearDeletePassword();
                })
                .create();
        dialog.setOnCancelListener(ignored -> clearDeletePassword());
        dialog.show();
    }

    private void clearDeletePassword() {
        if (binding != null) binding.edtSenhaExcluir.setText("");
    }

    private String textOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }

    private void clearPasswordFields() {
        binding.edtSenhaAtualEmail.setText("");
        binding.edtSenhaAtualSenha.setText("");
        binding.edtNovaSenha.setText("");
        binding.edtConfirmarSenha.setText("");
        binding.edtSenhaExcluir.setText("");
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
