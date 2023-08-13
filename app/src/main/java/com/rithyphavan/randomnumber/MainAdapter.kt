package com.rithyphavan.randomnumber

import android.graphics.Bitmap
import com.rithyphavan.randomnumber.databinding.LayoutNumberImageItemBinding

class MainAdapter :
    BaseAdapter<LayoutNumberImageItemBinding, Bitmap>(LayoutNumberImageItemBinding::inflate) {
    override fun onBind(holder: VH<LayoutNumberImageItemBinding, Bitmap>, item: Bitmap) {
        holder.binding.numberImage.setImageBitmap(item)
    }
}