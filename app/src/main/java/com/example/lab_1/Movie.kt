package com.example.lab_1

data class Movie(
    var title: String? = null,
    var description: String? = null,
    var images: ArrayList<String>? = arrayListOf()
)