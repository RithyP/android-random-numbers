package com.rithyphavan.randomnumber

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class MainViewModel : ViewModel() {

    private val _numbersBitMapUri = MutableStateFlow<MutableList<Uri>?>(null)
    val numbersBitMapUri = _numbersBitMapUri.asStateFlow()

    fun setUriList(list: List<Uri>?) {
        _numbersBitMapUri.value = list?.toMutableList()
    }

    fun appendUriList(uri: Uri) {
        val tempList = _numbersBitMapUri.value
        tempList?.add(uri)
        _numbersBitMapUri.value = tempList
    }

    var randomNum: IntRange? = null
        private set

    fun generateXYRandomNumber() {
        val x = Random.nextInt(0, Int.MAX_VALUE)
        val y = Random.nextInt(x + 20, Int.MAX_VALUE)
        randomNum = x..y
    }

    fun storeBitmapInFile() {

    }


}