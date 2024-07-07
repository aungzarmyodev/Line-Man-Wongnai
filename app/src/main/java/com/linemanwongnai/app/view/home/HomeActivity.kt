package com.linemanwongnai.app.view.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.linemanwongnai.app.R
import com.linemanwongnai.app.databinding.ActivityHomeBinding
import com.linemanwongnai.app.databinding.CoinDetailViewBottomSheetBinding
import com.linemanwongnai.app.model.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var adapter: CoinListAdapter

    private val viewModel: HomeViewModel by viewModels()
    private var isRefreshing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservable()
        initView()
    }

    private fun initView() {

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener {
            viewModel.getCoinList()
            isRefreshing = true
        }
    }

    private fun initObservable() {
        viewModel.liveData.observe(this) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE
                    if (!result.data.isNullOrEmpty()) {
                        binding.textViewEmpty.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.refreshLayout.isRefreshing = false

                        // get top rank three coin
                        val topRankThreeCoin =
                            result.data.filter { it.rank == 1 || it.rank == 2 || it.rank == 3 }

                        adapter.addData(result.data, isRefreshing)
                        adapter.addTopRankThreeCoin(topRankThreeCoin)

                    } else {
                        binding.textViewEmpty.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    binding.loading.visibility = View.GONE
                    binding.refreshLayout.isRefreshing = false
                    val message =
                        if (!result?.error?.message.isNullOrEmpty()) result?.error?.message.toString() else getString(
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
            val dialog = BottomSheetDialog(this)
            val bottomSheetBinding =
                CoinDetailViewBottomSheetBinding.inflate(layoutInflater, null, false)
            bottomSheetBinding.buttonGoToWebsite.setOnClickListener {
                dialog.dismiss()
                Toast.makeText(this, getString(R.string.label_go_to_website), Toast.LENGTH_LONG)
                    .show()
            }
            dialog.setCancelable(true)
            dialog.setContentView(bottomSheetBinding.root)
            dialog.show()
        }
    }
}