package com.example.flores.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flores.data.ItemModel

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val isUrgent: Boolean,
    val category: String,
    val time: String
)

fun ItemEntity.toModel(): ItemModel {
    return ItemModel(
        id = id,
        title = title,
        description = description,
        isUrgent = isUrgent,
        category = category,
        time = time
    )
}

fun ItemModel.toEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        title = title,
        description = description,
        isUrgent = isUrgent,
        category = category,
        time = time
    )
}