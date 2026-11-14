package com.example.meutreino.ui.workout;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meutreino.R;
import com.example.meutreino.data.model.Exercise;
import com.example.meutreino.databinding.ItemExerciseBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ExerciseViewHolder> {
    public interface ExerciseClickListener {
        void onClick(Exercise exercise);
    }

    public interface SelectionListener {
        void onChange(Exercise exercise, boolean selected);
    }

    public interface SeriesCountListener {
        void onChange(Exercise exercise, int total);
    }

    private final boolean setupMode;
    private final ExerciseClickListener onItemClick;
    private final SelectionListener onToggleSelect;
    private final SeriesCountListener onSeriesCountChange;
    private List<Exercise> exercises = new ArrayList<>();

    public WorkoutAdapter(boolean setupMode, ExerciseClickListener onItemClick,
                          SelectionListener onToggleSelect,
                          SeriesCountListener onSeriesCountChange) {
        this.setupMode = setupMode;
        this.onItemClick = onItemClick;
        this.onToggleSelect = onToggleSelect;
        this.onSeriesCountChange = onSeriesCountChange;
    }

    public void submitList(List<Exercise> newList) {
        exercises = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseBinding binding = ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExerciseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(exercises.get(position));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    @Override
    public void onViewRecycled(@NonNull ExerciseViewHolder holder) {
        holder.recycle();
        super.onViewRecycled(holder);
    }

    final class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final ItemExerciseBinding binding;
        private final Handler timerHandler = new Handler(Looper.getMainLooper());
        private final Runnable timerTicker = new Runnable() {
            @Override
            public void run() {
                if (boundExercise == null || !boundExercise.timerRunning) return;
                updateStopwatch();
                timerHandler.postDelayed(this, 1000L);
            }
        };
        private Exercise boundExercise;
        private CountDownTimer restTimer;
        private TextWatcher loadWatcher;

        ExerciseViewHolder(ItemExerciseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Exercise exercise) {
            stopRestTimer();
            timerHandler.removeCallbacks(timerTicker);
            boundExercise = exercise;
            binding.txtExerciseTitle.setText(exercise.titulo);
            binding.txtExerciseModality.setText(exercise.modalidade);
            if (exercise.imageResId != 0) {
                binding.imgExercise.setImageResource(exercise.imageResId);
            } else {
                binding.imgExercise.setImageDrawable(null);
            }

            if (setupMode) {
                binding.panelExecution.setVisibility(View.GONE);
                binding.txtSeriesStatus.setVisibility(View.GONE);
                binding.switchSelect.setVisibility(View.VISIBLE);
                binding.setupSeriesGroup.setVisibility(View.VISIBLE);
                binding.switchSelect.setOnCheckedChangeListener(null);
                binding.switchSelect.setChecked(exercise.isSelecionado);
                binding.switchSelect.setOnCheckedChangeListener((button, checked) -> {
                    exercise.isSelecionado = checked;
                    if (onToggleSelect != null) onToggleSelect.onChange(exercise, checked);
                });
                binding.seriesCountPicker.setMinValue(1);
                binding.seriesCountPicker.setMaxValue(20);
                binding.seriesCountPicker.setWrapSelectorWheel(false);
                binding.seriesCountPicker.setOnValueChangedListener(null);
                binding.seriesCountPicker.setValue(Math.max(1, Math.min(20, exercise.seriesTotal)));
                binding.seriesCountPicker.setOnValueChangedListener((picker, oldValue, newValue) -> {
                    exercise.seriesTotal = newValue;
                    if (onSeriesCountChange != null) {
                        onSeriesCountChange.onChange(exercise, newValue);
                    }
                });
            } else {
                binding.switchSelect.setVisibility(View.GONE);
                binding.setupSeriesGroup.setVisibility(View.GONE);
                binding.panelExecution.setVisibility(View.VISIBLE);
                binding.txtSeriesStatus.setVisibility(View.VISIBLE);
                updateSeriesUI(exercise);
                updateStopwatch();

                if (loadWatcher != null) binding.edtCarga.removeTextChangedListener(loadWatcher);
                binding.edtCarga.setText(exercise.cargaKg > 0
                        ? String.format(Locale.getDefault(), "%s", exercise.cargaKg) : "");
                loadWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            exercise.cargaKg = s == null || s.length() == 0 ? 0.0
                                    : Double.parseDouble(s.toString().replace(',', '.'));
                        } catch (NumberFormatException ignored) {
                            exercise.cargaKg = 0.0;
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                };
                binding.edtCarga.addTextChangedListener(loadWatcher);
                binding.btnViewVideo.setOnClickListener(v -> {
                    if (onItemClick != null) onItemClick.onClick(exercise);
                });
                binding.btnCompleteSet.setOnClickListener(v -> {
                    if (!exercise.isFinished()) {
                        exercise.seriesConcluidas++;
                        updateSeriesUI(exercise);
                        startRestTimer(exercise);
                    }
                });
                binding.btnCompleteExercise.setOnClickListener(v -> {
                    exercise.isCompleted = true;
                    stopExerciseTimer(exercise);
                    stopRestTimer();
                    updateSeriesUI(exercise);
                });
                binding.btnTimerToggle.setOnClickListener(v -> toggleStopwatch(exercise));
                if (exercise.timerRunning) {
                    updateStopwatch();
                    timerHandler.postDelayed(timerTicker, 1000L);
                }
            }
        }

        private void updateSeriesUI(Exercise exercise) {
            if (exercise.isCompleted) {
                binding.txtSeriesStatus.setText("Exercício concluído");
                binding.txtSeriesStatus.setTextColor(ContextCompat.getColor(
                        binding.getRoot().getContext(), R.color.primary));
                binding.btnCompleteSet.setEnabled(false);
                binding.btnCompleteSet.setText("Exercício concluído");
                binding.btnCompleteExercise.setEnabled(false);
                binding.btnCompleteExercise.setText("Concluído");
                return;
            }
            if (exercise.seriesConcluidas >= exercise.seriesTotal) {
                binding.txtSeriesStatus.setText("Todas as séries concluídas");
                binding.txtSeriesStatus.setTextColor(ContextCompat.getColor(
                        binding.getRoot().getContext(), R.color.primary));
                binding.btnCompleteSet.setEnabled(false);
                binding.btnCompleteSet.setText("Séries concluídas");
            } else {
                binding.txtSeriesStatus.setText(String.format(Locale.getDefault(),
                        "Séries: %d de %d", exercise.seriesConcluidas, exercise.seriesTotal));
                binding.txtSeriesStatus.setTextColor(ContextCompat.getColor(
                        binding.getRoot().getContext(), R.color.text_secondary));
                binding.btnCompleteSet.setEnabled(true);
                binding.btnCompleteSet.setText("Concluir série " + (exercise.seriesConcluidas + 1));
            }
            binding.btnCompleteExercise.setEnabled(true);
            binding.btnCompleteExercise.setText("Concluir exercício");
        }

        private void toggleStopwatch(Exercise exercise) {
            if (exercise.timerRunning) {
                stopExerciseTimer(exercise);
                timerHandler.removeCallbacks(timerTicker);
            } else if (!exercise.isFinished()) {
                exercise.timerRunning = true;
                exercise.timerStartedAtRealtime = SystemClock.elapsedRealtime();
                timerHandler.removeCallbacks(timerTicker);
                timerHandler.post(timerTicker);
            }
            updateStopwatch();
        }

        private void stopExerciseTimer(Exercise exercise) {
            if (exercise.timerRunning) {
                exercise.elapsedTimeMillis = exercise.getElapsedTimeMillis(
                        SystemClock.elapsedRealtime());
                exercise.timerRunning = false;
            }
            timerHandler.removeCallbacks(timerTicker);
            binding.btnTimerToggle.setText("Iniciar cronômetro");
            updateStopwatch();
        }

        private void updateStopwatch() {
            if (boundExercise == null || setupMode) return;
            long totalSeconds = boundExercise.getElapsedTimeMillis(
                    SystemClock.elapsedRealtime()) / 1000L;
            long hours = totalSeconds / 3600L;
            long minutes = (totalSeconds % 3600L) / 60L;
            long seconds = totalSeconds % 60L;
            String time = hours > 0
                    ? String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                    : String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            binding.txtExerciseTimer.setText(time);
            binding.btnTimerToggle.setText(boundExercise.timerRunning
                    ? "Pausar cronômetro" : "Iniciar cronômetro");
        }

        private void startRestTimer(Exercise exercise) {
            stopRestTimer();
            binding.containerTimer.setVisibility(View.VISIBLE);
            restTimer = new CountDownTimer(exercise.tempoDescansoSegundos * 1000L, 1000L) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000L);
                    binding.txtTimerCountdown.setText(String.format(Locale.getDefault(),
                            "00:%02d", seconds));
                }

                @Override
                public void onFinish() {
                    binding.txtTimerCountdown.setText("00:00");
                    binding.containerTimer.setVisibility(View.GONE);
                }
            }.start();
        }

        private void stopRestTimer() {
            if (restTimer != null) {
                restTimer.cancel();
                restTimer = null;
            }
            binding.containerTimer.setVisibility(View.GONE);
        }

        void recycle() {
            timerHandler.removeCallbacks(timerTicker);
            stopRestTimer();
            if (loadWatcher != null) {
                binding.edtCarga.removeTextChangedListener(loadWatcher);
                loadWatcher = null;
            }
            boundExercise = null;
        }
    }
}
