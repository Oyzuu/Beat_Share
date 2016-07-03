package be.omnuzel.beatshare;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SequencerActivityCopy extends AppCompatActivity {

    private AudioTrack audioTrack;
    private byte[] sound, wavHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        initSounds();
    }

    public void initSounds() {
        try {
            InputStream         is  = getResources().openRawResource(R.raw.iamm_dd2_ride_cymbal_1);
            BufferedInputStream bis = new BufferedInputStream       (is, 8000);
            DataInputStream     dis = new DataInputStream           (bis);

            wavHeader = new byte[44];
            sound     = new byte[dis.available() - wavHeader.length];

            for (int i = 0; i < 44; i++) {
                wavHeader[i] = dis.readByte();
            }

            int i = 0;
            while (dis.available() > 0) {
                sound[i] = dis.readByte();
                i++;
            }

            dis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playSound(View view) {
        if (audioTrack == null) {
            int sampleRate = 44100;

            int minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize,
                    AudioTrack.MODE_STATIC);

            audioTrack.write(sound, 0, sound.length);
        }

        audioTrack.play();
        audioTrack.stop();
        audioTrack.reloadStaticData();
    }
}
