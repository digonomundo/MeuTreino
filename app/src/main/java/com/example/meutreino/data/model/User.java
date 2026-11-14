package com.example.meutreino.data.model;

import java.util.Calendar;

public class User {
    public final String id;
    public final String nome;
    public final double peso;
    public final double altura;
    public final int idade;
    public final String dataNascimento;

    public User() {
        this("", "", 0.0, 0.0, 0, "");
    }

    public User(String id, String nome, double peso, double altura, int idade) {
        this(id, nome, peso, altura, idade, "");
    }

    public User(String id, String nome, double peso, double altura, int idade,
                String dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.peso = peso;
        this.altura = altura;
        this.idade = idade;
        this.dataNascimento = dataNascimento == null ? "" : dataNascimento;
    }

    public int getIdade() {
        int calculatedAge = calculateAge(dataNascimento);
        return calculatedAge >= 0 ? calculatedAge : idade;
    }

    public static int calculateAge(String isoDate) {
        if (isoDate == null || !isoDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return -1;
        }
        try {
            String[] parts = isoDate.split("-");
            Calendar birthDate = Calendar.getInstance();
            birthDate.clear();
            birthDate.setLenient(false);
            birthDate.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1,
                    Integer.parseInt(parts[2]));
            birthDate.getTimeInMillis();
            Calendar today = Calendar.getInstance();
            if (birthDate.after(today)) return -1;

            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            int currentMonth = today.get(Calendar.MONTH);
            int birthMonth = birthDate.get(Calendar.MONTH);
            if (currentMonth < birthMonth
                    || (currentMonth == birthMonth
                    && today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }
            return age;
        } catch (IllegalArgumentException exception) {
            return -1;
        }
    }

    public double getImc() {
        return altura > 0 ? peso / (altura * altura) : 0.0;
    }

    public double getConsumoAguaLitros() {
        return (peso * 35) / 1000.0;
    }

    public String getImcDescricao() {
        double imc = getImc();
        if (imc < 18.5) return "Abaixo do peso";
        if (imc < 25.0) return "Peso saudável";
        if (imc < 30.0) return "Sobrepeso";
        if (imc < 35.0) return "Obesidade Grau I";
        if (imc < 40.0) return "Obesidade Grau II";
        return "Obesidade Mórbida";
    }
}
