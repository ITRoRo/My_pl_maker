package com.example.myplmaker.data

import com.example.myplmaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}