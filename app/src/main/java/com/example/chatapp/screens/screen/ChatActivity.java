package com.example.chatapp.screens.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText message_text;
    private RecyclerView rv;
    private RecyclerViewAdapter adapter;
    private ArrayList<String> chatMessages = new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Database
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        message_text = findViewById(R.id.message_text);
        rv = findViewById(R.id.recyclerview_chat);
        adapter = new RecyclerViewAdapter(chatMessages);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        getData();
    }

    public void sendMessage(View view)
    {
        String massageToSend = message_text.getText().toString();
        UUID uuid = UUID.randomUUID();
        String uuidString =uuid.toString();

        String userEmail = user.getEmail().toString();

        databaseReference.child("Chats").child(uuidString).child("usermessage").setValue(massageToSend);
        databaseReference.child("Chats").child(uuidString).child("useremail").setValue(userEmail);
        databaseReference.child("Chats").child(uuidString).child("usermassegetime").setValue(ServerValue.TIMESTAMP);
        message_text.setText("");



    }


    public void getData()
    {
        DatabaseReference newReferance = database.getReference("Chats");

        Query query = newReferance.orderByChild("usermassegetime");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatMessages.clear();

                for(DataSnapshot ds : snapshot.getChildren()){
                    HashMap<String,String> hashMap = (HashMap<String, String>) ds.getValue();
                    String useremail = hashMap.get("useremail");
                    String usersmassege = hashMap.get("usermessage");

                    chatMessages.add(useremail + ":" +usersmassege);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.profile:
                Intent gotoProfile = new Intent(ChatActivity.this,ProfileActivity.class);
                startActivity(gotoProfile);
                break;
            case R.id.sign_out:
                mAuth.signOut();
                Intent goToSignUp = new Intent(ChatActivity.this,SignUpActivity.class);
                startActivity(goToSignUp);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}