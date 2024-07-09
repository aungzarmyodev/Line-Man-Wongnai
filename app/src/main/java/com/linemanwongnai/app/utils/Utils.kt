package com.linemanwongnai.app.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.linemanwongnai.app.R

object Utils {

    const val MILLION = 1000000f
    const val BILLION = 1000000000f
    const val TRILLION = 1000000000000f
    const val LIMIT = 20
    const val SEARCH_LIMIT = 7
    const val FRIEND_INVITE_LINK = "https://careers.lmwn.com"
    const val API_KEY = "coinrankingbc93fd9e46077d7761d92984ab58ec4e03a1f41ffc64ad1e"
    fun setImage(context: Context, imageUri: String?, imageView: AppCompatImageView) {

        // image caching
        Glide.with(context)
            .load(imageUri)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.background_circle_image_view)
            .override(48, 48)
            .error(R.drawable.background_circle_image_view)
            .into(imageView)
    }
}