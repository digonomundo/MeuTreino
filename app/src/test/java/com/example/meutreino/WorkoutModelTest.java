package com.example.meutreino;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.meutreino.data.model.Exercise;
import com.example.meutreino.data.model.ExerciseCatalog;
import com.example.meutreino.data.model.User;
import com.example.meutreino.data.model.WorkoutCategory;

import org.junit.Test;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class WorkoutModelTest {
    @Test
    public void userCalculatesHealthIndicators() {
        User user = new User("u1", "Ana", 68.0, 1.70, 30);

        assertEquals(68.0 / (1.70 * 1.70), user.getImc(), 0.001);
        assertEquals(2.38, user.getConsumoAguaLitros(), 0.001);
        assertEquals("Peso saudável", user.getImcDescricao());
    }

    @Test
    public void ageIsCalculatedFromBirthDateAndRejectsInvalidDates() {
        Calendar today = Calendar.getInstance();
        String birthDate = String.format(java.util.Locale.US, "%04d-01-01",
                today.get(Calendar.YEAR) - 30);
        User user = new User("u1", "Ana", 68.0, 1.70, 0, birthDate);

        assertEquals(30, user.getIdade());
        assertEquals(-1, User.calculateAge("2024-02-31"));
    }

    @Test
    public void exerciseCompletionRespectsConfiguredSeriesAndManualCompletion() {
        Exercise exercise = new Exercise("e1", "exercise", "Agachamento", "Com barra",
                WorkoutCategory.PERNAS, 0, "video1", "", false, 4, 0, 0.0, 60);

        assertFalse(exercise.isFinished());
        exercise.seriesConcluidas = 3;
        assertFalse(exercise.isFinished());
        exercise.isCompleted = true;
        assertTrue(exercise.isFinished());
    }

    @Test
    public void catalogHasAtLeastSixExercisesPerCategoryAndUniqueKeys() {
        for (WorkoutCategory category : WorkoutCategory.values()) {
            assertTrue(category.title + " should have at least six exercises",
                    ExerciseCatalog.getByCategory(category).size() >= 6);
        }

        Set<String> ids = new HashSet<>();
        Set<String> firestoreKeys = new HashSet<>();
        Set<String> videoKeys = new HashSet<>();
        for (Exercise exercise : ExerciseCatalog.ALL_EXERCISES) {
            assertTrue("Duplicate exercise id: " + exercise.id, ids.add(exercise.id));
            assertTrue("Duplicate Firestore key: " + exercise.firestoreKey,
                    firestoreKeys.add(exercise.firestoreKey));
            assertTrue("Duplicate video key: " + exercise.videoKey,
                    videoKeys.add(exercise.videoKey));
        }
    }

    @Test
    public void abdominalExercisesUseMatchingPhotosAndDescriptions() {
        Exercise declinedLegRaise = ExerciseCatalog.getByVideoKey("video15");
        Exercise declinedCrunch = ExerciseCatalog.getByVideoKey("video16");
        Exercise ballCrunch = ExerciseCatalog.getByVideoKey("video49");
        Exercise declinedBenchCrunch = ExerciseCatalog.getByVideoKey("video50");
        Exercise cableCrunch = ExerciseCatalog.getByVideoKey("video51");

        assertEquals(9, ExerciseCatalog.getByCategory(WorkoutCategory.ABDOMEN).size());
        assertEquals(R.drawable.abdominal_infra_na_prancha_declinada,
                declinedLegRaise.imageResId);
        assertEquals(R.drawable.abdominal_obliquo_na_prancha_declinada,
                declinedCrunch.imageResId);
        assertTrue(declinedCrunch.instrucoes.contains("sem fazer rotação"));
        assertEquals(R.drawable.abdominal_supra_na_bola, ballCrunch.imageResId);
        assertEquals(R.drawable.abdominal_supra_na_prancha_declinada,
                declinedBenchCrunch.imageResId);
        assertEquals(R.drawable.abdominal_supra_no_cabo, cableCrunch.imageResId);
        assertEquals(0, ExerciseCatalog.getByVideoKey("video31").imageResId);
        assertEquals(0, ExerciseCatalog.getByVideoKey("video34").imageResId);
        assertEquals(0, ExerciseCatalog.getByVideoKey("video37").imageResId);
        assertEquals(0, ExerciseCatalog.getByVideoKey("video41").imageResId);
    }
}
