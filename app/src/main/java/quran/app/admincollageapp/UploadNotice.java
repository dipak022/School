package quran.app.admincollageapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadNotice extends AppCompatActivity {
    private CardView addImage;
    private ImageView noticeimageView;
    private EditText noticetitle;
    private Button uploadnoticebutton;
    private final int REQ=1;
    private Bitmap bitmap;
    private DatabaseReference reference,dbRef;
    private StorageReference storageReference;
    String downloadurl="";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(this);
        addImage = findViewById(R.id.AddImageID);
        noticeimageView = findViewById(R.id.noticeImageViewId);
        noticetitle = findViewById(R.id.noticeTitleId);//noticeImageViewId
        uploadnoticebutton = findViewById(R.id.uploadNoticeButtonId);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalary();
            }
        });
        uploadnoticebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticetitle.getText().toString().isEmpty()){
                    noticetitle.setError("Title is Empty");
                    noticetitle.requestFocus();
                }else if(bitmap == null){
                      uploadData();
                    //uploadImage();

                }
                else{
                    uploadImage();
                }
            }
        });
    }

    private void uploadImage() {
        pd.setMessage("Uploading...");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimage=baos.toByteArray();
        final StorageReference filepath;
        filepath = storageReference.child("notice").child(finalimage+"jpg");
        final UploadTask uploadTask =filepath.putBytes(finalimage);
        uploadTask.addOnCompleteListener(UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                    Toast.makeText(UploadNotice.this, "something went wrong!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void uploadData() {

        dbRef =reference.child("notice");
        final String uniquekey =dbRef.push().getKey();
        String title = noticetitle.getText().toString();
        Calendar calendar =Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
        String date =simpleDateFormat.format(calendar.getTime());

        Calendar calendar1 =Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm a");
        String time =simpleDateFormat1.format(calendar1.getTime());
        NoticeData noticeData = new NoticeData(title,downloadurl,date,time,uniquekey);
        dbRef.child(uniquekey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(UploadNotice.this, "Save successful and upload data.", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadNotice.this, "Save Unsuccessful.", Toast.LENGTH_SHORT).show();
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
            noticeimageView.setImageBitmap(bitmap);
        }
    }
}