package jp.ac.ecc.sk3a12.ikouka.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.ac.ecc.sk3a12.ikouka.Model.ChatMessage;
import jp.ac.ecc.sk3a12.ikouka.R;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    private ArrayList<ChatMessage> messageList;
    private String currentUser;
    private HashMap<String, HashMap<String, String>> users;

    public MessageListAdapter(ArrayList<ChatMessage> messageList, String currentUser, HashMap<String, HashMap<String, String>> users) {
        this.messageList = messageList;
        this.currentUser = currentUser;
        this.users = users;
    }

    @NonNull
    @Override
    public MessageListAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_self, parent, false);
        }

        return new MessageViewHolder(v, viewType);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView messageAvatar;
        public TextView messageSender;
        public TextView messageContent;

        public MessageViewHolder(View view, int viewType) {
            super(view);
            if (viewType == 0) {
                messageAvatar = (CircleImageView) view.findViewById(R.id.chatMessageAvatar);
                messageSender = (TextView) view.findViewById(R.id.chatMessageSender);
                messageContent = (TextView) view.findViewById(R.id.chatMessageContent);
            } else {
                messageContent = (TextView) view.findViewById(R.id.chatSelfMessageContent);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        //if current user, use self message layout
        ChatMessage c = messageList.get(position);
        if (c.getSender().equals(currentUser)) {
            return 1;
        }
        //else, use normal message layout
        return 0;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        ChatMessage c = messageList.get(position);
        if (holder.getItemViewType() == 0) {
            String senderId = c.getSender();
            String senderName = users.get(senderId).get("displayName");
            holder.messageSender.setText(senderName);
            holder.messageContent.setText(c.getMessage());
        } else {
            holder.messageContent.setText(c.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


}
