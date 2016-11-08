package be.omnuzel.beatshare.controller.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.util.Random;

public class ChocolateSaltyBalls {
    public static ChocolateSaltyBalls instance;

    public static ChocolateSaltyBalls getInstance() {
        if (instance == null)
            instance = new ChocolateSaltyBalls();

        return instance;
    }

    private final char[] upperLetters = new char[26];
    private final char[] lowerLetters = new char[26];

    public ChocolateSaltyBalls() {
        for (int charNumber = 65, i = 0; charNumber <= 90; charNumber++, i++)
            upperLetters[i] = (char) charNumber;

        for (int charNumber = 97, i = 0; charNumber <= 122; charNumber++, i++)
            lowerLetters[i] = (char) charNumber;
    }

    public String generateSalt() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            if ((int) (Math.random() + .5) == 0)
                stringBuilder.append(lowerLetters[random.nextInt(25)]);
            else
                stringBuilder.append(upperLetters[random.nextInt(25)]);
        }

        Log.i("SALT", stringBuilder.toString());
        return stringBuilder.toString();
    }

    public String hash(String stringToHash) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(stringToHash.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();

        for (byte mByte : hash) {
            String h = Integer.toHexString(0xFF & mByte);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }

        Log.i("HASH", hexString.toString());
        return hexString.toString();
    }
}
