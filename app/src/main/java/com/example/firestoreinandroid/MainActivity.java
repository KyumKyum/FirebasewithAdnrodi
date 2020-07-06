package com.example.firestoreinandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    //Statics
    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";


    //Variables
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextPriority;
    private TextView textViewData;

    //References
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteBookRef = db.collection("Notebook");
    private DocumentReference noteRef = db.collection("Notebook").document("My First Note");
    //db.document("Notebook/My First Note")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPriority = findViewById(R.id.edit_text_priority);
        textViewData = findViewById(R.id.text_view_data);

    }

    @Override
    protected void onStart() {
        super.onStart();

        noteBookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }

                StringBuilder data = new StringBuilder();

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentID(documentSnapshot.getId());

                    String docId = note.getDocumentID();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    int priority = note.getPriority();

                    data.append("ID: ").append(docId).append("\nTitle: ").append(title)
                            .append("\nDescription: ").append(description).append("\nPriority: ")
                            .append(priority).append("\n\n");

                    //noteBookRef.document(docId) to get reference for particular id applicable at .update or .delete operation

                }

                textViewData.setText(data);

            }
        });
    }


    public void addNote(View v) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (editTextPriority.length() == 0) {
            editTextPriority.setText("0");
        }

        int priority = Integer.parseInt(editTextPriority.getText().toString());

        //Map<String,Object> note = new HashMap<>();
        //note.put(KEY_TITLE,title);
        //note.put(KEY_DESCRIPTION,description);

        Note note = new Note(title, description, priority);

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
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void loadNotes(View v) {
        Task task1 = noteBookRef.whereLessThan("priority", 2)
                .orderBy("priority", Query.Direction.ASCENDING)
                .get();

        Task task2 = noteBookRef.whereGreaterThan("priority", 2)
                .orderBy("priority", Query.Direction.ASCENDING)
                .get();

        Task<List<QuerySnapshot>> taskAll = Tasks.whenAllSuccess(task1, task2);
        taskAll.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                String data = "";
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots) {
                        Note note = documentSnapshots.toObject(Note.class);
                        note.setDocumentID(documentSnapshots.getId());

                        String docId = note.getDocumentID();
                        String title = note.getTitle();
                        String description = note.getDescription();
                        int priority = note.getPriority();

                        data += "ID: " + docId + "\nTitle: " + title + "\nDescription" + description +
                                "\nPriority: " + priority + "\n\n";

                    }
                }

                textViewData.setText(data);
            }
        });

    }
}
