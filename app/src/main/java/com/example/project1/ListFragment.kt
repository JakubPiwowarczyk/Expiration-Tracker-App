package com.example.project1

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project1.adapters.ProductListAdapter
import com.example.project1.data.InMemoryProductRepository
import com.example.project1.data.ProductRepository
import com.example.project1.databinding.FragmentListBinding
import com.example.project1.model.FormType
import com.example.project1.model.ProductCategory
import com.example.project1.model.ProductStatus

class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var productAdapter: ProductListAdapter
    private val productRepository: ProductRepository = InMemoryProductRepository
    private var filterOption: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentListBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        productAdapter = ProductListAdapter(
            onClick = {

                if (productRepository.isProductValid(it)){
                    findNavController().navigate(
                        R.id.action_listFragment_to_formFragment,
                        bundleOf("type" to FormType.Edit(it))
                    )
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("You cannot edit expired items!")
                        .setPositiveButton("Ok") {dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            },
            onHold = {
                if (productRepository.isProductValid(it)){
                    AlertDialog.Builder(context)
                        .setTitle("Remove product?")
                        .setPositiveButton("Accept") {dialog, _ ->
                            productRepository.removeProduct(it)
                            productAdapter.productList = productRepository.getProductList()
                            dialog.dismiss()
                            binding.productsCountLabel.text = productAdapter.itemCount.toString()
                            filterOption = 0
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("You cannot remove expired items!")
                        .setPositiveButton("Ok") {dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        )

        binding.list.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(context)
        }

        productAdapter.productList = productRepository.getProductList()
        binding.productsCountLabel.text = productAdapter.itemCount.toString()

        binding.addButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_listFragment_to_formFragment,
                bundleOf("type" to FormType.New)
            )
        }

        binding.filterButton.setOnClickListener {
            val options = arrayOf("All", "Valid", "Expired", "Food", "Medicines", "Cosmetics")
            AlertDialog.Builder(context)
                .setTitle("Show:")
                .setSingleChoiceItems(options, filterOption) { _, which ->
                    when (which) {
                        0 -> productAdapter.productList = productRepository.getProductList()
                        1 -> productAdapter.productList =
                            productRepository.getProductListByStatus(ProductStatus.Valid)
                        2 -> productAdapter.productList =
                            productRepository.getProductListByStatus(ProductStatus.Expired)
                        3 -> productAdapter.productList =
                            productRepository.getProductListByCategory(ProductCategory.Food)
                        4 -> productAdapter.productList =
                            productRepository.getProductListByCategory(ProductCategory.Medicine)
                        5 -> productAdapter.productList =
                            productRepository.getProductListByCategory(ProductCategory.Cosmetics)
                    }
                    binding.productsCountLabel.text = productAdapter.itemCount.toString()
                    filterOption = which
                }
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }



    override fun onStart() {
        super.onStart()
        findNavController()
            .addOnDestinationChangedListener(::onChangeDst)
    }

    private fun onChangeDst(
        navController: NavController,
        destination: NavDestination,
        bundle: Bundle?
    ) {
        if (destination.id == R.id.listFragment) {
            productAdapter.productList = productRepository.getProductList()
            filterOption = 0
            binding.productsCountLabel.text = productAdapter.itemCount.toString()
        }
    }

    override fun onStop() {
        super.onStop()
        findNavController()
            .removeOnDestinationChangedListener(::onChangeDst)
    }
}