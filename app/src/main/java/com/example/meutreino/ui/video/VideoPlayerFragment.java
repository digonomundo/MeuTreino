package com.example.meutreino.ui.video;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.meutreino.R;
import com.example.meutreino.data.model.Exercise;
import com.example.meutreino.data.model.ExerciseCatalog;
import com.example.meutreino.databinding.FragmentVideoTreinosBinding;

public class VideoPlayerFragment extends Fragment {
    private FragmentVideoTreinosBinding binding;
    private ExoPlayer player;
    private Uri videoUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVideoTreinosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String videoKey = getArguments() == null
                ? "video1" : getArguments().getString("videoKey", "video1");
        loadExerciseVideo(videoKey);
    }

    private void loadExerciseVideo(String videoKey) {
        Exercise exercise = ExerciseCatalog.getByVideoKey(videoKey);
        if (exercise == null) {
            Toast.makeText(requireContext(), "Exercício não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.txtDescVideo.setText(exercise.titulo + " (" + exercise.modalidade + ")\n\n"
                + exercise.instrucoes);

        int videoResId = getVideoResourceId(videoKey);
        if (videoResId == 0) {
            binding.videoContainer.setVisibility(View.GONE);
            binding.txtVideoUnavailable.setVisibility(View.VISIBLE);
            return;
        }
        binding.videoContainer.setVisibility(View.VISIBLE);
        binding.txtVideoUnavailable.setVisibility(View.GONE);
        videoUri = Uri.parse("android.resource://" + requireActivity().getPackageName()
                + "/" + videoResId);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (videoUri != null) initializePlayer(videoUri);
    }

    private void initializePlayer(Uri uri) {
        releasePlayer();
        player = new ExoPlayer.Builder(requireContext()).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                if (binding != null) {
                    binding.videoContainer.setVisibility(View.GONE);
                    binding.txtVideoUnavailable.setVisibility(View.VISIBLE);
                    binding.txtVideoUnavailable.setText("Não foi possível carregar este vídeo.");
                }
            }
        });
        binding.videoSelecionado.setPlayer(player);
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.play();
    }

    private int getVideoResourceId(String videoKey) {
        switch (videoKey) {
            case "video1": return R.raw.supinoretobarra;
            case "video2": return R.raw.supino_inclinado_barra;
            case "video3": return R.raw.supino_declinado_barra;
            case "video4": return R.raw.supino_reto_halter;
            case "video5": return R.raw.supino_inclinado_halter;
            case "video6": return R.raw.supino_declinado_halter;
            case "video7": return R.raw.supino_reto_barrag;
            case "video8": return R.raw.supino_inclinado_barrag;
            case "video9": return R.raw.crucifixo_sentado;
            case "video10": return R.raw.crucifixo_reto_halter;
            case "video11": return R.raw.crucifixo_inclinado_halter;
            case "video12": return R.raw.crucifixo_articulado_maquina;
            case "video34": return R.raw.triceps_com_corda_na_roldana;
            default: return 0;
        }
    }

    @Override
    public void onStop() {
        releasePlayer();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        releasePlayer();
        videoUri = null;
        binding = null;
        super.onDestroyView();
    }

    private void releasePlayer() {
        if (player == null) return;
        if (binding != null) binding.videoSelecionado.setPlayer(null);
        player.release();
        player = null;
    }
}
