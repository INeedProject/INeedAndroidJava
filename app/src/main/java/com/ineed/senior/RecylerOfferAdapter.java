package com.ineed.senior;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ineed.senior.ui.gallery.GalleryFragment;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class RecylerOfferAdapter extends RecyclerView.Adapter {

    DatabaseReference offersRef = FirebaseDatabase.getInstance().getReference("offers");
    private ArrayList<Offer> mOffersArray;
    private ArrayList<String> offerHashes;

    public RecylerOfferAdapter(ArrayList<Offer> mOffersArray, ArrayList<String> offerHashes){
       this.mOffersArray = mOffersArray;
       this.offerHashes = offerHashes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate and get the view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.recylerview_row, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
      // Get the current offer in the array
        final Offer offer = mOffersArray.get(position);

        // assign its title and user property to the textView of the recyler view
        ((TextView)holder.itemView.findViewById(R.id.textview_name)).setText("Offer for a need you posted");
        ((TextView)holder.itemView.findViewById(R.id.textview_user)).setText("By " + offer.getOfferer());

        // Get the reference for the two buttons
        final Button accept = (Button)holder.itemView.findViewById(R.id.button_accept);
        final Button decline = (Button)holder.itemView.findViewById(R.id.button_decline);

        if (offer.isState()) {
            accept.setText("Accepted");
            decline.setVisibility(View.INVISIBLE);
        }

        // Set an event listener to the accept button
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final Context context = v.getContext();

              if (!offer.isState()){
                  AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                  // description
                  builder.setMessage("You are about to begin a conversation with " + offer.getOfferer());

                  // Add the buttons
                  builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          offersRef.child(offerHashes.get(position)).child("state").setValue(true);
                          Intent chat = new Intent(v.getContext(), ChatActivity.class);
                          chat.putExtra("receiverEmail", offer.getOfferer());
                          context.startActivity(chat);
                      }
                  });
                  builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          // User cancelled the dialog
                      }
                  });

                  // Display the AlertDialog
                  builder.create().show();
              }
                else {
                  Intent chat = new Intent(v.getContext(), ChatActivity.class);
                  chat.putExtra("receiverEmail", offer.getOfferer());
                  context.startActivity(chat);
              }
            }
        });

        // Set an event listener to the reject button
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Context context = v.getContext();

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                // description
                builder.setMessage("You are about to reject the offer by " + offer.getOfferer() +". " +
                        "This action is irreversible");

                // Add the buttons
                builder.setPositiveButton("Reject Offer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        offersRef.child(offerHashes.get(position)).removeValue();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                // Display the AlertDialog
                builder.create().show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mOffersArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

         TextView title;
         TextView user;


        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.textview_name);
            user = view.findViewById(R.id.textview_user);

        }
    }
}
