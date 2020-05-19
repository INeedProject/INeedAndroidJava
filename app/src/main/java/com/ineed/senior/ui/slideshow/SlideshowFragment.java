package com.ineed.senior.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.*;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ineed.senior.ChatActivity;
import com.ineed.senior.R;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    // Create array list for storing chats and the user emails of the other side (dots removed e.g mert@gmailcom)
    ArrayList<String> userEmails;

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // Createe and assign an arraylit object to userIDS
        userEmails = new ArrayList<>();
        // Get the reference to the current user object from firebae
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get the chat history of the current user in the database
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Chats");

        // Get the referance of the listview to show the chats
        ListView chats = (ListView) root.findViewById(R.id.listview_chats);

        // Create and assign an adapter to fill the lisview with
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, this.userEmails);

        // Set the listviews adapter
        chats.setAdapter(adapter);


        // Set an event listener to get the seletted item from the list view
        chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // STart the ChatActivity with the userId of the selected chats other and
                startActivity(new Intent(getContext(), ChatActivity.class).putExtra("receiverEmail", userEmails.get(position)));
            }
        });

        // Set an event listener to get the data from database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userEmails.clear();

                for (DataSnapshot chat : dataSnapshot.getChildren()) {

                    final String chatKey = chat.getKey();
                    // chats containing message objects are stored with keys like "emailIDsender:emailIDreceiver", with dots removed from both emailIDs
                    String userEmail = user.getEmail().replaceAll("[.]","");

                    if (chatKey.contains(userEmail)){

                        if (chatKey.indexOf(userEmail) < chatKey.indexOf(":") && !(userEmails.contains(chatKey.split(":")[1]))) {
                            userEmails.add(chatKey.split(":")[1]);
                        }
                        else if (chatKey.indexOf(userEmail) > chatKey.indexOf(":") && !(userEmails.contains(chatKey.split(":")[0]))){
                            userEmails.add(chatKey.split(":")[0]);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return root;
    }
}