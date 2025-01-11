package gh2;

import deque.ArrayDeque;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public ArrayDeque<GuitarString> Guitars;
    public static String keyboard;
    public static final double CONCERT_A = 440.0;

    public GuitarHero() {
        Guitars = new ArrayDeque<>();
        keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        for (int i = 0; i < keyboard.length(); i++) {
            double frequency = CONCERT_A * Math.pow(2, (i - 24) / 12.0);
            Guitars.addLast(new GuitarString(frequency));
        }
    }

    public static void main(String[] args) {
        GuitarHero guitarHero = new GuitarHero();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (keyboard.indexOf(key) != -1) {
                    GuitarString choose = guitarHero.Guitars.get(keyboard.indexOf(key));
                    choose.pluck();
                }
            }
            double sample = 0.0;
            for (int i = 0; i < guitarHero.Guitars.size(); i++) {
                sample += guitarHero.Guitars.get(i).sample();
            }
            StdAudio.play(sample);
            for (int i = 0; i < guitarHero.Guitars.size(); i++) {
                guitarHero.Guitars.get(i).tic();
            }
        }
    }
}
