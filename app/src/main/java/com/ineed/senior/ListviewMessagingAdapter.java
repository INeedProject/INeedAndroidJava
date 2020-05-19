package com.ineed.senior;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ListviewMessagingAdapter extends BaseAdapter {

    private List<Message> messagesItems;
    private Context context;

    public ListviewMessagingAdapter(List<Message> messagesItems, Context context) {
        this.messagesItems = messagesItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message m = messagesItems.get(position);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // if the messages sender is us then inflate the text view with a different layout so that it looks different
        if (messagesItems.get(position).getmSender().equals(user.getEmail())) {
            convertView = mInflater.inflate(R.layout.list_row_layout_odd,
                    null);
        } else {
            convertView = mInflater.inflate(R.layout.list_row_layout_even,
                    null);
        }

        final TextView lblFrom = (TextView)    convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        txtMsg.setText(m.getmMessage());
        if(m.getmSender().equals(user.getEmail()))
            lblFrom.setText("Me");
        else{
            lblFrom.setText(m.getmSender());
        }



        return convertView;

    }
}
