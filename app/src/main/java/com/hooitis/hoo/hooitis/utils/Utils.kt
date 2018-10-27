package com.hooitis.hoo.hooitis.utils

import android.graphics.Bitmap
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.util.*

@Suppress("unused")
class Utils {
    companion object {
        fun makeCalendarToDate(calendar: Calendar): Date{
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.time
        }

        fun uploadImageToGCS(bitmap: Bitmap, storageReference: StorageReference): UploadTask {
            val fileName = "${Utils.makeRandomString()}.png"
            val imageRef = storageReference.child(fileName)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            return imageRef.putBytes(baos.toByteArray())
        }

        fun getBasalMetabolism(weight: Int, height: Int, age: Int, sex: Int): Double {
            val basalMetabolism:Double = (12.2 * weight) - (4.82 * age) - (126.1 * sex) + (2.85 * height) + 468.3
            return basalMetabolism
        }

        fun getKcal(bM: Double, lifeType: Double): Int = (bM * lifeType).toInt()

        fun getCarbonHydrate(weight: Int, height: Int, age: Int, sex: Int,  lifeType: Double): String{
            val basalMetabolism = getBasalMetabolism(weight, height, age, sex)

            val kcal: Int = getKcal(basalMetabolism , lifeType)
            return ((kcal * 0.65) / 4).toString()
        }

        fun getFat(weight: Int, height: Int, age: Int, sex: Int,  lifeType: Double): String {
            val basalMetabolism = getBasalMetabolism(weight, height, age, sex)
            val kcal: Int = getKcal(basalMetabolism , lifeType)
            return  ((kcal * 0.15) / 9).toString()
        }

        fun getProtein(weight: Int, height: Int, age: Int, sex: Int,  lifeType: Double): String {
            val basalMetabolism = getBasalMetabolism(weight, height, age, sex)
            val kcal: Int = getKcal(basalMetabolism , lifeType)
            return ((kcal * 0.20) / 4).toString()
        }

        fun makeRandomString(): String = UUID.randomUUID().toString()

        fun convertString2List(value: String): List<String> = Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }
}
