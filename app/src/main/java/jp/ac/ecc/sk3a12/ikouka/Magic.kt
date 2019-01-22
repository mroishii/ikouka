package jp.ac.ecc.sk3a12.ikouka

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class Magic {
    companion object {
        fun getFileExtension(context: Context, uri: Uri): String {
            lateinit var filename : String
            if (uri.getScheme().equals("content")) {
                var cursor: Cursor = context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor.close()
                }
            }

            if (filename == null) {
                filename = uri.getPath()
                var cut = filename.lastIndexOf('/')
                if (cut != -1) {
                    filename = filename.substring(cut + 1)
                }
            }

            return filename.substring(filename.lastIndexOf('.') + 1)
        }

        fun getDbInstance(): FirebaseFirestore {
            return FirebaseFirestore.getInstance().apply {
                val settings = FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build()
                this.firestoreSettings = settings
            }
        }
    }
}