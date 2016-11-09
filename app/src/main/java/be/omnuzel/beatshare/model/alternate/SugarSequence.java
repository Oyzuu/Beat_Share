package be.omnuzel.beatshare.model.alternate;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isdc on 9/11/16.
 */

public class SugarSequence extends SugarRecord {

    private String name;
    private String genre;
    private String author;
    private int bpm;
    private SugarLocation location;
    private List<SugarBar> bars;

    public SugarSequence() {

    }

    public void init() {
        bars = new ArrayList<>();

        addBar();
    }

    public void addBar() {
        SugarBar sugarBar = new SugarBar();
        sugarBar.init();
        bars.add(sugarBar);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public SugarLocation getLocation() {
        return location;
    }

    public void setLocation(SugarLocation location) {
        this.location = location;
    }

    public List<SugarBar> getBars() {
        return bars;
    }

    public void setBars(List<SugarBar> bars) {
        this.bars = bars;
    }

    @Override
    public String toString() {
        return "SugarSequence{" +
                "name='" + name + '\'' +
                ", genre='" + genre + '\'' +
                ", author='" + author + '\'' +
                ", bpm=" + bpm +
                ", location=" + location +
                ", bars=" + bars +
                '}';
    }

    public void sugarSave() {
        for (SugarBar bar : bars) {
            bar.sugarSave();
        }

        save();
    }
}
