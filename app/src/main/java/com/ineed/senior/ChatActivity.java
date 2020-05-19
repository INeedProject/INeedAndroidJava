package com.ineed.senior;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    ArrayList<Message> chatMessages; // holds retrieved messages
    ListView chatScreen; // Reference to chat screen
    String receiverEmail; // email of receiver

    ListviewMessagingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final TextView writeMessage=(TextView)findViewById(R.id.textview_writeMessage);
        Button send = (Button)findViewById(R.id.button_send);

        // Get the receiver's user id from bundle
        receiverEmail = getIntent().getStringExtra("receiverEmail");
        // Get the reference to the current user object
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get the reference to the users chat database
        final DatabaseReference chatsReference = FirebaseDatabase.getInstance().getReference("Chats");
        // Assing a new arraylist object ot the chatMessages
        chatMessages = new ArrayList<>();
        // Get the referance to our chat screeen
        chatScreen = (ListView)findViewById(R.id.listview_chats);
        // Create an adapter with Message object to fill our chat screen
        adapter = new ListviewMessagingAdapter(chatMessages,this);

        // Set an event listener to get the data
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the messages so that we don't have duplicates
                chatMessages.clear();
                    // Check for possible exceptions
                    try {
                        // adding messages sent from the current user to the other person
                        fillChatMessages(dataSnapshot.child(user.getEmail().replaceAll("[.]","")
                                + ":" + receiverEmail.replaceAll("[.]","")));

                        // adding messages sent from the other person to the current user
                        fillChatMessages(dataSnapshot.child(receiverEmail.replaceAll("[.]","")
                                + ":" + user.getEmail().replaceAll("[.]","")));

                    } catch (Exception e) {
                        Log.e("TAG", e.getMessage() );
                    }
                    finally {
                        Collections.sort(chatMessages); // sorts chronologically using the epoch timestamp in each Message object
                        // Tell the adapter that our messages array has changed so it should update itself
                        adapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Set the listview adapter
        chatScreen.setAdapter(adapter);

        writeMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(ChatActivity.this);
                }
            }
        });

        // Create an event listener to send the message we write
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the message text and store it to Firebase with the current time
                String messageToSend = writeMessage.getText().toString();
                String chatID = user.getEmail().replaceAll("[.]","") +
                        ":" + receiverEmail.replaceAll("[.]","");
                Message newMessage = new Message(user.getEmail(), messageToSend, receiverEmail, new Date().getTime());
                FirebaseDatabase.getInstance().getReference("Chats")
                        .child(chatID)
                        .child(newMessage.getTimestamp()+"")
                        .setValue(newMessage);
                writeMessage.setText("");
                hideKeyboard(ChatActivity.this);
                scrollMyListViewToBottom(); // on sending
            }
        });

        scrollMyListViewToBottom(); // on opening
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void scrollMyListViewToBottom() {
        chatScreen.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatScreen.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private void fillChatMessages(DataSnapshot dataSnapshot){
       for(DataSnapshot message : dataSnapshot.getChildren()) {
            Message retrieved = new Message(message.child("mSender").getValue().toString(),
                    message.child("mMessage").getValue().toString(),
                    message.child("mReceiver").getValue().toString(),
                    (Long)message.child("timestamp").getValue());
            this.chatMessages.add(retrieved);
        }
    }


}
