package com.ineed.senior.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ineed.senior.Offer;
import com.ineed.senior.R;
import com.ineed.senior.RecylerOfferAdapter;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private GalleryViewModel mGalleryViewModel;
    private ArrayList<Offer> offerList;
    private ArrayList<String> offerHashes;
    private RecylerOfferAdapter offerAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mGalleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        final RecyclerView favorites=(RecyclerView)root.findViewById(R.id.recylerview_favorite);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("offers");
        offerList = new ArrayList<>();
        offerHashes = new ArrayList<>();
        offerAdapter = new RecylerOfferAdapter(offerList, offerHashes);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offerList.clear();
                offerHashes.clear();
                for (DataSnapshot offerRetrieved : dataSnapshot.getChildren()){
                    Offer offer = offerRetrieved.getValue(Offer.class);
                    if (offer.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        offerList.add(offer);
                        offerHashes.add(offerRetrieved.getKey());
                    }
                }

                offerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        favorites.setLayoutManager(new LinearLayoutManager(getContext()));
        favorites.setAdapter(offerAdapter);

        return root;
    }
}