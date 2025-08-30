package emad.space.brewbuddy.ui.detail


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import emad.space.brewbuddy.databinding.FragmentCoffeeDetailBinding

@AndroidEntryPoint
class CoffeeDetailFragment : Fragment() {

    private var _binding: FragmentCoffeeDetailBinding? = null
    private val binding get() = _binding!!
    private val vm: CoffeeDetailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCoffeeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.bindArgs(requireArguments())
        binding.tvTitle.text = vm.title
        binding.tvPrice.text = "Rp ${vm.price}"
        binding.btnMinus.setOnClickListener { vm.decrement() ; binding.tvQty.text = vm.quantity.toString() ; updateTotal() }
        binding.btnPlus.setOnClickListener { vm.increment() ; binding.tvQty.text = vm.quantity.toString() ; updateTotal() }
        binding.btnBuyNow.setOnClickListener {
            vm.placeOrder()
        }
        binding.tvQty.text = vm.quantity.toString()
        updateTotal()
    }

    private fun updateTotal() {
        binding.tvTotal.text = "Rp ${vm.total()}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}