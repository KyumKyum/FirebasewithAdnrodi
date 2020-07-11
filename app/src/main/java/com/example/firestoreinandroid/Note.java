package com.example.firestoreinandroid;

import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.Map;

public class Note {

    String documentID;
    String title;
    String description;
    int priority;
    Map<String,Boolean> tags;

    public Note() {
        //public no - arg constructor needed.
    }

    public Note(String title, String description, int priority, Map<String,Boolean> tags) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.tags = tags;
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

    public String getDescription() {
        return description;
    }

    public int getPriority() { return priority; }

    public Map<String,Boolean> getTags() {
        return tags;
    }
}
