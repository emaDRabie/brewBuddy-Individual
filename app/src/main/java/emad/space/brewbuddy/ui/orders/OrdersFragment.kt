package emad.space.brewbuddy.ui.orders

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import emad.space.brewbuddy.R
import emad.space.brewbuddy.databinding.FragmentOrdersBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private val vm: OrdersViewModel by viewModels()
    private val adapter by lazy { OrdersAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter

        binding.toggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipRecently -> vm.setRecent()
                R.id.chipPastOrders -> vm.setPast()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collectLatest { s ->
                adapter.submitList(s.items)
                binding.tvEmpty.visibility = if (s.items.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}