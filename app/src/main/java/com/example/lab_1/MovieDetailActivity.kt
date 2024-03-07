package com.example.lab_1

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.lab_1.databinding.ActivityDescriptionBinding
import com.example.lab_1.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var binding: ActivityDescriptionBinding
    private lateinit var uid: String
    private var isInFavorites = false

    private lateinit var viewPager: ViewPager2
    private lateinit var imageSliderAdapter: ImageSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_description)
        setContentView(binding.root)

        viewPager = findViewById(R.id.viewPager)
        imageSliderAdapter = ImageSliderAdapter()
        viewPager.adapter = imageSliderAdapter


        title = findViewById(R.id.Title)
        description = findViewById(R.id.Description)

        title.text = intent.getStringExtra("movieTitle")
        description.text = intent.getStringExtra("movieDescription")

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().currentUser!!.uid
        }

        val id = intent.getStringExtra("movieId")
        val storageReference = FirebaseStorage.getInstance().reference.child("images/$id")
        val list = storageReference.listAll()
        list.addOnSuccessListener { result ->
                result.items.forEach { imageReference ->
                    imageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                        imageSliderAdapter.addImage(imageUrl.toString())
                    }
                }
            }
            .addOnFailureListener {
                // Обработка ошибки при загрузке изображений
            }

        checkMovieForFavorites(intent.getStringExtra("movieId"))

        /*if(isInFavotires){
            binding.bAddToFavorites.text = "Remove from favorites"
            binding.bAddToFavorites.setOnClickListener {

            }
        } else {
            binding.bAddToFavorites.setOnClickListener {
                setMovieToFavorites(intent.getStringExtra("movieTitle"))
            }
        }*/
    }

    private fun setMovieToFavorites(key: String?){
        val database = FirebaseDatabase.getInstance()
        val k = intent.getStringExtra("movieId")
        val reference = database.getReference("AuthorizedUsers/$uid/favorites/$k")
        reference.setValue(key)
    }

    private fun removeMovieFromFavorites(){
        val database = FirebaseDatabase.getInstance()
        val k = intent.getStringExtra("movieId")
        val reference = database.getReference("AuthorizedUsers/$uid/favorites/$k")
        reference.removeValue()
    }

    private fun checkMovieForFavorites(key: String?){
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("AuthorizedUsers/$uid/favorites")
        reference.get().addOnSuccessListener {
            isInFavorites = false
            for (snap in it.children) {
                if(snap.key == key){
                    isInFavorites = true
                }
            }
            if(isInFavorites) {
                binding.bAddToFavorites.text = "Remove from favorites"
                binding.bAddToFavorites.setOnClickListener {
                    removeMovieFromFavorites()
                    recreate()
                }
            }
            else{
                binding.bAddToFavorites.setOnClickListener {
                    setMovieToFavorites(intent.getStringExtra("movieTitle"))
                    recreate()
                }
            }
        }
    }


}