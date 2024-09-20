package com.example.project1.data

import com.example.project1.model.Product
import com.example.project1.model.ProductCategory
import com.example.project1.model.ProductStatus
import java.util.UUID

interface ProductRepository {
    fun getProductList(): List<Product>
    fun getProduct(id: UUID): Product
    fun getProductListByCategory(category: ProductCategory): List<Product>
    fun getProductListByStatus(status: ProductStatus): List<Product>
    fun addProduct(product: Product)
    fun editProduct(id: UUID, product: Product)
    fun removeProduct(id: UUID)
    fun isProductValid(id: UUID): Boolean
}