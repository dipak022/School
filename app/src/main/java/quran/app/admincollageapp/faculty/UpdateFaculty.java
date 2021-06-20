package quran.app.admincollageapp.faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import quran.app.admincollageapp.R;

public class UpdateFaculty extends AppCompatActivity {
    FloatingActionButton fab;
    private RecyclerView computer;
    private LinearLayout csNoData;
    private List<TeacherData> list1;
    private TeacherAdapter adapter;


    private DatabaseReference reference,dbRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);

        csNoData=findViewById(R.id.csNoData);

        computer=findViewById(R.id.computer);

        reference= FirebaseDatabase.getInstance().getReference().child("teacher");

        csDepartment();

        fab= findViewById(R.id.fabId);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateFaculty.this,AddTeacher.class));
            }
        });
    }

    private void csDepartment() {
        dbRef=reference.child("one");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1 = new ArrayList<>();
                if (!snapshot.exists()){
                    csNoData.setVisibility(View.VISIBLE);
                    computer.setVisibility(View.GONE);

                }else {
                    csNoData.setVisibility(View.GONE);
                    computer.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        TeacherData data = snapshot1.getValue(TeacherData.class);
                        list1.add(data);
                    }
                    computer.setHasFixedSize(true);
                    computer.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapter = new TeacherAdapter(list1,UpdateFaculty.this);
                    computer.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}