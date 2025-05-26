
package cr.ac.una.muffetsolitario.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundUtils {

    // Initialization-on-demand holder idiom for thread-safe singleton
    private static class Holder {
        private static final SoundUtils INSTANCE = new SoundUtils();
    }

    private SoundUtils() {
        // private constructor to prevent instantiation
    }

    public static SoundUtils getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Plays a sound effect based on the effect name.
     * @param effect The name of the sound effect to play.
     */
    public void playSound(String effect) {
        if (effect.equals("buttonPressed")) {
            playMedia("/cr/ac/una/matchmaker/resources/buttonSound.wav");
        }
    }

    /**
     * Helper method to load and play a media file from resources.
     * @param resourcePath The path to the media resource.
     */
    private void playMedia(String resourcePath) {
        URL url = getClass().getResource(resourcePath);
        if (url != null) {
            Media media = new Media(url.toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } else {
            // Optionally log: resource not found
            // System.err.println("Sound resource not found: " + resourcePath);
        }
    }
}
