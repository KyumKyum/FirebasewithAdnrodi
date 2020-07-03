package com.example.firestoreinandroid;

import com.google.firebase.firestore.Exclude;

public class Note {

    String documentID;
    String title;
    String description;
    int priority;

    public Note() {
        //public no - arg constructor needed.
    }

    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
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

    public void setDescription(String description) { this.description = description; }

    public int getPriority() { return priority; }

    public void setPriority(int priority) { this.priority = priority; }
}
