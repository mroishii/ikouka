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
        Anketo mAnketo = new Anketo();
        return mAnketo;
    }
}
