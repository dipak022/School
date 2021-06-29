package quran.app.admincollageapp.faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import quran.app.admincollageapp.R;

public class UpdateTeacherActivity extends AppCompatActivity {
    private ImageView UpdateTeacherImage;
    private EditText UpdateTeacherNmae;
    private EditText UpdateTeacherEmail;
    private EditText UpdateTeacherPost;
    private Button UpdateTeacherButton;
    private Button DeleteTeacherButton;
    private String name,email,post,image;
    private final int REQ =1;
    private Bitmap bitmap=null;
    private ProgressDialog pd;
    String downloadurl="",uniqueKey,category;

    private StorageReference storageReference;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipdate_teacher);

        reference= FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        post=getIntent().getStringExtra("post");
        image=getIntent().getStringExtra("image");
        uniqueKey = getIntent().getStringExtra("key");
        category =getIntent().getStringExtra("category");


        UpdateTeacherImage = findViewById(R.id.UpdateTeacherImage);
        UpdateTeacherNmae = findViewById(R.id.UpdateTeacherNmae);
        UpdateTeacherEmail = findViewById(R.id.UpdateTeacherEmail);
        UpdateTeacherPost = findViewById(R.id.UpdateTeacherPost);
        UpdateTeacherButton = findViewById(R.id.UpdateTeacherButton);
        DeleteTeacherButton = findViewById(R.id.DeleteTeacherButton);

        try {
            Picasso.get().load(image).into(UpdateTeacherImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UpdateTeacherNmae.setText(name);
        UpdateTeacherEmail.setText(email);
        UpdateTeacherPost.setText(post);

        UpdateTeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalary();
            }
        });
        UpdateTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                name=UpdateTeacherNmae.getText().toString();
                email=UpdateTeacherEmail.getText().toString();
                post=UpdateTeacherPost.getText().toString();


                checkVAlidation();
            }
        });
        DeleteTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });
    }

    private void deleteData() {
        reference.child(category).child(uniqueKey).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UpdateTeacherActivity.this, "Teacher Delete Successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateTeacherActivity.this,UpdateFaculty.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK| intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, "Something Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkVAlidation() {
        if (name.isEmpty()){
            UpdateTeacherNmae.setError("Name is Empty");
            UpdateTeacherNmae.requestFocus();
        }else if (email.isEmpty()){
            UpdateTeacherEmail.setError("Email is Empty");
            UpdateTeacherEmail.requestFocus();
        }else if (post.isEmpty()){
            UpdateTeacherPost.setError("Post is Empty");
            UpdateTeacherPost.requestFocus();
        }else if(bitmap== null){
            UpdateData(image);
        }else{
            uploadImage();
        }
    }

    private void uploadImage() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimage=baos.toByteArray();
        final StorageReference filepath;
        filepath = storageReference.child("teachers").child(finalimage+"jpg");
        final UploadTask uploadTask =filepath.putBytes(finalimage);
        uploadTask.addOnCompleteListener(UpdateTeacherActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    downloadurl=String.valueOf(uri);
                                    UpdateData(downloadurl);

                                }
                            });
                        }
                    });
                }else{
                    pd.dismiss();
                    Toast.makeText(UpdateTeacherActivity.this, "something went wrong!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void UpdateData(String s) {

        HashMap hp = new HashMap();
        hp.put("name",name);
        hp.put("email",email);
        hp.put("post",post);
        hp.put("image",s);




        reference.child(category).child(uniqueKey).updateChildren(hp).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UpdateTeacherActivity.this, "Teacher Update Successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateTeacherActivity.this,UpdateFaculty.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK| intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void openGalary() {
        Intent picImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picImage,REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ && resultCode== RESULT_OK){
            Uri uri =data.getData();
            try {
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UpdateTeacherImage.setImageBitmap(bitmap);
        }
    }
}