package quran.app.admincollageapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

public class UploadPDFactivaty extends AppCompatActivity {

    private CardView AddPdf;
    private EditText Pdftitle;
    private Button uploadPdfbutton;
    private TextView textView;
    private final int REQ=1;
    private Uri pdfData;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    String downloadurl="";
    private ProgressDialog pd;
    private String PDFName;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_p_d_factivaty);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(this);
        AddPdf = findViewById(R.id.AddPdfID);
        Pdftitle = findViewById(R.id.PdfTitleId);//noticeImageViewId
        uploadPdfbutton = findViewById(R.id.uploadPdfButtonId);
        textView= findViewById(R.id.PdfTextViewId);
        AddPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalary();
            }
        });
        uploadPdfbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title= Pdftitle.getText().toString();
                if (title.isEmpty()){
                    Pdftitle.setError("Title is Empty");
                    Pdftitle.requestFocus();
                }else if(pdfData == null){

                    Toast.makeText(UploadPDFactivaty.this, "Please Upload Pdf", Toast.LENGTH_SHORT).show();

                }else {
                    UploadPdf();
                }
            }
        });
    }

    private void UploadPdf() {
        pd.setTitle("Please Wait...");
        pd.setMessage("Uploading pdf");
        pd.show();
        StorageReference reference = storageReference.child("pdf/"+PDFName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri =uriTask.getResult();
                        UploadData(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPDFactivaty.this, "SomeThing Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UploadData(String valueOf) {
        String uniqueKey=databaseReference.child("pdf").push().getKey();
        HashMap data = new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfUrl",valueOf);
        databaseReference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadPDFactivaty.this, "PDF Upload Successfully.", Toast.LENGTH_SHORT).show();
                Pdftitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPDFactivaty.this, "Failed to Upload Pdf.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGalary() {
        Intent intent = new Intent();
        intent.setType("pdf/docs/ppt");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select pdf file"),REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ && resultCode== RESULT_OK){
            pdfData=data.getData();
            //Toast.makeText(this, ""+pdfData, Toast.LENGTH_SHORT).show();

            if (pdfData.toString().startsWith("content://")){
                Cursor cursor =null;
                try {
                    cursor=UploadPDFactivaty.this.getContentResolver().query(pdfData,null,null,null,null);
                    if (cursor !=null && cursor.moveToFirst()){
                        PDFName =cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(pdfData.toString().startsWith("file://")){
               PDFName= new File(pdfData.toString()).getName();
            }
            textView.setText(PDFName);

        }
    }
}