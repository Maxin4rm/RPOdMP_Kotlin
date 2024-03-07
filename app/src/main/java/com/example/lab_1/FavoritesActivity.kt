package com.example.lab_1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_1.databinding.ActivityFavoritesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var moviesList: ArrayList<Movie>
    private lateinit var keysList: ArrayList<String>
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        //setContentView(R.layout.activity_favorites)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().currentUser!!.uid
        }

        recyclerView = findViewById(R.id.recyclerViewFav)
        recyclerView.layoutManager = LinearLayoutManager(this)

        keysList = arrayListOf()
        moviesList = arrayListOf()
        getData()

    }

    private fun getData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("AuthorizedUsers/$uid/favorites")
        //dbRef = FirebaseDatabase.getInstance().getReference("\"Movies")

        databaseReference.get().addOnSuccessListener {
            moviesList.clear()
            keysList.clear()
            if (it.exists()) {
                for (snap in it.children) {
                    moviesList.add(Movie(snap.getValue().toString(), null))
                    keysList.add(snap.key!!)
                }

                val movieAdapter = MovieAdapter(moviesList)
                recyclerView.adapter = movieAdapter

                movieAdapter.setOnMovieClickListener(object : MovieAdapter.OnMovieClickListener {
                    override fun onItemClick(position: Int) {
                        val intent = Intent(this@FavoritesActivity, MovieDetailActivity::class.java)

                        intent.putExtra("movieTitle", moviesList[position].title)
                        intent.putExtra("movieDescription", "")
                        intent.putExtra("movieId", keysList[position])

                        startActivity(intent)

                    }

                })
            }
        }
    }
}