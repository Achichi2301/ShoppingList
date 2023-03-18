package com.example.shopponglist.db

import android.content.Context
import androidx.room.*
import com.example.shopponglist.entites.*

@Database (entities = [LibraryItem::class, NoteItem::class,
    ShopListItem::class, ShopListNameItem::class], version = 1)
abstract class MainDataBase: RoomDatabase() {
    abstract fun getDao(): Dao

    companion object{
        @Volatile
        private var INSTANCE: MainDataBase? = null
        fun getDataBase(context: Context): MainDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    MainDataBase::class.java, "shopping_list.db").build()
                    instance
            }
        }
    }
}