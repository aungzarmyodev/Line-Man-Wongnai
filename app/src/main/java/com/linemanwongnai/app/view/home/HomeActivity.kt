package com.linemanwongnai.app.view.home

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.linemanwongnai.app.R
import com.linemanwongnai.app.databinding.ActivityHomeBinding
import com.linemanwongnai.app.model.Status
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
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
                    if (!result.data?.data?.coinList.isNullOrEmpty()) {
                        binding.textViewEmpty.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.refreshLayout.isRefreshing = false
                        adapter.addData(result.data!!.data.coinList, isRefreshing)

                    } else {
                        binding.textViewEmpty.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    when (result?.error) {
                        is IOException -> {
                            val snackBar = Snackbar.make(
                                binding.root,
                                getString(R.string.label_no_network_error),
                                Snackbar.LENGTH_INDEFINITE
                            )
                            snackBar.setAction(
                                getString(R.string.label_ok)
                            ) { snackBar.dismiss() }
                            snackBar.show()
                        }

                        else -> {
                            val snackBar = Snackbar.make(
                                binding.recyclerView,
                                if (!result?.error?.message.isNullOrEmpty()) result?.error?.message.toString() else getString(
                                    R.string.label_unknown_error
                                ),
                                Snackbar.LENGTH_LONG
                            )
                            snackBar.show()
                        }
                    }
                }

                Status.LOADING -> {

                }
            }
        }
    }
}