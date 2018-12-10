package jp.ac.ecc.sk3a12.ikouka.Model;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

public class MyBuilder {
    private String TAG = "MyBuilder";
    private String LOCATION = "";

    public MyBuilder() {

    }

    public MyBuilder(String LOCATION) {
        this.LOCATION = LOCATION;
    }

    public Anketo buildAnketoObject(DocumentSnapshot anketoDs) {
        Anketo mAnketo = new Anketo(anketoDs.getId(),
                ((Timestamp) anketoDs.get("created")).getSeconds() * 1000,
                anketoDs.getString("type"),
                anketoDs.getString("title"),
                anketoDs.getString("description"),
                anketoDs.getString("owner"),
                ((Timestamp) anketoDs.get("due")).getSeconds() * 1000);
        mAnketo.setAnswers((HashMap<String, Object>)anketoDs.get("answers"));
        Log.d(TAG, "FROM <" + LOCATION + "> ANKETO OBJECT CREATED -> " + mAnketo);

        return mAnketo;
    }
}
