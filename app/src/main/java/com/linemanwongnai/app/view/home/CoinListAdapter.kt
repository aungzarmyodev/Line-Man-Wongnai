package com.linemanwongnai.app.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.linemanwongnai.app.R
import com.linemanwongnai.app.databinding.CoinListItemBinding
import com.linemanwongnai.app.databinding.FooterViewBinding
import com.linemanwongnai.app.databinding.HeaderViewBinding
import com.linemanwongnai.app.databinding.InviteFriendLayoutBinding
import com.linemanwongnai.app.databinding.ItemViewHeaderLayoutBinding
import com.linemanwongnai.app.model.CoinModel
import com.linemanwongnai.app.utils.Utils
import java.text.DecimalFormat
import javax.inject.Inject

class CoinListAdapter @Inject constructor() : RecyclerView.Adapter<ViewHolder>() {

    private val headerType = 0
    private val itemHeaderType = 1
    private val itemType = 2
    private val footerType = 3

    private val coinList = mutableListOf<CoinModel>()
    private val topRankThreeCoinList = mutableListOf<CoinModel>()
    val itemClick = MutableLiveData<CoinModel>()

    // filter by search coin
    private var isSearch = false

    private var footerViewHolder: FooterViewHolder? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (viewType) {
            footerType -> {
                val view =
                    FooterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                footerViewHolder = FooterViewHolder(view)
                footerViewHolder as FooterViewHolder
            }

            headerType -> {
                val view =
                    HeaderViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(view)
            }

            itemHeaderType -> {
                val view =
                    ItemViewHeaderLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                CoinItemListViewHeaderViewHolder(view)
            }

            else -> {
                val view =
                    CoinListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CoinViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if (isSearch) {
            when (position) {
                0 -> itemHeaderType
                else -> itemType
            }
        } else {
            when (position) {
                headerType -> headerType
                itemHeaderType -> itemHeaderType
                coinList.size + 2 -> footerType
                else -> itemType
            }
        }
    }

    override fun getItemCount(): Int {
        var itemCount = 0
        if (isSearch) {
            itemCount = coinList.size + 1
        } else {
            if (coinList.isNotEmpty()) {
                itemCount = if (topRankThreeCoinList.isNotEmpty()) coinList.size + 3
                else coinList.size + 2
            }
        }
        return itemCount
    }

    fun addData(list: List<CoinModel>, isRefreshing: Boolean) {
        isSearch = false
        if (isRefreshing) {
            coinList.clear()
        }
        coinList.addAll(list)
        notifyDataSetChanged()
    }

    fun addSearchData(list: List<CoinModel>) {
        isSearch = true
        coinList.clear()
        coinList.addAll(list)
        notifyDataSetChanged()
    }

    // add top rank 3 coin
    fun addTopRankThreeCoin(list: List<CoinModel>) {
        topRankThreeCoinList.clear()
        topRankThreeCoinList.addAll(list)
        notifyDataSetChanged()
    }

    fun showLoadingBar(isShowLoadingBar: Boolean) {
        if (footerViewHolder != null) {
            footerViewHolder!!.binding.loading.visibility =
                if (isShowLoadingBar) View.VISIBLE else View.GONE
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is CoinViewHolder -> {
                if (topRankThreeCoinList.isNotEmpty() && !isSearch) {
                    holder.onBind(coinList[position - 2])
                } else {
                    holder.onBind(coinList[position - 1])
                }

                holder.itemView.setOnClickListener {
                    if (topRankThreeCoinList.isNotEmpty() && !isSearch) {
                        itemClick.postValue(coinList[position - 2])
                    } else {
                        itemClick.postValue(coinList[position - 1])
                    }
                }
            }

            is HeaderViewHolder -> {
                holder.onBind(topRankThreeCoinList)
                holder.binding.topOneLayout.setOnClickListener {
                    itemClick.postValue(topRankThreeCoinList[0])
                }
                holder.binding.topTwoLayout.setOnClickListener {
                    itemClick.postValue(topRankThreeCoinList[1])
                }
                holder.binding.topThreeLayout.setOnClickListener {
                    itemClick.postValue(topRankThreeCoinList[2])
                }
            }
        }
    }
}

class CoinViewHolder(private val binding: CoinListItemBinding) :
    ViewHolder(binding.root) {

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

class CoinItemListViewHeaderViewHolder(binding: ItemViewHeaderLayoutBinding) :
    ViewHolder(binding.root)

class HeaderViewHolder(val binding: HeaderViewBinding) : ViewHolder(binding.root) {
    fun onBind(coinList: List<CoinModel>) {
        binding.textViewLabelTop3Coin.text = HtmlCompat.fromHtml(
            binding.root.context.getString(R.string.label_top_rank_three_coin),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        coinList.forEach { coinModel ->
            when (coinModel.rank) {
                1 -> {
                    showTopOneCoin(coinModel)
                }

                2 -> {
                    showTopTwoCoin(coinModel)
                }

                3 -> {
                    showTopThreeCoin(coinModel)
                }
            }
        }
    }

    private fun showTopOneCoin(coinModel: CoinModel) {
        binding.includedTopOneLayout.textViewCoinName.text = coinModel.name
        binding.includedTopOneLayout.textViewCoinSymbol.text = coinModel.symbol
        Utils.setImage(
            binding.root.context,
            coinModel.iconUrl,
            binding.includedTopOneLayout.imageCoinIcon
        )

        if (!coinModel.change.isNullOrEmpty()) {
            if (coinModel.change!!.contains("-")) {
                binding.includedTopOneLayout.textViewChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_textView_decrease_rate
                    )
                )
                binding.includedTopOneLayout.imageDown.visibility = View.VISIBLE
                binding.includedTopOneLayout.imageUp.visibility = View.GONE
            } else {
                binding.includedTopOneLayout.textViewChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_textView_increase_rate
                    )
                )
                binding.includedTopOneLayout.imageDown.visibility = View.GONE
                binding.includedTopOneLayout.imageUp.visibility = View.VISIBLE
            }
            binding.includedTopOneLayout.textViewChange.text = coinModel.change
        }
    }

    private fun showTopTwoCoin(coinModel: CoinModel) {
        binding.includedTopTwoLayout.textViewCoinName.text = coinModel.name
        binding.includedTopTwoLayout.textViewCoinSymbol.text = coinModel.symbol
        Utils.setImage(
            binding.root.context,
            coinModel.iconUrl,
            binding.includedTopTwoLayout.imageCoinIcon
        )

        if (!coinModel.change.isNullOrEmpty()) {
            if (coinModel.change!!.contains("-")) {
                binding.includedTopTwoLayout.textViewChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_textView_decrease_rate
                    )
                )
                binding.includedTopTwoLayout.imageDown.visibility = View.VISIBLE
                binding.includedTopTwoLayout.imageUp.visibility = View.GONE
            } else {
                binding.includedTopTwoLayout.textViewChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_textView_increase_rate
                    )
                )
                binding.includedTopTwoLayout.imageDown.visibility = View.GONE
                binding.includedTopTwoLayout.imageUp.visibility = View.VISIBLE
            }
            binding.includedTopTwoLayout.textViewChange.text = coinModel.change
        }
    }

    private fun showTopThreeCoin(coinModel: CoinModel) {
        binding.includedTopThreeLayout.textViewCoinName.text = coinModel.name
        binding.includedTopThreeLayout.textViewCoinSymbol.text = coinModel.symbol
        Utils.setImage(
            binding.root.context,
            coinModel.iconUrl,
            binding.includedTopThreeLayout.imageCoinIcon
        )

        if (!coinModel.change.isNullOrEmpty()) {
            if (coinModel.change!!.contains("-")) {
                binding.includedTopThreeLayout.textViewChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_textView_decrease_rate
                    )
                )
                binding.includedTopThreeLayout.imageDown.visibility = View.VISIBLE
                binding.includedTopThreeLayout.imageUp.visibility = View.GONE
            } else {
                binding.includedTopThreeLayout.textViewChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_textView_increase_rate
                    )
                )
                binding.includedTopThreeLayout.imageDown.visibility = View.GONE
                binding.includedTopThreeLayout.imageUp.visibility = View.VISIBLE
            }
            binding.includedTopThreeLayout.textViewChange.text = coinModel.change
        }
    }
}

class FooterViewHolder(val binding: FooterViewBinding) : ViewHolder(binding.root)

class InviteFriendViewHolder(val binding: InviteFriendLayoutBinding) :
    ViewHolder(binding.root) {
    fun onBind() {

    }
}