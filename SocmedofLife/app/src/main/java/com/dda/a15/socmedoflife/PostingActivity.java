package com.dda.a15.socmedoflife;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText txtText;
    private Button btnSubmit;
    private TextView coba;

    //spinner
    private Spinner spPhrile;

    //db
    FirebaseDatabase db;
    DatabaseReference dbUsers;
    DatabaseReference dbPosts;
    private List<Map> listUsers;
    private List<Map> listPosts;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;
    //recyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        txtText = (EditText) findViewById(R.id.txtText);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        spPhrile = (Spinner) findViewById(R.id.spPhrile);

        // Buat adapter ke spinner
        ArrayAdapter<String> phriles = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        phriles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //tambah items ke spinner
        phriles.add("Happy");
        phriles.add("Sad");
        phriles.add("Confused");
        phriles.add("Shocked");
        phriles.add("Sick");
        phriles.add("Angry");
        phriles.add("Set your phrile");

        spPhrile.setAdapter(phriles);
        spPhrile.setSelection(phriles.getCount()); //display hint

        //database
        db = FirebaseDatabase.getInstance();
        dbUsers = db.getReference("users");
        dbPosts = db.getReference("posts");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        //update database
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(listUsers!=null && dataSnapshot.getValue()!=null){
                    listUsers = (List<Map>) dataSnapshot.getValue();
                }
                else{
                    //jika null instansiasi
                    listUsers = new ArrayList<Map>();//list biasa
                    if(dataSnapshot.getValue()!= null){
                        listUsers = (List<Map>) dataSnapshot.getValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        dbPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(listPosts!=null && dataSnapshot.getValue()!=null){
                    listPosts = (List<Map>) dataSnapshot.getValue();

                }
                else{
                    //jika null instansiasi
                    listPosts = new ArrayList<Map>();//list biasa
                    if(dataSnapshot.getValue()!= null){
                        listPosts = (List<Map>) dataSnapshot.getValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phrile = "";
                phrile = spPhrile.getSelectedItem().toString();
                if(!phrile.equals("Set your phrile") && !txtText.getText().toString().equals("")){
                    String text = txtText.getText().toString();

                    int idxUser = -1;
                    for(int i = 0; i<listUsers.size();i++){
                        if(listUsers.get(i).get("email").toString().equals(user.getEmail())){
                            idxUser = i;
                        }
                    }
                    String email = user.getEmail();
                    String name = listUsers.get(idxUser).get("name").toString();

                    Map map = new HashMap();
                    map.put("email",email);
                    map.put("name",name);
                    map.put("phrile",phrile);
                    map.put("text",text);
                    listPosts.add(map);
                    dbPosts.setValue(listPosts);
                    Intent intent = new Intent(PostingActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else{
                    if(txtText.getText().toString().equals("")){
                        txtText.setError("Fill how you explain your phrile");
                    }
                    if(phrile.equals("Set your phrile")){
                        TextView errorText = (TextView)spPhrile.getSelectedView();
                        errorText.setError("yo");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Fill your phrile");//changes the selected item text to this
                    }
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //buat apa?
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), "Choose your phrile", Toast.LENGTH_SHORT).show();
    }
}
