package com.linemanwongnai.app.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.linemanwongnai.app.R
import com.linemanwongnai.app.databinding.CoinListItemBinding
import com.linemanwongnai.app.model.CoinModel
import com.linemanwongnai.app.utils.Utils
import java.text.DecimalFormat
import javax.inject.Inject

class SearchCoinListAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val coinList = mutableListOf<CoinModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = CoinListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchCoinViewHolder(view)
    }

    override fun getItemCount(): Int {
        return coinList.size
    }

    fun addData(list: List<CoinModel>) {
        coinList.clear()
        coinList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchCoinViewHolder) {
            holder.onBind(coinList[position])
        }
    }
}

class SearchCoinViewHolder(private val binding: CoinListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(coinModel: CoinModel) {
        binding.textViewCoinName.text = coinModel.name
        binding.textViewCoinSymbol.text = coinModel.symbol
        val priceFormat = DecimalFormat("#,###.#####")
        try {
            binding.textViewCoinAmount.text =
                binding.root.context.getString(
                    R.string.label_coin_price,
                    priceFormat.format(coinModel.price)
                )
        } catch (_: Exception) {
        }

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