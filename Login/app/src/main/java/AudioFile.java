package com.example.login;

public class AudioFile {
    public String url="";
    public String audiofilename="";

    public String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AudioFile(){}


    public AudioFile(String url,String audiofilename) {
        this.url = url;
        this.audiofilename=audiofilename;

    }

    public String getAudiofilename() {
        return audiofilename;
    }

    public void setAudiofilename(String audiofilename) {
        this.audiofilename = audiofilename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
