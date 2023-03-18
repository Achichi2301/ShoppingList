package com.example.shopponglist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopponglist.activities.MainApp
import com.example.shopponglist.activities.ShopListActivity
import com.example.shopponglist.databinding.FragmentShopListNamesBinding
import com.example.shopponglist.db.MainViewModel
import com.example.shopponglist.db.ShopListNameAdapter
import com.example.shopponglist.dialogs.DeleteDialog
import com.example.shopponglist.dialogs.NewListDialog
import com.example.shopponglist.entites.ShopListNameItem
import com.example.shopponglist.utils.TimeManager


class ShopListNamesFragment : BaseFragment(), ShopListNameAdapter.Listener{
    private lateinit var binding: FragmentShopListNamesBinding
    private lateinit var adapter: ShopListNameAdapter

    private val mainViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFractory((context?.applicationContext as MainApp).database)
    }
    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener{
            override fun onClick(name: String) {
                val shopLisName = ShopListNameItem(
                    null,
                    name,
                    TimeManager.getCurrentTime(),
                    0,
                    0,
                    ""
                )
                mainViewModel.insertShopListName(shopLisName)
            }

        }, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopListNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    private fun initRcView() = with(binding){
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = ShopListNameAdapter(this@ShopListNamesFragment)
        rcView.adapter = adapter
    }

    private fun observer(){
        mainViewModel.allShopListNames.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }



    companion object {
        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                mainViewModel.deleteShopList(id, true)
            }

        })
    }

    override fun editItem(shopListNameItem: ShopListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener{
            override fun onClick(name: String) {
                mainViewModel.updateListName(shopListNameItem.copy(name = name))
            }

        }, shopListNameItem.name)
    }

    override fun onClickItem(shopListNameItem: ShopListNameItem) {
        val i = Intent(activity, ShopListActivity::class.java).apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME, shopListNameItem)
        }
        startActivity(i)
    }
}