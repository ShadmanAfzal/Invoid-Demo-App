package com.example.mylibrary


import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Inform{
    fun getFileExtension(uri: Uri, context: Context): String? = when (uri.scheme) {
        ContentResolver.SCHEME_FILE -> File(uri.path!!).extension
        ContentResolver.SCHEME_CONTENT -> getCursorContent(uri,context);
        else -> null
    }

    private fun getCursorContent(uri: Uri, context: Context): String? = try {
        context.contentResolver.query(uri, null, null, null, null)?.let {cursor ->
            cursor.run {
                val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
                if (moveToFirst()) mimeTypeMap.getExtensionFromMimeType(context.contentResolver.getType(uri))
                else null
            }.also { cursor.close() }
        }
    } catch (e: Exception) {
        null
    }

    fun getName(uri: Uri): String
    {
        return File(uri?.path!!).name;
    }

    fun onUpload(fileName:String, imageUri:Uri, context: Context, progressbar:ProgressDialog ) {
        var imageRef = FirebaseStorage.getInstance().reference.child("images/${fileName}");
        imageRef.putFile(imageUri!!).addOnSuccessListener {
            progressbar.dismiss();
            Toast.makeText(context, "Document has been uploaded Successfully", Toast.LENGTH_SHORT).show();

        }.addOnFailureListener{
            progressbar.dismiss();
            Toast.makeText(context,it.message.toString(), Toast.LENGTH_SHORT);
        }.addOnProgressListener {
            var progress = (100 * it.bytesTransferred/it.totalByteCount);
            progressbar.setMessage("Uploaded ${progress.toString()}%");
        };
    }

    fun isFile(type: String) : Boolean
    {
        return ( type == "pdf" );
    }
}