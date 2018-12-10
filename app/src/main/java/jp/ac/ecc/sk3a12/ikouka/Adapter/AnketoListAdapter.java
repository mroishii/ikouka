package jp.ac.ecc.sk3a12.ikouka.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.ac.ecc.sk3a12.ikouka.Activity.AnketoActivity;
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo;
import jp.ac.ecc.sk3a12.ikouka.Model.MyBuilder;
import jp.ac.ecc.sk3a12.ikouka.R;

public class AnketoListAdapter extends RecyclerView.Adapter<AnketoListAdapter.AnketoViewHolder> {
    private String TAG = "AktLstAdapter";

    private ArrayList<Anketo> anketoList;
    private ArrayList<String> anketosId;
    private HashMap<String, HashMap<String, String>> users;
    private String currentUser;
    Context mContext;

    //Firebase
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    //MyBuilder
    private MyBuilder myBuilder = new MyBuilder("AnketoListAdapter");

//    public AnketoListAdapter(Context context, ArrayList<Anketo> anketoList, HashMap<String, HashMap<String, String>> users, String currentUser) {
//        this.mContext = context;
//        this.anketoList = anketoList;
//        this.users = users;
//        this.currentUser = currentUser;
//    }

    public AnketoListAdapter(Context context, ArrayList<String> anketosId, HashMap<String, HashMap<String, String>> users, String currentUser) {
        this.mContext = context;
        this.anketosId = anketosId;
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
        public TextView anketoDue;
        public TextView anketoDescription;
        public ImageView anketoAnswered;
        public ConstraintLayout parentLayout;

        public AnketoViewHolder(View view, int viewType) {
            super(view);
                anketoImage = (CircleImageView) view.findViewById(R.id.anketo_list_image);
                anketoTitle = (TextView) view.findViewById(R.id.anketo_list_title);
                anketoDue = (TextView) view.findViewById(R.id.anketo_list_due);
                anketoDescription = (TextView) view.findViewById(R.id.anketo_list_description);
                anketoAnswered = (ImageView) view.findViewById(R.id.anketo_list_answered);
                parentLayout = view.findViewById(R.id.anketo_list_item_parent);


        }
    }


//    @Override
//    public void onBindViewHolder(AnketoListAdapter.AnketoViewHolder holder, int position) {
//        final Anketo a = anketoList.get(position);
//
//        holder.anketoTitle.setText(a.getTitle());
//        holder.anketoDescription.setText(a.getDescription());
//        if (!a.isAnswered(currentUser)) {
//            holder.anketoAnswered.setVisibility(ImageView.INVISIBLE);
//        }
//        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, AnketoActivity.class);
//                intent.putExtra("anketo", a);
//                intent.putExtra("users", users);
//                mContext.startActivity(intent);
//            }
//        });
//
//    }

    @Override
    public void onBindViewHolder(final AnketoListAdapter.AnketoViewHolder holder, int position) {
        final String anketoId = anketosId.get(position);

        mDb.collection("Anketo")
                .document(anketoId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot anketo = task.getResult();
                            if (anketo.exists()) {
                                Log.d(TAG, "GOT ANKETO -> " + anketo);

                                final Anketo mAnketo = myBuilder.buildAnketoObject(anketo);

                                holder.anketoTitle.setText(mAnketo.getTitle());
                                holder.anketoDescription.setText(mAnketo.getDescription());

                                Date dueDate = new Date(mAnketo.getDue());
                                holder.anketoDue.setText("締切：" + dueDate.toString());
                                if (!mAnketo.isAnswered(currentUser)) {
                                    holder.anketoAnswered.setVisibility(ImageView.INVISIBLE);
                                }
                                holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, AnketoActivity.class);
                                        intent.putExtra("anketoId", mAnketo.getId());
                                        intent.putExtra("users", users);
                                        mContext.startActivity(intent);
                                    }
                                });

                            } else {
                                Log.d(TAG, "ANKETO NOT EXISTED");
                            }
                        } else {
                            Log.d(TAG, "GET FAILED AT -> " + task.getException());
                        }
                    }
                });
    }




    @Override
    public int getItemCount() {
        return anketosId.size();
    }


}
