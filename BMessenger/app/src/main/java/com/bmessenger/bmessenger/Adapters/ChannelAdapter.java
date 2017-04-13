package com.bmessenger.bmessenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmessenger.bmessenger.Activities.MessagingActivity;
import com.bmessenger.bmessenger.Manager.UserControl;
import com.bmessenger.bmessenger.Models.Channel;
import com.bmessenger.bmessenger.Models.ChannelItem;
import com.bmessenger.bmessenger.R;

import java.util.List;

/**
 * Created by uli on 3/29/2017.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public TextView nameTextView;
        public TextView summaryTextView;
        private Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.channel_name);
            summaryTextView = (TextView) itemView.findViewById(R.id.channel_summary);

            this.context = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, MessagingActivity.class);
                //based on item add info to intent
                UserControl.get(context).setmChannelName(nameTextView.getText().toString());
                context.startActivity(intent);
            }

        }
    }

    private List<ChannelItem> mChannels;

    private Context mContext;

    public ChannelAdapter(Context context, List<ChannelItem> channels) {
        mChannels = channels;
        mContext = context;
    }

    private Context getContext() {
        return  mContext;
    }

    @Override
    public ChannelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View channelView = inflater.inflate(R.layout.item_channel, parent, false);

        ViewHolder viewHolder = new ViewHolder(context, channelView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChannelAdapter.ViewHolder viewHolder, int position) {
        ChannelItem channel = mChannels.get(position);

        TextView textView = viewHolder.nameTextView;
        TextView summaryView = viewHolder.summaryTextView;

        textView.setText(channel.getName());
        summaryView.setText(channel.getSummary());
    }

    @Override
    public int getItemCount() {
        return mChannels.size();
    }
}