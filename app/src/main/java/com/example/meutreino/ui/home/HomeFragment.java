package com.example.meutreino.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.meutreino.R;
import com.example.meutreino.data.model.WorkoutCategory;
import com.example.meutreino.databinding.FragmentHomeBinding;
import com.example.meutreino.ui.profile.UserProfileViewModel;

import java.util.Calendar;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private final UserProfileViewModel profileViewModel = new UserProfileViewModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.cardPeito.setOnClickListener(v ->
                navigateToCategory(WorkoutCategory.PEITO, R.id.action_nav_home_to_nav_peito));
        binding.cardAbdomen.setOnClickListener(v ->
                navigateToCategory(WorkoutCategory.ABDOMEN, R.id.action_nav_home_to_nav_abdomen));
        binding.cardPernas.setOnClickListener(v ->
                navigateToCategory(WorkoutCategory.PERNAS, R.id.action_nav_home_to_nav_pernas));
        binding.cardBracos.setOnClickListener(v ->
                navigateToCategory(WorkoutCategory.BRACOS, R.id.nav_bracos));
        binding.cardCostas.setOnClickListener(v ->
                navigateToCategory(WorkoutCategory.COSTAS, R.id.nav_costas));
        binding.cardOmbros.setOnClickListener(v ->
                navigateToCategory(WorkoutCategory.OMBROS, R.id.nav_ombros));
        profileViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.status == UserProfileViewModel.Status.SUCCESS && state.user != null) {
                String name = state.user.nome.trim();
                int separator = name.indexOf(' ');
                if (separator >= 0) name = name.substring(0, separator);
                updateWelcome(name);
            }
        });
        updateWelcome("");
    }

    private void updateWelcome(String name) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "Bom dia"
                : hour < 18 ? "Boa tarde" : "Boa noite";
        binding.txtWelcome.setText(name.isEmpty() ? greeting : greeting + ", " + name);
        binding.txtWelcomeSubtitle.setText("O que gostaria de treinar hoje?");
    }

    @Override
    public void onResume() {
        super.onResume();
        profileViewModel.loadUserProfile();
    }

    private void navigateToCategory(WorkoutCategory category, int destinationId) {
        Bundle arguments = new Bundle();
        arguments.putString("categoryKey", category.firestoreKey);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(destinationId, arguments);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
