package jp.ac.ecc.sk3a12.ikouka

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

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
    }
}