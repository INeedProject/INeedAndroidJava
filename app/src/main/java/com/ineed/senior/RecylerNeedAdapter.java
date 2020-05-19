package com.ineed.senior;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecylerNeedAdapter extends RecyclerView.Adapter {

    private ArrayList<Need> mNeedsArray;

    public  RecylerNeedAdapter(ArrayList<Need> mNeedsArray){
       this.mNeedsArray=mNeedsArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate and get the view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.recylerview_row, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      // Get the curren need in the array
        final Need need=mNeedsArray.get(position);

        // assign its title and user property to the textView of the recyler view
        ((TextView)holder.itemView.findViewById(R.id.textview_name)).setText("Help Title: "+need.getmTitle().replace("_"," "));
        ((TextView)holder.itemView.findViewById(R.id.textview_user)).setText("User: "+need.getmUser().replace("_"," "));

        // GEt the two buttons referance
        final Button favorite=(Button)holder.itemView.findViewById(R.id.button_accept);
        final Button offer=(Button)holder.itemView.findViewById(R.id.button_decline);



        // Set an event listener to the favorite button
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GEt the database and the current user refenaces
                DatabaseReference mDatabase=  FirebaseDatabase.getInstance().getReference();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Set the current need under the curtent users Favs database
                mDatabase.child("Favs").child(user.getUid()).child(need.getmCitie()).child(need.getmDistrict()).child(String.valueOf(need.getmIndex())).setValue(need);
                favorite.setText("Favorited");
                favorite.setActivated(false);


            }
        });

        // Set an event listner to the offer button
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the ChatActivity and put the user id of the need into th bundle
                v.getContext().startActivity(new Intent(v.getContext(), ChatActivity.class).putExtra("userId",need.getmUserId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNeedsArray.size();
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
