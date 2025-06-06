package cr.ac.una.muffetsolitario.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundUtils {
    private static class Holder {
        private static final SoundUtils INSTANCE = new SoundUtils();
    }

    private MediaPlayer bgmPlayer;
    private boolean isMuted = false;
    private double volume = 1.0;
    private final Map<String, MediaPlayer> soundEffects = new HashMap<>();

    private SoundUtils() {
        // Initialize BGM
        URL bgmUrl = getClass().getResource("/cr/ac/una/muffetsolitario/resources/assets/sound/mp3Sans.mp3");
        if (bgmUrl != null) {
            Media bgmMedia = new Media(bgmUrl.toString());
            bgmPlayer = new MediaPlayer(bgmMedia);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.setVolume(volume);
        }

        // Initialize attack sound
        URL attackUrl = getClass().getResource("/cr/ac/una/muffetsolitario/resources/assets/sound/mp3Attack.mp3");
        if (attackUrl != null) {
            Media attackMedia = new Media(attackUrl.toString());
            MediaPlayer attackPlayer = new MediaPlayer(attackMedia);
            soundEffects.put("attack", attackPlayer);
        }

        // Initialize dialog sound
        URL dialogUrl = getClass().getResource("/cr/ac/una/muffetsolitario/resources/assets/sound/mp3Dialog.mp3");
        if (dialogUrl != null) {
            Media dialogMedia = new Media(dialogUrl.toString());
            MediaPlayer dialogPlayer = new MediaPlayer(dialogMedia);
            soundEffects.put("dialog", dialogPlayer);
        }
    }

    public static SoundUtils getInstance() {
        return Holder.INSTANCE;
    }

    public void playBGM() {
        if (bgmPlayer != null && !isMuted) {
            bgmPlayer.play();
        }
    }

    public void stopBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
        }
    }

    public void pauseBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.pause();
        }
    }

    public void playAttackSound() {
        MediaPlayer attackPlayer = soundEffects.get("attack");
        if (attackPlayer != null && !isMuted) {
            attackPlayer.stop();
            attackPlayer.play();
        }
    }

    public void playDialogSound() {
        MediaPlayer dialogPlayer = soundEffects.get("dialog");
        if (dialogPlayer != null && !isMuted) {
            dialogPlayer.stop();
            dialogPlayer.play();
        }
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
        if (bgmPlayer != null) {
            bgmPlayer.setMute(muted);
        }
        soundEffects.values().forEach(player -> player.setMute(muted));
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(this.volume);
        }
        soundEffects.values().forEach(player -> player.setVolume(this.volume));
    }

    public double getVolume() {
        return volume;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
