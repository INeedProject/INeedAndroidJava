package com.ineed.senior;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyChatsActivity extends Activity {
    final String TAG="TAG";
    ArrayList<String> messages;
    ArrayList<String> userIDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);
        userIDS=new ArrayList<>();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference mDatabase=  FirebaseDatabase.getInstance().getReference("Chats").child(user.getUid());

        messages=new ArrayList<>();

        ListView chats=(ListView)findViewById(R.id.listview_chats);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, messages);

        chats.setAdapter(adapter);

        chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t=(TextView)view;
                startActivity(new Intent(getApplicationContext(),ChatActivity.class).putExtra("userId",userIDS.get(position)));
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                userIDS.clear();
                for (DataSnapshot d:dataSnapshot.getChildren()){
                    // TODO get the onclick on a chat than get its id and send it to the chat screen
                    final String uId=d.getKey();
                    userIDS.add(uId);
                    DatabaseReference  mUsers=   FirebaseDatabase.getInstance().getReference("Users");

                    mUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ss) {
                            for(DataSnapshot dd:ss.getChildren()){
                                if(uId.equals(dd.getKey())){
                                    messages.add("Your Chat Wtih "+dd.getValue().toString()+" ->");
                                }
                            }
                            adapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}