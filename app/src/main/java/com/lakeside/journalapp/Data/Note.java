package com.lakeside.journalapp.Data;

public class Note {

    private String noteId;
    private String title;
    private String description;
    private String upDatedAt;

    public Note(){}

    public Note(String title, String description, String upDatedAt) {
        this.title = title;
        this.description = description;
        this.upDatedAt = upDatedAt;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpDatedAt() {
        return upDatedAt;
    }

    public void setUpDatedAt(String upDatedAt) {
        this.upDatedAt = upDatedAt;
    }
}
