package com.example.project1.data

import com.example.project1.model.Product
import com.example.project1.model.ProductCategory
import com.example.project1.model.ProductStatus
import java.time.LocalDate
import java.util.UUID

object InMemoryProductRepository : ProductRepository{
    private val productListHolder = mutableListOf(
        Product("Pizza", ProductCategory.Food, 8, LocalDate.of(2024, 5, 15)),
        Product("Dior Home", ProductCategory.Cosmetics, 1, LocalDate.of(2026, 5, 15)),
        Product("Apap", ProductCategory.Medicine, 1, LocalDate.of(2024, 12, 6)),
        Product("Bananas", ProductCategory.Food, 6, LocalDate.of(2024, 4, 28))
    )

    override fun getProductList(): List<Product> = productListHolder.sortedBy { it.expirationDate }

    override fun getProduct(id: UUID): Product = productListHolder.first { it.id == id }

    override fun getProductListByCategory(category: ProductCategory): List<Product> {
        return productListHolder.filter { it.category == category }.sortedBy { it.expirationDate }
    }

    override fun getProductListByStatus(status: ProductStatus): List<Product> {
        return productListHolder.filter { it.status == status }.sortedBy { it.expirationDate }
    }

    override fun addProduct(product: Product) {
        productListHolder.add(product)
    }

    override fun editProduct(id: UUID, product: Product) {
        val index = productListHolder.indexOfFirst { it.id == id }
        productListHolder[index] = product
    }

    override fun removeProduct(id: UUID) {
        productListHolder.removeIf { it.id == id }
    }

    override fun isProductValid(id: UUID): Boolean {
        val product = productListHolder.first { it.id == id }
        return product.status == ProductStatus.Valid
    }


}