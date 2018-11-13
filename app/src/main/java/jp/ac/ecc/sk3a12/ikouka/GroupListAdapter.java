package jp.ac.ecc.sk3a12.ikouka;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get item at position
        Group group = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.grouplist_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.grouplist_item_title);
            viewHolder.description = (TextView) convertView.findViewById(R.id.grouplist_item_description);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.title.setText(group.getTitle());
        viewHolder.description.setText(group.getDescription());
        viewHolder.image.setOnClickListener(this);

        // Return the completed view to render on screen
        return convertView;
    }
}

