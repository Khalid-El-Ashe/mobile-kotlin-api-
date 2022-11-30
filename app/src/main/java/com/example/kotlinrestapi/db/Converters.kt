package com.example.kotlinrestapi.db

import androidx.room.TypeConverter
import com.example.kotlinrestapi.models.Source

// هذا الكلاس عبارة عن محول لتسهيل عملية استخدام كلاس السورس من حيث نوع العملية المراد استخدامها من هذ الكلاس
class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}