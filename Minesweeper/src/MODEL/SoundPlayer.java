/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author ADMIN
 */
public class SoundPlayer {

    private Clip clip;
    private static SoundPlayer instance;

    public static SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }
        return instance;
    }

//    public void playMusic(String filepath) {
//        try {
//            if (!hasPlayed) {
//                AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(filepath));
//                clip = AudioSystem.getClip();
//                clip.open(audioInput);
//                clip.start();
//                clip.loop(Clip.LOOP_CONTINUOUSLY); // lặp vô hạn
//                hasPlayed = true;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    public void playMusic(String musicLocation) {
        try {
            // Nếu clip đang chạy và đường dẫn nhạc giống nhau => không phát lại
            if (clip != null && clip.isRunning()) {
                return;
            }

            // Nếu có clip cũ => dừng và đóng lại
            if (clip != null) {
                clip.stop();
                clip.close();
            }

            File musicPath = new File(musicLocation);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start(); // chỉ phát 1 lần
            } else {
                System.out.println("Không tìm thấy file nhạc.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}
