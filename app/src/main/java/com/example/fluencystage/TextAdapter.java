package com.example.fluencystage;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class TextAdapter extends ArrayAdapter<ChatMessage> {

    private Activity activity;

    public TextAdapter(Activity context, int resource, List<ChatMessage> objects) {
        super(context, resource, objects);
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource ; // determined by view type
        ChatMessage chatMessage = getItem(position);



        layoutResource = R.layout.rightcloud;

        convertView = inflater.inflate(layoutResource, parent, false);
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);


        //set message content
        holder.msg.setText(chatMessage.getContent());

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }

    private class ViewHolder {
        private TextView msg;

        public ViewHolder(View v) {
            msg =  v.findViewById(R.id.txt_msg);
        }
    }
}