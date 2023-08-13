package com.rithyphavan.randomnumber

import android.net.Uri
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.rithyphavan.randomnumber.FileUtils.getBitmapFromUri
import com.rithyphavan.randomnumber.FileUtils.saveBitmapImgToCacheFile
import com.rithyphavan.randomnumber.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val vm: MainViewModel by viewModels()

    private val imageAdapter by lazy {
        MainAdapter()
    }

    override fun initView() {
        binding.imageList.adapter = imageAdapter
    }

    override fun initAction() {
        binding.generateBtn.setOnClickListener {
            vm.setUriList(null) // Clear list
            vm.generateXYRandomNumber()
            onRandomNumberGenerated()
        }
    }

    private fun onRandomNumberGenerated() {
        binding.xToYRange.text = "X = ${vm.randomNum?.first} and Y = ${vm.randomNum?.last} "
        lifecycleScope.launch(Dispatchers.IO) {
            generateBitMap()
        }
    }

    private suspend fun generateBitMap() = withContext(Dispatchers.IO) {
        vm.randomNum?.let { rangeNum ->
            val uriList = mutableListOf<Uri>()
            for (i in rangeNum) {
                val bitmapUri = saveBitmapImgToCacheFile(
                    BitmapUtil.textAsBitmap(i.toString(), 150f), "numberImages", "imageNumber$i"
                )
                uriList.add(
                    bitmapUri
                )
                vm.setUriList(uriList)
            }
        }
    }

    override fun initObserver() {
        lifecycleScope.launch(Dispatchers.Main) {
            vm.numbersBitMapUri.collect { uriList ->
                if (!uriList.isNullOrEmpty()) {
                    for (uri in uriList) {
                        val bitmapImage = getBitmapFromUri(this@MainActivity, uri.toString())
                        if (bitmapImage != null) {
                            imageAdapter.addItem(bitmapImage)
                        }
                    }
                }
            }
        }
    }


}