package com.example.meutreino.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.meutreino.R;
import com.example.meutreino.databinding.ActivityHomeBinding;
import com.example.meutreino.ui.main.MainActivity;
import com.example.meutreino.ui.profile.UserProfileViewModel;

import java.util.Calendar;

public class UserDataInputActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_PROFILE = "editProfile";

    private ActivityHomeBinding binding;
    private final UserProfileViewModel profileViewModel = new UserProfileViewModel();
    private boolean editMode;
    private String selectedBirthDateIso = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        editMode = getIntent().getBooleanExtra(EXTRA_EDIT_PROFILE, false);
        binding.txtProfileTitle.setText(editMode ? "Editar perfil" : "Complete seu perfil");
        binding.txtProfileSubtitle.setText(editMode
                ? "Mantenha seus dados atualizados."
                : "Esses dados ajudam a personalizar sua experiência.");
        binding.btnSalvarDados.setText(editMode ? "Salvar alterações" : "Salvar e continuar");
        binding.btnPular.setText(editMode ? "Cancelar" : "Preencher depois");

        profileViewModel.getUserState().observe(this, state -> {
            boolean loading = state.status == UserProfileViewModel.Status.LOADING;
            binding.btnSalvarDados.setEnabled(!loading);
            binding.btnPular.setEnabled(!loading);
            if (state.status == UserProfileViewModel.Status.SUCCESS && editMode
                    && state.user != null) {
                binding.edtNome.setText(state.user.nome);
                if (state.user.peso > 0) binding.edtPeso.setText(String.valueOf(state.user.peso));
                if (state.user.altura > 0) binding.edtAltura.setText(String.valueOf(state.user.altura));
                if (!state.user.dataNascimento.isEmpty()) {
                    setBirthDate(state.user.dataNascimento);
                }
            }
            if (state.status == UserProfileViewModel.Status.SAVED) {
                Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show();
                if (editMode) {
                    finish();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            } else if (state.status == UserProfileViewModel.Status.ERROR) {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show();
            }
        });

        binding.btnSalvarDados.setOnClickListener(view -> profileViewModel.saveUserProfile(
                binding.edtNome.getText().toString().trim(),
                binding.edtPeso.getText().toString().trim(),
                binding.edtAltura.getText().toString().trim(),
                selectedBirthDateIso));
        binding.edtDataNascimento.setOnClickListener(view -> showBirthDatePicker());
        binding.btnPular.setOnClickListener(view -> {
            if (editMode) {
                finish();
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        if (editMode) profileViewModel.loadUserProfile();
    }

    private void showBirthDatePicker() {
        Calendar initialDate = Calendar.getInstance();
        if (!selectedBirthDateIso.isEmpty()) {
            String[] parts = selectedBirthDateIso.split("-");
            initialDate.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1,
                    Integer.parseInt(parts[2]));
        }
        DatePickerDialog picker = new DatePickerDialog(this,
                R.style.Theme_MeuTreino_DatePicker,
                (view, year, month, dayOfMonth) -> { },
                initialDate.get(Calendar.YEAR),
                initialDate.get(Calendar.MONTH),
                initialDate.get(Calendar.DAY_OF_MONTH));
        picker.getDatePicker().setMaxDate(System.currentTimeMillis());
        picker.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok),
                (dialog, which) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(picker.getDatePicker().getYear(),
                            picker.getDatePicker().getMonth(),
                            picker.getDatePicker().getDayOfMonth());
                    setBirthDate(String.format(java.util.Locale.US, "%04d-%02d-%02d",
                            selectedDate.get(Calendar.YEAR),
                            selectedDate.get(Calendar.MONTH) + 1,
                            selectedDate.get(Calendar.DAY_OF_MONTH)));
                });
        picker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                (dialog, which) -> dialog.cancel());
        picker.show();
        picker.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(this, R.color.primary));
        picker.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(this, R.color.text_secondary));
    }

    private void setBirthDate(String isoDate) {
        String[] parts = isoDate.split("-");
        selectedBirthDateIso = isoDate;
        binding.edtDataNascimento.setText(String.format(java.util.Locale.getDefault(),
                "%02d/%02d/%04d",
                Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0])));
    }
}
