package com.example.meutreino.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.meutreino.R;
import com.example.meutreino.data.model.WorkoutCategory;
import com.example.meutreino.databinding.FragmentWorkoutCategoryBinding;
import com.example.meutreino.ui.profile.UserProfileViewModel;

import java.util.ArrayList;

public class WorkoutCategoryFragment extends Fragment {
    private FragmentWorkoutCategoryBinding binding;
    private final WorkoutViewModel workoutViewModel = new WorkoutViewModel();
    private final UserProfileViewModel profileViewModel = new UserProfileViewModel();
    private WorkoutAdapter adapter;
    private WorkoutCategory category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String categoryKey = getArguments() == null
                ? "peito" : getArguments().getString("categoryKey", "peito");
        category = WorkoutCategory.fromKey(categoryKey);
        binding.txtCategoryTitle.setText("Treino de " + category.title);
        adapter = new WorkoutAdapter(false, exercise -> {
            profileViewModel.selectVideo(exercise.videoKey, () -> {
                Bundle args = new Bundle();
                args.putString("videoKey", exercise.videoKey);
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.nav_tela_video, args);
            });
        }, null, null);
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(adapter);

        binding.btnEditWorkout.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("categoryKey", category.firestoreKey);
            int destinationId;
            switch (category) {
                case PEITO: destinationId = R.id.nav_montagem_peito; break;
                case ABDOMEN: destinationId = R.id.nav_montagem_abdomen; break;
                case BRACOS: destinationId = R.id.nav_montagem_bracos; break;
                case COSTAS: destinationId = R.id.nav_montagem_costas; break;
                case OMBROS: destinationId = R.id.nav_montagem_ombro; break;
                case PERNAS: destinationId = R.id.nav_montagem_pernas; break;
                default: throw new IllegalStateException("Categoria não suportada");
            }
            Navigation.findNavController(requireView()).navigate(destinationId, args);
        });

        workoutViewModel.getExercisesState().observe(getViewLifecycleOwner(), state -> {
            if (state.status == WorkoutViewModel.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.txtEmptyMessage.setVisibility(View.GONE);
            } else if (state.status == WorkoutViewModel.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                ArrayList<com.example.meutreino.data.model.Exercise> selected = new ArrayList<>();
                for (com.example.meutreino.data.model.Exercise exercise : state.exercises) {
                    if (exercise.isSelecionado) selected.add(exercise);
                }
                adapter.submitList(selected);
                binding.txtEmptyMessage.setVisibility(selected.isEmpty() ? View.VISIBLE : View.GONE);
            } else if (state.status == WorkoutViewModel.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show();
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
        profileViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.status == UserProfileViewModel.Status.ERROR) {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show();
            }
        });
        workoutViewModel.loadExercises(category);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
