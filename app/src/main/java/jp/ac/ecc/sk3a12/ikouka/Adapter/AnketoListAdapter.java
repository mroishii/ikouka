package jp.ac.ecc.sk3a12.ikouka.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.ac.ecc.sk3a12.ikouka.Activity.AnketoActivity;
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo;
import jp.ac.ecc.sk3a12.ikouka.Model.AnketoAnswer;
import jp.ac.ecc.sk3a12.ikouka.R;

public class AnketoListAdapter extends RecyclerView.Adapter<AnketoListAdapter.AnketoViewHolder> {
    private ArrayList<Anketo> anketoList;
    private HashMap<String, HashMap<String, String>> users;
    private String currentUser;
    Context mContext;

    public AnketoListAdapter(Context context, ArrayList<Anketo> anketoList, HashMap<String, HashMap<String, String>> users, String currentUser) {
        this.mContext = context;
        this.anketoList = anketoList;
        this.users = users;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public AnketoListAdapter.AnketoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.anketo_list_item, parent, false);

        return new AnketoListAdapter.AnketoViewHolder(v, viewType);
    }


    public class AnketoViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView anketoImage;
        public TextView anketoTitle;
        public TextView anketoDescription;
        public ImageView anketoAnswered;
        public ConstraintLayout parentLayout;

        public AnketoViewHolder(View view, int viewType) {
            super(view);
                anketoImage = (CircleImageView) view.findViewById(R.id.anketo_list_image);
                anketoTitle = (TextView) view.findViewById(R.id.anketo_list_title);
                anketoDescription = (TextView) view.findViewById(R.id.anketo_list_description);
                anketoAnswered = (ImageView) view.findViewById(R.id.anketo_list_answered);
                parentLayout = view.findViewById(R.id.anketo_list_item_parent);


        }
    }


    @Override
    public void onBindViewHolder(AnketoListAdapter.AnketoViewHolder holder, int position) {
        final Anketo a = anketoList.get(position);

        holder.anketoTitle.setText(a.getTitle());
        holder.anketoDescription.setText(a.getDescription());
        if (!a.isAnswered(currentUser)) {
            holder.anketoAnswered.setVisibility(ImageView.INVISIBLE);
        }
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AnketoActivity.class);
                intent.putExtra("anketo", a);
                intent.putExtra("users", users);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return anketoList.size();
    }


}
