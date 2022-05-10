package com.hemant.music.goonj;

public class AudioModel {
    String artist;
    String title;
    String duration;
    String path;
    String album;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    ;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public AudioModel(String artist, String title, String duration, String path, String album) {
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.path = path;
        this.album = album;
    }
}
