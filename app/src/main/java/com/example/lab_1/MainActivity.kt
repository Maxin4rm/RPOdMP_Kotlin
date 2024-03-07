package com.example.lab_1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.ArrayList
import com.example.lab_1.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var moviesList: ArrayList<Movie>
    private lateinit var keysList: ArrayList<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewFav)
        recyclerView.layoutManager = LinearLayoutManager(this)

        keysList = arrayListOf()
        moviesList = arrayListOf()
        getData()
        //addMovieToFirebase(movie)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.sign_out){
            auth.signOut()
            finish()
        } else if(item.itemId == R.id.profile){
            val i = Intent(this, ProfileActivity::class.java)
            startActivity(i)
        } else if(item.itemId == R.id.favorites){
            val i = Intent(this, FavoritesActivity::class.java)
            startActivity(i)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addMovieToFirebase(movie: Movie) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Movies")
        reference.push().setValue(movie)
    }

    private fun getData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Movies")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                moviesList.clear()
                keysList.clear()
                if (dataSnapshot.exists()) {
                    for (snap in dataSnapshot.children) {
                        moviesList.add(snap.getValue(Movie::class.java)!!)
                        keysList.add(snap.key!!)
                    }

                    val movieAdapter = MovieAdapter(moviesList)
                    recyclerView.adapter = movieAdapter

                    movieAdapter.setOnMovieClickListener(object : MovieAdapter.OnMovieClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)

                            intent.putExtra("movieTitle", moviesList[position].title)
                            intent.putExtra("movieDescription", moviesList[position].description)
                            intent.putExtra("movieId", keysList[position])

                            startActivity(intent)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}