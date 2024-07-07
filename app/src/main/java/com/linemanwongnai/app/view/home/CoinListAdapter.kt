package com.linemanwongnai.app.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.linemanwongnai.app.R
import com.linemanwongnai.app.databinding.CoinListItemBinding
import com.linemanwongnai.app.databinding.FooterViewBinding
import com.linemanwongnai.app.databinding.HeaderViewBinding
import com.linemanwongnai.app.databinding.InviteFriendLayoutBinding
import com.linemanwongnai.app.model.CoinModel
import com.linemanwongnai.app.utils.Utils
import javax.inject.Inject

class CoinListAdapter @Inject constructor() : RecyclerView.Adapter<ViewHolder>() {

    private val itemType = 0
    private val footerType = 1

    private val data = mutableListOf<CoinModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if (viewType == footerType) {
            val view =
                FooterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FooterViewHolder(view)

        } else {
            val view =
                CoinListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CoinViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            data.size -> footerType
            else -> itemType
        }
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    fun addData(list: List<CoinModel>, isRefreshing: Boolean) {
        if (isRefreshing) {
            data.clear()
        }
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is CoinViewHolder -> {
                holder.onBind(data[position])
            }

//            is HeaderViewHolder -> {
//                holder.onBind()
//            }
//
//            is InviteFriendViewHolder -> {
//                holder.onBind()
//                holder.binding.root.setOnClickListener { }
//            }
//
//            is FooterViewHolder -> {
//
//            }
        }
    }
}

class CoinViewHolder(private val binding: CoinListItemBinding) :
    ViewHolder(binding.root) {

    fun onBind(coinModel: CoinModel) {
        binding.textViewCoinName.text = coinModel.name
        binding.textViewCoinSymbol.text = coinModel.symbol
        binding.textViewCoinAmount.text =
            binding.root.context.getString(R.string.label_coin_price, coinModel.price)

        if (!coinModel.change.isNullOrEmpty()) {
            if (coinModel.change!!.contains("-")) {
                binding.textViewChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_textView_decrease_rate
                    )
                )
                binding.imageDown.visibility = View.VISIBLE
                binding.imageUp.visibility = View.GONE
            } else {
                binding.textViewChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_textView_increase_rate
                    )
                )
                binding.imageDown.visibility = View.GONE
                binding.imageUp.visibility = View.VISIBLE
            }
            binding.textViewChange.text = coinModel.change
        }

        if (!coinModel.iconUrl.isNullOrEmpty()) {
            Utils.setImage(binding.root.context, coinModel.iconUrl, binding.imageIcon)
        }
    }
}

class HeaderViewHolder(private val binding: HeaderViewBinding) : ViewHolder(binding.root) {
    fun onBind() {

    }
}

class FooterViewHolder(private val binding: FooterViewBinding) : ViewHolder(binding.root) {
    fun onBind() {
        // binding.textView2
    }
}

class InviteFriendViewHolder(val binding: InviteFriendLayoutBinding) :
    ViewHolder(binding.root) {
    fun onBind() {

    }
}