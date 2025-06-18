/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

/**
 *
 * @author nguye
 */
public class Score implements Comparable<Score> {
    private String name;
    private int time;

    public Score(String name, int time) {
        this.name = name;
        this.time = time;
    }

    public String getName() { return name; }
    public int getTime() { return time; }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.time, other.time);
    }

    @Override
    public String toString() {
        return name + " - " + time + " giây";
    }
    
}
