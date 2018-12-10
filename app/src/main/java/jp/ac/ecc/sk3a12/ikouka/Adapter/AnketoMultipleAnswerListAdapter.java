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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.ac.ecc.sk3a12.ikouka.Activity.AnketoActivity;
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo;
import jp.ac.ecc.sk3a12.ikouka.Model.AnketoAnswer;
import jp.ac.ecc.sk3a12.ikouka.R;

public class AnketoMultipleAnswerListAdapter extends RecyclerView.Adapter<AnketoMultipleAnswerListAdapter.AnketoAnswerViewHolder> {
    private String TAG = "AktMulAnsLst";

    //Firestore
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    private ArrayList<AnketoAnswer> anketoAnswerList;
    private HashMap<String, HashMap<String, String>> users;
    private String currentUser;
    private String anketoId;
    Context mContext;

    public AnketoMultipleAnswerListAdapter(Context context, String anketoId, ArrayList<AnketoAnswer> anketoList, HashMap<String, HashMap<String, String>> users, String currentUser) {
        this.mContext = context;
        this.anketoId = anketoId;
        this.anketoAnswerList = anketoList;
        this.users = users;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public AnketoMultipleAnswerListAdapter.AnketoAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anketo_multiple_answer_item, parent, false);

        return new AnketoMultipleAnswerListAdapter.AnketoAnswerViewHolder(v, viewType);
    }


    public class AnketoAnswerViewHolder extends RecyclerView.ViewHolder {
        public CheckBox answerCheckbox;
        public LinearLayout answered;

        public AnketoAnswerViewHolder(View view, int viewType) {
            super(view);
            answerCheckbox = view.findViewById(R.id.anketo_answer_checkbox);
            answered = view.findViewById(R.id.anketo_answered);


        }
    }

    @Override
    public void onBindViewHolder(final AnketoMultipleAnswerListAdapter.AnketoAnswerViewHolder holder, final int position) {
        final AnketoAnswer a = anketoAnswerList.get(position);

        holder.answerCheckbox.setText(a.getDescription());
        if (a.isAnswered(currentUser)) {
            holder.answerCheckbox.setChecked(true);
        } else {
            holder.answerCheckbox.setChecked(false);
        }

        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        inputParams.weight = 1;
        for (String uid : users.keySet()) {
            if (a.isAnswered(uid)) {
                CircleImageView img = new CircleImageView(mContext);
                img.setImageResource(R.mipmap.ic_launcher);
                holder.answered.addView(img, inputParams);
            }
        }

        holder.answerCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                final String path = "answers." + a.getId() + ".answered." + currentUser;
                mDb.collection("Anketo")
                        .document(anketoId)
                        .update(
                                path, isChecked
                        )
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "UPDATED " + path + " TO " + isChecked);
                                }
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return anketoAnswerList.size();
    }


}
