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

import java.util.ArrayList;

import jp.ac.ecc.sk3a12.ikouka.Activity.GroupActivity;
import jp.ac.ecc.sk3a12.ikouka.Model.Group;
import jp.ac.ecc.sk3a12.ikouka.R;

public class GroupListAdapter extends ArrayAdapter<Group> implements View.OnClickListener {
    private ArrayList<Group> dataset;
    Context mContext;

    //View look-up cache
    private static class ViewHolder {
        TextView title;
        TextView description;
        ImageView image;
    }

    //Constructor
    public GroupListAdapter(ArrayList<Group> data, Context context) {
        super(context, R.layout.grouplist_item, data);
        this.dataset = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Group group = (Group)object;

        Log.d("item", group.getGroupId());

    }

    //private int lastPosition = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get item at position
        final Group group = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.grouplist_item, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, GroupActivity.class);
                    intent.putExtra("groupId", group.getGroupId());
                    intent.putExtra("groupTitle", group.getTitle());
                    mContext.startActivity(intent);
                }
            });
            viewHolder.title = (TextView) convertView.findViewById(R.id.grouplist_item_title);
            viewHolder.description = (TextView) convertView.findViewById(R.id.grouplist_item_description);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.grouplist_item_image);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;

        viewHolder.title.setText(group.getTitle());
        if (group.getDescription().length() > 30) {
            viewHolder.description.setText(group.getDescription().substring(0, 27) + "...");
        } else {
            viewHolder.description.setText(group.getDescription());
        }


        // Return the completed view to render on screen
        return convertView;
    }

}

