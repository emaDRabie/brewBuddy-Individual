package emad.space.brewbuddy.ui.detail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import emad.space.brewbuddy.R
import emad.space.brewbuddy.databinding.FragmentCoffeeDetailBinding
import emad.space.brewbuddy.ui.payment.PaymentBottomSheet
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoffeeDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentCoffeeDetailBinding? = null
    private val binding get() = _binding!!
    private val vm: CoffeeDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog
        )
        isCancelable = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dlg ->
            val d = dlg as BottomSheetDialog
            val bottomSheet =
                d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.setBackgroundResource(R.drawable.bottom_sheet_shape)
                it.clipToOutline = true
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoffeeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.bindArgs(requireArguments())

        Glide.with(binding.ivCoffee)
            .load(vm.image)
            .placeholder(R.drawable.img_recommended)
            .into(binding.ivCoffee)

        binding.tvTitle.text = vm.title
        binding.tvPrice.text = "Rp ${vm.price}"

        val desc = vm.description.orEmpty()
        binding.tvDetails.text = desc
        binding.tvDetails.visibility = if (desc.isBlank()) View.GONE else View.VISIBLE

        binding.tvQty.text = vm.quantity.toString()
        updateTotal()

        binding.btnMinus.setOnClickListener {
            vm.decrement()
            binding.tvQty.text = vm.quantity.toString()
            updateTotal()
        }
        binding.btnPlus.setOnClickListener {
            vm.increment()
            binding.tvQty.text = vm.quantity.toString()
            updateTotal()
        }

        // Buy now -> open PaymentBottomSheet with full context for placing order
        binding.btnBuyNow.setOnClickListener {
            val sheet = PaymentBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("orderTitle", vm.title)
                    putInt("orderQty", vm.quantity)
                    putString("orderPrice", vm.price.toPlainString())
                    putInt("coffeeId", vm.coffeeId)
                    putString("orderCategory", vm.category.name)
                    putString("orderImage", vm.image)
                    putString("orderDescription", vm.description)
                }
            }
            sheet.show(parentFragmentManager, "PaymentBottomSheet")
            dismiss()
        }

        binding.btnExit.setOnClickListener { dismiss() }

        // Favorite toggle reflects state via isSelected, plus optional "Saved" dialog
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.isFavorite.collect { fav ->
                    binding.btnFavorite.isSelected = fav
                }
            }
        }
        binding.btnFavorite.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val before = vm.isFavorite.value
                vm.toggleFavorite()
                val after = vm.isFavorite.value
                if (!before && after) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Saved to favorites")
                        .setPositiveButton("Done", null)
                        .show()
                }
            }
        }
    }

    private fun updateTotal() {
        binding.tvTotal.text = "Rp ${vm.total()}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}