package com.dda.a15.socmedoflife;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    private ImageView mImageView;
    private FloatingActionButton fab;

    //db
    FirebaseDatabase db;
    DatabaseReference dbUsers;
    DatabaseReference dbPosts;
    DatabaseReference dbNotification;
    private List<Map> listUsers;
    private List<Map> listPosts;
    private List<Post> posts;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;

    //page
    private int page;

    //loading
    private ProgressBar progressBar;


    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.

    //handle event saat bottomnavigation di klik
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //inisialisasi
            fab=(FloatingActionButton)findViewById(R.id.fab);

            RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
            LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
            rv.setLayoutManager(llm);

            RVAdapter adapter = new RVAdapter(posts);
            //mImageView.bringToFront();
            switch (item.getItemId()) {
                //home dipilih
                case R.id.navigation_home:
                    if(listPosts!=null && listUsers!=null) {
                        fab.setVisibility(View.INVISIBLE);
                        page = 0;
                        initializeData();

                        rv.setAdapter(adapter);
                        //mTextMessage.setText(R.string.title_home);
                        return true;
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Wait a sec. Application is still loading",Toast.LENGTH_SHORT).show();
                    }
                //me dipilih
                case R.id.navigation_me:
                    if(listPosts!=null && listUsers!=null) {
                        fab.setVisibility(View.VISIBLE);
                        page = 1;
                        initializeData();

                        rv.setAdapter(adapter);
                        //mTextMessage.setText(R.string.title_me);
                        return true;
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Wait a sec. Application is still loading", Toast.LENGTH_SHORT).show();
                    }
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        page=0;
        //beri nilai ke variabel fab, untuk di set invisible pada awalnya
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        //database
        db = FirebaseDatabase.getInstance();
        dbUsers = db.getReference("users");
        dbPosts = db.getReference("posts");
        dbNotification = db.getReference("notification");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        //inisialisasi
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        progressBar.setVisibility(View.VISIBLE);

        //recycler view
        posts = new ArrayList<>();


        //event handling click fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PostingActivity.class);
                startActivity(intent);
            }
        });

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
                    initializeData();
                    RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
                    LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                    rv.setLayoutManager(llm);

                    RVAdapter adapter = new RVAdapter(posts);
                    rv.setAdapter(adapter);
                }
                else{
                    //jika null instansiasi
                    listPosts = new ArrayList<Map>();//list biasa
                    if(dataSnapshot.getValue()!= null){
                        listPosts = (List<Map>) dataSnapshot.getValue();
                        initializeData();
                        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
                        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                        rv.setLayoutManager(llm);

                        RVAdapter adapter = new RVAdapter(posts);
                        rv.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeData(){
        if(page==0){
            posts.clear();
            if(listPosts.size()>=1){
                for(int i=listPosts.size()-1;i>=0;i--) {
                    Post p = new Post(listPosts.get(i).get("name").toString(),
                            listPosts.get(i).get("phrile").toString(),
                            listPosts.get(i).get("text").toString());
                    posts.add(p);

                }
                progressBar.setVisibility(View.GONE);
            }
        }
        else if(page==1){
            posts.clear();
            if(listPosts.size()>=1){
                for(int i=listPosts.size()-1;i>=0;i--) {
                    if(listPosts.get(i).get("email").toString().equals(user.getEmail())){
                        Post p = new Post(listPosts.get(i).get("name").toString(),
                                listPosts.get(i).get("phrile").toString(),
                                listPosts.get(i).get("text").toString());
                        posts.add(p);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }

    //membuat options menu(tombol kanan atas)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //memberikan pemrosesan saat menu di klik
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);

            return true;
        }
        else if(id == R.id.action_logout){
            signOut();
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void signOut() {
        auth.signOut();
    }

}

class Post {
    String name;
    String phrile;
    String text;

    Post(String name, String phrile, String text) {
        this.name = name;
        this.phrile = phrile;
        this.text = text;
    }
}