package com.example.myplmaker.search.data.network

import com.example.myplmaker.search.data.dto.Response


interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
} 