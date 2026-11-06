package com.example.flores.data.network

import com.example.flores.data.ItemModel
import com.google.gson.annotations.SerializedName

data class ApiItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)

fun ApiItem.toModel(): ItemModel {
    return ItemModel(
        id = id,
        title = title,
        description = body,
        isUrgent = false,
        category = "Web API",
        time = "12:00 PM"
    )
}