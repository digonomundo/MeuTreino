package com.example.meutreino.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meutreino.R;
import com.example.meutreino.data.repository.AuthRepository;
import com.example.meutreino.ui.auth.LoginActivity;
import com.example.meutreino.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final AuthRepository authRepository = new AuthRepository();
    private final Runnable navigate = () -> {
        Class<?> destination = authRepository.isUserLoggedIn()
                ? MainActivity.class : LoginActivity.class;
        startActivity(new Intent(this, destination));
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_splash);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        handler.postDelayed(navigate, 800);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(navigate);
        super.onDestroy();
    }
}
