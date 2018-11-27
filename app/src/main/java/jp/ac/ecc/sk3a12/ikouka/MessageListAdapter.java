package jp.ac.ecc.sk3a12.ikouka;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    private ArrayList<ChatMessage> messageList;

    public MessageListAdapter(ArrayList<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageListAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single, parent, false);
        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView messageAvatar;
        public TextView messageSender;
        public TextView messageContent;

        public MessageViewHolder(View view) {
            super(view);
            messageAvatar = (CircleImageView) view.findViewById(R.id.chatMessageAvatar);
            messageSender = (TextView) view.findViewById(R.id.chatMessageSender);
            messageContent = (TextView) view.findViewById(R.id.chatMessageContent);

        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        ChatMessage c = messageList.get(position);
        holder.messageSender.setText(c.getSender());
        holder.messageContent.setText(c.getMessage());
        //TODO get real username and set userimage
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
