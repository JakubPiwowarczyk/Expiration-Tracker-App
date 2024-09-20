package com.example.project1

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.project1.data.InMemoryProductRepository
import com.example.project1.data.ProductRepository
import com.example.project1.databinding.FragmentFormBinding
import com.example.project1.model.FormType
import com.example.project1.model.Product
import com.example.project1.model.ProductCategory
import java.time.LocalDate

private const val FORM_TYPE = "type"

class FormFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: FragmentFormBinding
    private lateinit var formType: FormType
    private lateinit var repository: ProductRepository

    private lateinit var product: Product

    private var year: Int = LocalDate.now().year
    private var month: Int = LocalDate.now().monthValue
    private var day: Int = LocalDate.now().dayOfMonth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            formType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(FORM_TYPE, FormType::class.java)
            }else {
                it.getSerializable(FORM_TYPE) as? FormType
            } ?: FormType.New
        }
        repository = InMemoryProductRepository
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFormBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val formType = formType

        binding.pickDateButton.setOnClickListener {
            DatePickerDialog(requireContext(), this, year, month - 1, day).show()
        }
        binding.categoryRadioGroup.check(binding.medicineButton.id)

        with(binding.save) {
            text = when(formType) {
                is FormType.Edit -> "save"
                FormType.New -> "add"
            }
            setOnClickListener {
                val isNameValid: Boolean = binding.nameField.text.toString() != ""
                val isQuantityValid: Boolean = if (binding.quantityField.text.toString() == ""){
                    false
                } else {
                    binding.quantityField.text.toString().toInt() > 0
                }
                val isExpirationDateValid: Boolean = !LocalDate.of(year, month, day).isBefore(LocalDate.now())

                if (isNameValid && isQuantityValid && isExpirationDateValid) {
                    val name: String = binding.nameField.text.toString()
                    val categoryButtonId: Int = binding.categoryRadioGroup.checkedRadioButtonId
                    val category: ProductCategory = when (categoryButtonId) {
                        binding.foodButton.id -> ProductCategory.Food
                        binding.cosmeticButton.id -> ProductCategory.Cosmetics
                        else -> ProductCategory.Medicine
                    }
                    val quantity: Int = binding.quantityField.text.toString().toInt()
                    val expirationDate: LocalDate = LocalDate.of(year, month, day)
                    product = product.copy(
                        name = name,
                        category = category,
                        quantity = quantity,
                        expirationDate = expirationDate
                    )
                    when(formType) {
                        is FormType.Edit -> repository.editProduct(formType.id, product)
                        FormType.New -> repository.addProduct(product)
                    }
                    findNavController().popBackStack()
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("Invalid data!")
                        .setMessage("Valid name: $isNameValid\nValid quantity: $isQuantityValid\nValid expiration date: $isExpirationDateValid")
                        .setPositiveButton("Ok") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }

        when (formType) {
            is FormType.Edit -> {
                product = repository.getProduct(formType.id)
                binding.nameField.setText(product.name)
                val categoryButtonId: Int = when (product.category) {
                    ProductCategory.Food -> binding.foodButton.id
                    ProductCategory.Medicine -> binding.medicineButton.id
                    else -> binding.cosmeticButton.id
                }
                binding.categoryRadioGroup.check(categoryButtonId)
                binding.quantityField.setText(product.quantity.toString())
                this.year = product.expirationDate.year
                this.month = product.expirationDate.monthValue
                this.day = product.expirationDate.dayOfMonth
            }
            FormType.New -> {
                product = Product("pizza", ProductCategory.Food, 8, LocalDate.now())
            }
        }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month + 1
        this.day = dayOfMonth
    }
}