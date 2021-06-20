package quran.app.admincollageapp.faculty;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

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
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipdate_teacher);

        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        post=getIntent().getStringExtra("post");
        image=getIntent().getStringExtra("image");


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