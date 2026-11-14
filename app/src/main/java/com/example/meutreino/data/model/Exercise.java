package com.example.meutreino.data.model;

public class Exercise {
    public final String id;
    public final String firestoreKey;
    public final String titulo;
    public final String modalidade;
    public final WorkoutCategory categoria;
    public final int imageResId;
    public final String videoKey;
    public final String instrucoes;
    public boolean isSelecionado;
    public int seriesTotal;
    public int seriesConcluidas;
    public double cargaKg;
    public final int tempoDescansoSegundos;
    public boolean isCompleted;
    public boolean timerRunning;
    public long elapsedTimeMillis;
    public long timerStartedAtRealtime;

    public Exercise(
            String id,
            String firestoreKey,
            String titulo,
            String modalidade,
            WorkoutCategory categoria,
            int imageResId,
            String videoKey,
            String instrucoes
    ) {
        this(id, firestoreKey, titulo, modalidade, categoria, imageResId, videoKey, instrucoes,
                false, 3, 0, 0.0, 60);
    }

    public Exercise(
            String id,
            String firestoreKey,
            String titulo,
            String modalidade,
            WorkoutCategory categoria,
            int imageResId,
            String videoKey,
            String instrucoes,
            boolean isSelecionado,
            int seriesTotal,
            int seriesConcluidas,
            double cargaKg,
            int tempoDescansoSegundos
    ) {
        this.id = id;
        this.firestoreKey = firestoreKey;
        this.titulo = titulo;
        this.modalidade = modalidade;
        this.categoria = categoria;
        this.imageResId = imageResId;
        this.videoKey = videoKey;
        this.instrucoes = instrucoes;
        this.isSelecionado = isSelecionado;
        this.seriesTotal = seriesTotal;
        this.seriesConcluidas = seriesConcluidas;
        this.cargaKg = cargaKg;
        this.tempoDescansoSegundos = tempoDescansoSegundos;
    }

    public boolean isFinished() {
        return isCompleted || seriesConcluidas >= seriesTotal;
    }

    public long getElapsedTimeMillis(long nowRealtime) {
        return elapsedTimeMillis + (timerRunning ? nowRealtime - timerStartedAtRealtime : 0L);
    }

    public Exercise copy() {
        Exercise copy = new Exercise(id, firestoreKey, titulo, modalidade, categoria, imageResId,
                videoKey, instrucoes, isSelecionado, seriesTotal, seriesConcluidas, cargaKg,
                tempoDescansoSegundos);
        copy.isCompleted = isCompleted;
        copy.timerRunning = timerRunning;
        copy.elapsedTimeMillis = elapsedTimeMillis;
        copy.timerStartedAtRealtime = timerStartedAtRealtime;
        return copy;
    }
}
