package com.example.meutreino.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.meutreino.R;
import com.example.meutreino.databinding.FragmentTelaUsuarioBinding;
import com.example.meutreino.ui.auth.AuthViewModel;
import com.example.meutreino.ui.auth.LoginActivity;
import com.example.meutreino.ui.auth.UserDataInputActivity;

import java.util.Locale;

public class UserProfileFragment extends Fragment {
    private FragmentTelaUsuarioBinding binding;
    private final UserProfileViewModel userProfileViewModel = new UserProfileViewModel();
    private final AuthViewModel authViewModel = new AuthViewModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTelaUsuarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userProfileViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.status == UserProfileViewModel.Status.SUCCESS && state.user != null) {
                boolean hasWeight = state.user.peso > 0;
                boolean hasHeight = state.user.altura > 0;
                boolean hasBirthDate = !state.user.dataNascimento.isEmpty();
                boolean complete = !state.user.nome.trim().isEmpty()
                        && hasWeight && hasHeight && hasBirthDate;

                binding.txtNome.setText(state.user.nome.trim().isEmpty()
                        ? "Perfil incompleto" : state.user.nome);
                binding.txtAltura.setText(hasHeight
                        ? String.format(Locale.getDefault(), "%.2f m", state.user.altura) : "Altura não informada");
                binding.txtPeso.setText(hasWeight
                        ? String.format(Locale.getDefault(), "%.1f kg", state.user.peso) : "Peso não informado");
                binding.txtIdade.setText(hasBirthDate || state.user.idade > 0
                        ? String.format(Locale.getDefault(), "%d anos", state.user.getIdade())
                        : "Data de nascimento não informada");
                binding.txtImc.setText(hasWeight && hasHeight
                        ? String.format(Locale.getDefault(), "%.2f", state.user.getImc()) : "—");
                binding.txtResultadoImc.setText(hasWeight && hasHeight
                        ? state.user.getImcDescricao() : "Informe peso e altura para calcular o IMC.");
                binding.txtQtdAgua.setText(hasWeight
                        ? String.format(Locale.getDefault(), "%.2f L/dia",
                        state.user.getConsumoAguaLitros()) : "—");
                binding.txtProfileIncomplete.setVisibility(complete ? View.GONE : View.VISIBLE);
            } else if (state.status == UserProfileViewModel.Status.ERROR) {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UserDataInputActivity.class);
            intent.putExtra(UserDataInputActivity.EXTRA_EDIT_PROFILE, true);
            startActivity(intent);
        });
        binding.btnSegurancaConta.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.nav_conta_seguranca));
        binding.btnDeslogar.setOnClickListener(v -> {
            authViewModel.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        userProfileViewModel.loadUserProfile();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
