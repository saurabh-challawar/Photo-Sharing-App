package com.example.firebasephotoupload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    private DatabaseReference databaseReference;
    private List<PhotoUpload> uploadList;
    private ProgressBar progressBar,progressBarItem;

    private ValueEventListener mDBListener;

    private FirebaseStorage storageReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ImagesActivity.this));
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
//        progressBarItem=(ProgressBar)findViewById(R.id.progressBarItem);

        uploadList = new ArrayList<>();

        imageAdapter = new ImageAdapter(ImagesActivity.this,uploadList);

        recyclerView.setAdapter(imageAdapter);

        imageAdapter.setOnItemClickListener(ImagesActivity.this);


        databaseReference= FirebaseDatabase.getInstance().getReference("Uploads");
        storageReference = FirebaseStorage.getInstance();

        mDBListener= databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                uploadList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PhotoUpload upload = dataSnapshot.getValue(PhotoUpload.class);
                    upload.setKey(dataSnapshot.getKey());
                    uploadList.add(upload);
                }

                imageAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImagesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhateverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        PhotoUpload selectedItem = uploadList.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference selectedRef = storageReference.getReferenceFromUrl(selectedItem.getImageUri());
        selectedRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(ImagesActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImagesActivity.this, "Error Occured.Item not deleted.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDBListener);
    }
}
