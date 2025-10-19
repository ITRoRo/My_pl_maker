package com.example.myplmaker.search.data.model

sealed class Answers<T>(val data: T? = null, val answer : Int? = null) {
    class Error<T>(answer: Int, data: T? = null) : Answers<T>(data, answer)
    class Success<T>(data: T?) : Answers<T>(data)
}