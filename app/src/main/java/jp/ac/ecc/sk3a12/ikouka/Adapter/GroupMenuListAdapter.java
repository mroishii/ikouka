package jp.ac.ecc.sk3a12.ikouka.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.ac.ecc.sk3a12.ikouka.Activity.AnketoListActivity;
import jp.ac.ecc.sk3a12.ikouka.Activity.GroupChatActivity;
import jp.ac.ecc.sk3a12.ikouka.Model.Group;
import jp.ac.ecc.sk3a12.ikouka.R;

public class GroupMenuListAdapter extends ArrayAdapter<String> {
    private ArrayList<String> text;
    private ArrayList<Integer> icon;
    private Group currentGroup ;
    Context mContext;

    public GroupMenuListAdapter(ArrayList<String> text, ArrayList<Integer> icon, Group currentGroup, Context context) {
        super(context, R.layout.listmenu_item, text);
        this.icon = icon;
        this.text = text;
        this.currentGroup = currentGroup;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.listmenu_item, null);
        }

        final int item_position = position;
        final ImageView item_icon = convertView.findViewById(R.id.listmenu_item_icon);
        final TextView item_text = convertView.findViewById(R.id.listmenu_item_text);

        item_icon.setImageResource(icon.get(position));
        item_text.setText(text.get(position));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (item_position) {
                    case 0: {
                        Toast.makeText(mContext, item_text.getText(), Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case 1: {
                        Toast.makeText(mContext, item_text.getText(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, AnketoListActivity.class);
                        Log.d("GroupMenuAdapter", "currentGroup => " + currentGroup.toString());
                        intent.putExtra("groupId", currentGroup.getGroupId());
                        intent.putExtra("groupTitle", currentGroup.getTitle());
                        mContext.startActivity(intent);
                        break;
                    }

                    case 2: {
                        Toast.makeText(mContext, item_text.getText(), Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case 3: {
                        Intent intent = new Intent(mContext, GroupChatActivity.class);
                        intent.putExtra("groupId", currentGroup.getGroupId());
                        mContext.startActivity(intent);
                        break;
                    }
                }
            }
        });

        return convertView;
    }
}
