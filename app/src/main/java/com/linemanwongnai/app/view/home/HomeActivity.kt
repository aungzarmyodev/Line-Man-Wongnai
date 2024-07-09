package com.linemanwongnai.app.view.home

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.linemanwongnai.app.R
import com.linemanwongnai.app.databinding.ActivityHomeBinding
import com.linemanwongnai.app.databinding.CoinDetailViewBottomSheetBinding
import com.linemanwongnai.app.model.CoinModel
import com.linemanwongnai.app.model.FriendInviteModel
import com.linemanwongnai.app.model.Status
import com.linemanwongnai.app.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var adapter: CoinListAdapter

    private val viewModel: HomeViewModel by viewModels()
    private var isRefreshing = true
    private var offset = 1
    private var coinList = mutableListOf<Any>()
    private var topRankThreeCoinList = mutableListOf<CoinModel>()
    private var isLoadMore = false
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var visibleThreshold = 4
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var startIndexFriendInvite = 5
    private var showIndexFriendInvite = startIndexFriendInvite
    private var alreadyShowFriendInvite = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )

            android.content.res.Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservable()
        initView()
    }

    override fun onStart() {
        super.onStart()
        loadDataEveryTenSecond()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        // Remove callbacks to avoid memory leaks
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    private fun initView() {

        // coin list
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        binding.refreshLayout.setOnRefreshListener {
            coinList.clear()
            topRankThreeCoinList.clear()
            isRefreshing = true
            offset = 1
            viewModel.getCoinList(Utils.LIMIT, 0)
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0)
                    return

                totalItemCount = layoutManager.itemCount
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoadMore && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    offset++
                    viewModel.getCoinList(Utils.LIMIT, offset)
                    adapter.showLoadingBar(true)
                    isRefreshing = false
                    isLoadMore = true
                }
            }
        })

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!char.isNullOrEmpty()) {
                    viewModel.search(char.toString())
                    handler.removeCallbacks(runnable)
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.textViewEmpty.visibility = View.GONE
                    binding.textViewSorry.visibility = View.GONE
                    adapter.addData(coinList)
                    adapter.addTopRankThreeCoin(topRankThreeCoinList)
                    loadDataEveryTenSecond()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.imageClearIcon.setOnClickListener {
            binding.editTextSearch.text = null
            binding.recyclerView.visibility = View.VISIBLE
            binding.textViewEmpty.visibility = View.GONE
            binding.textViewSorry.visibility = View.GONE
            adapter.addData(coinList)
            adapter.addTopRankThreeCoin(topRankThreeCoinList)
        }
    }

    private fun loadDataEveryTenSecond() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                // Load data
                if (isRefreshing) {
                    val limit = Utils.LIMIT * offset
                    viewModel.getCoinList(limit, 0)
                }
                // Schedule the runnable to run again after 10 seconds
                handler.postDelayed(this, 10000)
            }
        }
        // Start the initial run
        handler.post(runnable)
    }

    private fun initObservable() {
        viewModel.liveData.observe(this) { result ->
            adapter.showLoadingBar(false)
            when (result.status) {
                Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE
                    isLoadMore = false

                    if (!result?.data.isNullOrEmpty()) {
                        binding.textViewEmpty.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.refreshLayout.isRefreshing = false

                        // set default values
                        if (isRefreshing) {
                            coinList.clear()
                            topRankThreeCoinList.clear()
                            showIndexFriendInvite = startIndexFriendInvite
                            alreadyShowFriendInvite = 0
                        }

                        coinList.addAll(result.data!!)
                        // calculation for add friend invite layout interval 5,10,20,40
                        while (coinList.size >= showIndexFriendInvite) { // +2 for top rank three layout + Buy,Sell and hold crypto
                            coinList.add(
                                showIndexFriendInvite,
                                FriendInviteModel(Utils.FRIEND_INVITE_LINK)
                            )
                            showIndexFriendInvite =
                                showIndexFriendInvite * 2 + startIndexFriendInvite
                        }

                        adapter.addData(coinList)
                        // get top rank three coin
                        topRankThreeCoinList.addAll(
                            result.data.filter { it.rank == 1 || it.rank == 2 || it.rank == 3 })
                        adapter.addTopRankThreeCoin(topRankThreeCoinList)
                        // set refreshing = true
                        isRefreshing = true

                    } else {
                        if (adapter.itemCount == 0) {
                            binding.textViewEmpty.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            Toast.makeText(this, getString(R.string.empty_data), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }

                Status.ERROR -> {

                    binding.refreshLayout.isRefreshing = false
                    if (adapter.itemCount == 0) {
                        binding.loading.visibility = View.GONE
                    }
                    val message =
                        if (!result?.error?.localizedMessage.isNullOrEmpty()) result?.error?.localizedMessage.toString() else getString(
                            R.string.label_unknown_error
                        )
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }

                Status.LOADING -> {
                    binding.loading.visibility = View.VISIBLE
                }
            }
        }

        adapter.itemClick.observe(this) { coinModel ->
            if (coinModel != null) {
                viewModel.getCoinDetail(coinModel.uuid)
            }
        }

        viewModel.coinDetailLiveData.observe(this) { result ->
            val coinModel = result?.data
            if (coinModel != null) {
                val dialog = BottomSheetDialog(this)
                val bottomSheetBinding =
                    CoinDetailViewBottomSheetBinding.inflate(layoutInflater, null, false)
                bottomSheetBinding.buttonGoToWebsite.setOnClickListener {
                    dialog.dismiss()
                    if (coinModel.websiteUrl != null) {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(coinModel.websiteUrl))
                        startActivity(browserIntent)
                    }
                }
                Utils.setImage(this, coinModel.iconUrl, bottomSheetBinding.imageCoinIcon)
                bottomSheetBinding.textViewCoinName.text = coinModel.name
                if (!coinModel.textColor.isNullOrEmpty()) {
                    if (coinModel.textColor!!.length == 6) {
                        bottomSheetBinding.textViewCoinName.setTextColor(Color.parseColor(coinModel.textColor))
                    } else {
                        bottomSheetBinding.textViewCoinName.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.color_textView_coin_name
                            )
                        )
                    }
                } else {
                    bottomSheetBinding.textViewCoinName.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.color_textView_coin_name
                        )
                    )
                }

                bottomSheetBinding.textViewCoinSymbol.text =
                    getString(R.string.label_coin_symbol, coinModel.symbol)

                val priceFormat = DecimalFormat("#,###.##")
                try {
                    bottomSheetBinding.textViewPrice.text = getString(
                        R.string.label_coin_price,
                        priceFormat.format(coinModel.price)
                    )
                } catch (_: Exception) {
                }

                // check million, billion, trillion
                if (coinModel.marketCap != null) {
                    try {
                        val marketCap = coinModel.marketCap!!.toLong()
                        if (marketCap >= Utils.MILLION && marketCap < Utils.BILLION) {
                            val million = marketCap / Utils.MILLION
                            bottomSheetBinding.textViewMarketCap.text =
                                getString(R.string.label_price_million, priceFormat.format(million))
                        } else if (marketCap >= Utils.BILLION && marketCap < Utils.TRILLION) {
                            val billion = marketCap / Utils.BILLION
                            bottomSheetBinding.textViewMarketCap.text =
                                getString(R.string.label_price_billion, priceFormat.format(billion))
                        } else if (marketCap >= Utils.TRILLION) {
                            val trillion = marketCap / Utils.TRILLION
                            bottomSheetBinding.textViewMarketCap.text =
                                getString(
                                    R.string.label_price_trillion,
                                    priceFormat.format(trillion)
                                )
                        } else {
                            bottomSheetBinding.textViewMarketCap.text =
                                getString(R.string.label_coin_price, priceFormat.format(marketCap))
                        }
                    } catch (_: Exception) {
                    }
                }
                bottomSheetBinding.textViewDescription.text = coinModel.description
                dialog.setCancelable(true)
                dialog.setContentView(bottomSheetBinding.root)
                dialog.show()
            }
        }

        searchObservable()
        inviteFriendObservable()
    }

    private fun searchObservable() {
        viewModel.searchLiveData.observe(this) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    val searchCoinList = result?.data
                    if (!searchCoinList.isNullOrEmpty()) {
                        adapter.addSearchData(searchCoinList)
                    } else {
                        binding.textViewEmpty.visibility = View.VISIBLE
                        binding.textViewSorry.visibility = View.VISIBLE
                        binding.textViewEmpty.text = getString(R.string.label_no_result_by_search)
                        binding.recyclerView.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    binding.refreshLayout.isRefreshing = false
                    val message =
                        if (!result?.error?.localizedMessage.isNullOrEmpty()) result?.error?.localizedMessage.toString() else getString(
                            R.string.label_unknown_error
                        )
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }

                Status.LOADING -> {

                }
            }
        }
    }

    private fun inviteFriendObservable() {
        adapter.itemFriendInviteClick.observe(this) { url ->
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
}