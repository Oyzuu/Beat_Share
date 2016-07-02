package be.omnuzel.beatshare;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SequencerActivity extends AppCompatActivity {

    AudioTrack audioTrack;
    byte[] sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        InputStream is = getResources().openRawResource(R.raw.iamm_e1_electric_snare);

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                5000,
                AudioTrack.MODE_STATIC);

        try {
            sound = new byte[is.available()];
            sound = convertStreamToByteArray(is);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        audioTrack.write(sound, 0, sound.length);
    }

    public void playSound(View view) {
        audioTrack.play();
        audioTrack.reloadStaticData();
    }

    public byte[] convertStreamToByteArray(InputStream is) throws IOException {
        byte[] buffer = new byte[10240];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 1;

        while ((i = is.read(buffer)) > 0) {
            baos.write(buffer);
        }

        return baos.toByteArray();
    }
}
