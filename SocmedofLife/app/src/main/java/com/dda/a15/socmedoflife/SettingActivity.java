package com.dda.a15.socmedoflife;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class SettingActivity extends AppCompatActivity {

    private Button btnChangeName, btnChangePassword, btnSendResetEmail,
            changeName, changePassword, sendEmail, signOut;

    private EditText oldEmail, name, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    //db
    FirebaseDatabase db;
    DatabaseReference dbUsers;
    DatabaseReference dbPosts;
    private List<Map> listUsers;
    private List<Map> listPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(getString(R.string.app_name));
        //setSupportActionBar(toolbar);

        //database
        db = FirebaseDatabase.getInstance();
        dbUsers = db.getReference("users");
        dbPosts = db.getReference("posts");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

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

        btnChangeName = (Button) findViewById(R.id.change_name_button);
        btnChangePassword = (Button) findViewById(R.id.change_password_button);
        btnSendResetEmail = (Button) findViewById(R.id.sending_pass_reset_button);
        changeName = (Button) findViewById(R.id.changeName);
        changePassword = (Button) findViewById(R.id.changePass);
        sendEmail = (Button) findViewById(R.id.send);
        signOut = (Button) findViewById(R.id.sign_out);

        name = (EditText) findViewById(R.id.name);
        oldEmail = (EditText) findViewById(R.id.old_email);
        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.newPassword);

        name.setVisibility(View.GONE);
        oldEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeName.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setVisibility(View.VISIBLE);
                oldEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeName.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                int idxUser = -1;
                for(int i = 0; i<listUsers.size();i++){
                    if(listUsers.get(i).get("email").toString().equals(user.getEmail())){
                        idxUser = i;
                    }
                }
                name.setText(listUsers.get(idxUser).get("name").toString());
            }
        });

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!name.getText().toString().trim().equals("")) {

                    int idxUser = -1;
                    for(int i = 0; i<listUsers.size();i++){
                        if(listUsers.get(i).get("email").toString().equals(user.getEmail())){
                            idxUser = i;
                        }
                    }

                    ArrayList<Integer> idxPosts = new ArrayList<>();
                    for(int i = 0; i<listPosts.size();i++){
                        if(listPosts.get(i).get("email").toString().equals(user.getEmail())){
                            idxPosts.add(i);
                        }
                    }
                    Map map = new HashMap();
                    map.put("email",user.getEmail());
                    map.put("name",name.getText().toString());
                    listUsers.set(idxUser,map);
                    dbUsers.setValue(listUsers);

                    for (int idxPost:
                         idxPosts) {
                        Map mapPost = new HashMap();
                        mapPost.put("email",user.getEmail());
                        mapPost.put("name",name.getText().toString());
                        mapPost.put("phrile",listPosts.get(idxPost).get("phrile").toString());
                        mapPost.put("text",listPosts.get(idxPost).get("text").toString());
                        listPosts.set(idxPost,mapPost);
                    }
                    dbPosts.setValue(listPosts);

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SettingActivity.this, "Name has changed.", Toast.LENGTH_LONG).show();
                    name.setVisibility(View.GONE);
                    oldEmail.setVisibility(View.GONE);
                    password.setVisibility(View.GONE);
                    newPassword.setVisibility(View.GONE);
                    changeName.setVisibility(View.GONE);
                    changePassword.setVisibility(View.GONE);
                    sendEmail.setVisibility(View.GONE);
                } else {
                    name.setError("Enter name");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setVisibility(View.GONE);
                oldEmail.setVisibility(View.GONE);
                password.setVisibility(View.VISIBLE);
                newPassword.setVisibility(View.VISIBLE);
                changeName.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("") && !password.getText().toString().trim().equals("")) {
                    //auth input pass and current user email.
                    auth.signInWithEmailAndPassword(user.getEmail(), password.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //if the task is successfull
                                    if (task.isSuccessful()) {
                                        //start the profile activity
                                        if (newPassword.getText().toString().trim().length() < 6) {
                                            newPassword.setError("Password too short, enter minimum 6 characters");
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            user.updatePassword(newPassword.getText().toString().trim())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SettingActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                                                signOut();
                                                                progressBar.setVisibility(View.GONE);
                                                            } else {
                                                                Toast.makeText(SettingActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });
                                        }
                                    } else if (newPassword.getText().toString().trim().equals("")) {
                                        newPassword.setError("Enter password");
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(SettingActivity.this, "Login Fail", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setVisibility(View.GONE);
                oldEmail.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeName.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(SettingActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}