package com.example.project1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.databinding.ItemProductBinding
import com.example.project1.model.Product
import java.util.UUID

class ProductView(private val itemBinding: ItemProductBinding) : RecyclerView.ViewHolder(itemBinding.root) {
    lateinit var product: Product

    fun onBind(product: Product) {
        this.product = product
        with(itemBinding) {
            name.text = product.name
            category.text = product.category.name
            quantity.text = product.quantity.toString()
            expirationDate.text = product.expirationDate.toString()
            statusImage.setImageResource(product.status.statusIcon)
        }
    }
}

class ProductListAdapter(
    private val onClick: (UUID) -> Unit,
    private val onHold: (UUID) -> Unit,
) : RecyclerView.Adapter<ProductView>() {
    var productList = listOf<Product>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductView {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductBinding.inflate(inflater, parent, false)
        return ProductView(binding).also { holder ->
            binding.root.setOnClickListener { view ->
                onClick(holder.product.id)
            }
            binding.root.setOnLongClickListener { view ->
                onHold(holder.product.id)
                true
            }
        }
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductView, position: Int) {
        holder.onBind(productList[position])
    }
}