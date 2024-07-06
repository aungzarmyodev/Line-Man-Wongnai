package com.linemanwongnai.app.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.linemanwongnai.app.databinding.CoinListItemBinding
import com.linemanwongnai.app.databinding.FooterViewBinding
import com.linemanwongnai.app.databinding.HeaderViewBinding
import com.linemanwongnai.app.databinding.InviteFriendLayoutBinding
import javax.inject.Inject

class CoinListAdapter @Inject constructor() : RecyclerView.Adapter<ViewHolder>() {

    private val header = 0
    private val itemType = 1
    private val footer = 2
    private val inviteFriend = 3
    private var interval = 2
    private var showIndex = 2
    private var startInterval = 2

    private val data = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        when (viewType) {
            header -> {
                val view =
                    HeaderViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return HeaderViewHolder(view)
            }

            footer -> {
                val view =
                    FooterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return FooterViewHolder(view)
            }

            inviteFriend -> {
                val view =
                    InviteFriendLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return InviteFriendViewHolder(view)
            }

            else -> {
                val view =
                    CoinListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CoinViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return 20 + 2
    }

    override fun getItemViewType(position: Int): Int {

        return if (position == header) {
            header
        } else if (position == 51) {
            footer
        } else if (isShowInviteFriend(position)) {
            inviteFriend
        } else {
            itemType
        }
    }

    private fun isShowInviteFriend(position: Int): Boolean {
        var isShow = false
        if (position == showIndex + 1) {
            isShow = true
            startInterval *= interval
            showIndex += startInterval + 1
        }
        return isShow
    }

    fun addData(list: List<Int>) {

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is CoinViewHolder -> {
                holder.onBind()
            }

            is HeaderViewHolder -> {
                holder.onBind()
            }

            is InviteFriendViewHolder -> {
                holder.onBind()
                holder.binding.root.setOnClickListener {  }
            }

            is FooterViewHolder -> {

            }
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