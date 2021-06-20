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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import quran.app.admincollageapp.NoticeData;
import quran.app.admincollageapp.R;
import quran.app.admincollageapp.UploadNotice;

public class AddTeacher extends AppCompatActivity {
    private ImageView addTeacherImage;
    private EditText addTeacherName,addTeacherEmail,addTeacherPost;
    private Spinner addTeacherCategory;
    private Button addTeacherButton;
    private final int REQ=1;
    private Bitmap bitmap=null;
    private String category;
    String name,email,post,downloadurl="";
    private ProgressDialog pd;

    private StorageReference storageReference;
    private DatabaseReference reference,dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        pd = new ProgressDialog(this);
        addTeacherImage= findViewById(R.id.addTeacherImageId);
        addTeacherName= findViewById(R.id.addTeachernameId);
        addTeacherEmail= findViewById(R.id.addTeacheremailId);
        addTeacherPost= findViewById(R.id.addTeacherpostId);
        addTeacherCategory= findViewById(R.id.addTeacherCategoryId);
        addTeacherButton= findViewById(R.id.AddTeacherButtonId);

        reference= FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference = FirebaseStorage.getInstance().getReference();


        String [] items= new String[]{"Select Category","one","two","three"};
        addTeacherCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));
        addTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=addTeacherCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addTeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalary();
            }
        });
        addTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });
    }

    private void checkValidation() {
        name=addTeacherName.getText().toString();
        email=addTeacherEmail.getText().toString();
        post=addTeacherPost.getText().toString();
        if(name.isEmpty()){
            addTeacherName.setError("Name is Empty");
            addTeacherName.requestFocus();
        }else if(email.isEmpty()){
            addTeacherEmail.setError("Email is Empty");
            addTeacherEmail.requestFocus();
        }else if(post.isEmpty()){
            addTeacherPost.setError("Post is Empty");
            addTeacherPost.requestFocus();
        }else if(category.equals("Select Category")){
            Toast.makeText(this, "Please Select Teacher Category", Toast.LENGTH_SHORT).show();
        }else if(bitmap == null){
            pd.setMessage("Uploading...");
            pd.show();
            uploadData();
        }else{
            pd.setMessage("Uploading...");
            pd.show();
            uploadImage();
        }
    }

    private void uploadData() {

        dbRef =reference.child(category);
        final String uniquekey =dbRef.push().getKey();

        TeacherData teacherData = new TeacherData(name,email,post,downloadurl,uniquekey);
        dbRef.child(uniquekey).setValue(teacherData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this, "Save successful Teacher Addes.", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this, "Save Unsuccessful.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void uploadImage() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimage=baos.toByteArray();
        final StorageReference filepath;
        filepath = storageReference.child("teachers").child(finalimage+"jpg");
        final UploadTask uploadTask =filepath.putBytes(finalimage);
        uploadTask.addOnCompleteListener(AddTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    uploadData();

                                }
                            });
                        }
                    });
                }else{
                    pd.dismiss();
                    Toast.makeText(AddTeacher.this, "something went wrong!!", Toast.LENGTH_SHORT).show();
                }

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
            addTeacherImage.setImageBitmap(bitmap);
        }
    }
}