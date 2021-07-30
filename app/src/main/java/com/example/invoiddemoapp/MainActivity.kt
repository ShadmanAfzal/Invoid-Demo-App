package com.example.invoiddemoapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.Inform
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    lateinit var image : ImageView
    lateinit var selectBtn : Button
    lateinit var uploadBtn : Button
    lateinit var lottieView : View

    private val imagePick  = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Invoid"
        lottieView = findViewById(R.id.animationView);
        image = findViewById(R.id.imageView);
        selectBtn = findViewById(R.id.SelectImage)
        uploadBtn = findViewById(R.id.SubmitButton)

        selectBtn.setOnClickListener{

            val mimetypes = arrayOf("application/pdf")
            val fileDrawer = Intent(Intent.ACTION_GET_CONTENT,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            fileDrawer.putExtra(Intent.EXTRA_MIME_TYPES,mimetypes);
            startActivityForResult(fileDrawer,imagePick);
        }

        uploadBtn.setOnClickListener{
            if(imageUri!=null){
                val progressbar = ProgressDialog(this);
                progressbar.setMessage("Uploading");
                progressbar.setTitle("Document is uploading");
                progressbar.setCancelable(false);
                progressbar.show();
                var fileName : String = Inform().getName(imageUri!!)!!;
                Inform().onUpload(fileName,imageUri!!,this,progressbar);
            }
            else {
                Toast.makeText(this, "No Document has been selected", Toast.LENGTH_SHORT).show();
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == imagePick)
        {
            imageUri = data?.data;

            var type : String = Inform().getFileExtension(imageUri!!,this)!!;

            if(type == "pdf")
            {
                val assetManager = assets;
                val istr: InputStream = assetManager.open("file.png");
                val bitmap = BitmapFactory.decodeStream(istr);
                image.visibility = View.VISIBLE;
                image.setImageBitmap(bitmap);
                lottieView.visibility = View.GONE;
            }
            else
            {
                Toast.makeText(this, "Only PDF files are allowed", Toast.LENGTH_SHORT).show();
                imageUri = null;
                image.visibility = View.GONE;
                lottieView.visibility = View.VISIBLE;

            }
        }
    }
}

