package quran.app.admincollageapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import quran.app.admincollageapp.faculty.UpdateFaculty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView UploadNotice,addGalleryImages,addBook,facalty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UploadNotice= (CardView)findViewById(R.id.addNoticeId);
        addGalleryImages= (CardView)findViewById(R.id.addGalleryImagesId);
        addBook= (CardView)findViewById(R.id.addEbookId);
        facalty= (CardView)findViewById(R.id.facultysId);
        UploadNotice.setOnClickListener(this);
        addGalleryImages.setOnClickListener(this);
        addBook.setOnClickListener(this);
        facalty.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.addNoticeId:
                 intent = new Intent(MainActivity.this,UploadNotice.class);
                startActivity(intent);
                break;
            case R.id.addGalleryImagesId:
                 intent = new Intent(MainActivity.this,UploadImage.class);
                startActivity(intent);
                break;
            case R.id.addEbookId:
                intent = new Intent(MainActivity.this,UploadPDFactivaty.class);
                startActivity(intent);
                break;
            case R.id.facultysId:
                intent = new Intent(MainActivity.this,UpdateFaculty.class);
                startActivity(intent);
                break;

        }

    }
}