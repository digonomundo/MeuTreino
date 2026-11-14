package com.example.meutreino.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.meutreino.data.model.WorkoutCategory;
import com.example.meutreino.databinding.FragmentWorkoutSetupBinding;

public class WorkoutSetupFragment extends Fragment {
    private FragmentWorkoutSetupBinding binding;
    private final WorkoutViewModel workoutViewModel = new WorkoutViewModel();
    private WorkoutAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutSetupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String categoryKey = getArguments() == null
                ? "peito" : getArguments().getString("categoryKey", "peito");
        WorkoutCategory category = WorkoutCategory.fromKey(categoryKey);
        binding.txtSetupCategoryTitle.setText("Montagem de " + category.title);
        adapter = new WorkoutAdapter(true, null,
                (exercise, selected) -> workoutViewModel.toggleExercise(exercise, selected),
                (exercise, total) -> workoutViewModel.saveSeriesTotal(exercise, total));
        binding.rvSetupExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSetupExercises.setAdapter(adapter);

        workoutViewModel.getExercisesState().observe(getViewLifecycleOwner(), state -> {
            if (state.status == WorkoutViewModel.Status.LOADING) {
                binding.progressBarSetup.setVisibility(View.VISIBLE);
            } else if (state.status == WorkoutViewModel.Status.SUCCESS) {
                binding.progressBarSetup.setVisibility(View.GONE);
                adapter.submitList(state.exercises);
            } else if (state.status == WorkoutViewModel.Status.ERROR) {
                binding.progressBarSetup.setVisibility(View.GONE);
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show();
            } else {
                binding.progressBarSetup.setVisibility(View.GONE);
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
