package com.example.firestoreinandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Variables
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextPriority;
    private EditText editTextTags;
    private TextView textViewData;

    //References
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteBookRef = db.collection("Notebook");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPriority = findViewById(R.id.edit_text_priority);
        textViewData = findViewById(R.id.text_view_data);
        editTextTags = findViewById(R.id.edit_text_tags);

        updateArray();
    }



    public void addNote(View v) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (editTextPriority.length() == 0) {
            editTextPriority.setText("0");
        }

        int priority = Integer.parseInt(editTextPriority.getText().toString());

        String tagInput = editTextTags.getText().toString();
        String[] tagArray = tagInput.split("\\s*,\\s*");
        List<String> tags = Arrays.asList(tagArray);

        Note note = new Note(title, description, priority, tags);

        noteBookRef.add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadNotes(View v) {
        noteBookRef.whereArrayContains("tags","tag5")//Before sdk 17, query on array was not possible
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";

                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentID(documentSnapshot.getId());
                            String docId = note.getDocumentID();

                            data += "ID: " + docId;

                            for(String tag: note.getTags()){
                                data += "\n-"+tag;
                            }

                            data += "\n\n";
                        }

                        textViewData.setText(data);
                    }
                });
    }

    private void updateArray(){
        noteBookRef.document("FbVGmusOXNFJz0sBL14z")
                //.update("tags", FieldValue.arrayUnion("New Value"));
                //Adding tags; it will be added only if the element called 'new Tags' doesn't exist.
                .update("tags",FieldValue.arrayRemove("New Value"));
                //Removing tags; Not removing in position - problematic in multi-user condition.
    }
}
