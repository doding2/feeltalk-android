package com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.chat.audioVisualizer;

public class DecibelCalculator {

    public static int calculate(byte[] buf, int size) {
        double sum = 0;
        for (int i = 0; i < size/2; i++) {
            double y = (buf[i*2] | buf[i*2+1] << 8);
            sum += y * y;
        }
        double amplitude = sum / (buf.length / 2.0);
        return (int) (20 * Math.log10(amplitude / 32768.0));
    }

}
