package com.example.firestoreinandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    //Variables
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextPriority;
    private TextView textViewData;

    //References
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteBookRef = db.collection("Notebook");
    private DocumentSnapshot lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPriority = findViewById(R.id.edit_text_priority);
        textViewData = findViewById(R.id.text_view_data);

        executeTransactions();
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
                    }
                });
    }

    public void loadNotes(View v) {

        Query query;

        if (lastResult == null) {
            query = noteBookRef.orderBy("priority")
                    .limit(3);
        } else {
            query = noteBookRef.orderBy("priority")
                    .startAfter(lastResult)
                    .limit(3);
        }

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentID(documentSnapshot.getId());

                            String docId = note.getDocumentID();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            int priority = note.getPriority();

                            data += "ID: " + docId + "\nTitle: " + title + "\nDescription: " + description
                                    + "\nPriority: " + priority + "\n\n";
                        }

                        if (queryDocumentSnapshots.size() > 0) {
                            data += "*******************\n\n";

                            textViewData.append(data);

                            lastResult = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                    }
                });
    }

    private void executeTransactions(){
        WriteBatch batch = db.batch();
        DocumentReference doc1 = noteBookRef.document("New Note");
        DocumentReference doc2 = noteBookRef.document("Not Existing Note");
        DocumentReference doc3 = noteBookRef.document("3BPSYIL67X6BgHN2OYGe");
        DocumentReference doc4 = noteBookRef.document();

        batch.set(doc1,new Note("New Note","New Note",1));
       // batch.update(doc2, "title","Not Existing Note");
        //Error case: Updating not existing document : nothing should be happen.
        batch.delete(doc3);
        batch.set(doc4,new Note("Added Note","Added Note",2));

        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textViewData.setText(e.toString());
            }
        });

    }
}
