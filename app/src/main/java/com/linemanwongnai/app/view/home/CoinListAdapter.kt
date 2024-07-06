package com.linemanwongnai.app.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.linemanwongnai.app.databinding.CoinListItemBinding
import javax.inject.Inject

class CoinListAdapter @Inject constructor() : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CoinListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is CoinViewHolder) {
            holder.onBind()
        }
    }
}

class CoinViewHolder(private val binding: CoinListItemBinding) :
    ViewHolder(binding.root) {

    fun onBind() {
        binding.textViewCoinName.text = "Bitcoin"
        binding.textViewCoinSymbol.text = "BTC"
        binding.textViewCoinAmount.text = "\$ 1245.12"
        binding.textViewInterestRate.text = "1.7"
    }

}