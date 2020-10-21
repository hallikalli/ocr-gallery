package com.hklee.ocrgallery.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URI

@Entity
data class OcrPhoto(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "ocr") val text: String
)