package com.example.firebasephotoupload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMG_REQUEST = 1;
    private Button btnChoose,btnUpload,btnLogout;
    private EditText fileName;
    private TextView showUpload;
    private ProgressBar progressBar;
    private ImageView imageView;

    private Uri imageUri;

    private FirebaseAuth fAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask<UploadTask.TaskSnapshot> uploadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChoose=(Button)findViewById(R.id.btnChoose);
        btnUpload=(Button)findViewById(R.id.btnUpload);
        fileName=(EditText) findViewById(R.id.fileName);
        showUpload=(TextView)findViewById(R.id.showUpload);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        imageView=(ImageView)findViewById(R.id.imageView);

        btnLogout=(Button)findViewById(R.id.logout);

        fAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference("Uploads/"+fAuth.getUid()+"/");
        databaseReference= FirebaseDatabase.getInstance().getReference("Uploads");

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadProgress !=null && uploadProgress.isInProgress()){
                    Toast.makeText(MainActivity.this, "Upload in process.", Toast.LENGTH_SHORT).show();
                }
                else {
                    fileUpload();
                }
            }
        });

        showUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Toast.makeText(MainActivity.this,"Logged Out.",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,Login.class));
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void fileUpload() {
        showUpload.setEnabled(false);
        if(imageUri!=null){
            final StorageReference fileRef= storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

            uploadProgress= fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                   progressBar.setProgress(0);
                                }
                            },500);
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    PhotoUpload photoUpload = new PhotoUpload(fileName.getText().toString().trim(), uri.toString(), fAuth.getCurrentUser().getEmail());
                                    //String uploadId=databaseReference.push().getKey();
                                    databaseReference.child(fAuth.getUid()).setValue(photoUpload);
                                    Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                    showUpload.setEnabled(true);
                                }
                            });
//                            PhotoUpload photoUpload = new PhotoUpload(fileName.getText().toString().trim(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//                            String uploadId=databaseReference.push().getKey();
//                            databaseReference.child(uploadId).setValue(photoUpload);
//                            Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            showUpload.setEnabled(true);
                            progressBar.setProgress(0);

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int)progress);
                        }
                    });
        }
        else{
            Toast.makeText(this, "Select an image first.",Toast.LENGTH_SHORT).show();
            showUpload.setEnabled(true);
        }
    }

    private void chooseFile() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            imageView.setImageURI(imageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this,Login.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this,Login.class));
        }
    }
}
