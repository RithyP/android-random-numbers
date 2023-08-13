package com.rithyphavan.randomnumber

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
    suspend fun Context.saveBitmapImgToCacheFile(
        bitmap: Bitmap, cachePathString: String, fileName: String
    ): Uri = withContext(Dispatchers.IO) {
        try {
            val cachePath = File(cacheDir, cachePathString)
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/${fileName}") // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val imagePath = File(cacheDir, cachePathString)
        val newFile = File(imagePath, fileName)

        FileProvider.getUriForFile(
            this@saveBitmapImgToCacheFile, "com.rithyphavan.randomnumber.provider", newFile,
        )

    }

    suspend fun getBitmapFromUri(context: Context, uriString: String): Bitmap? =
        withContext(Dispatchers.IO) {
            var bitmap: Bitmap? = null
            try {
                bitmap =
                    MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(uriString))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bitmap
        }
}