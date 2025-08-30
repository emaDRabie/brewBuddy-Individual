package emad.space.brewbuddy.ui.menu

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import emad.space.brewbuddy.R
import emad.space.brewbuddy.databinding.FragmentMenuBinding
import emad.space.brewbuddy.ui.shared.CoffeeAdapter
import emad.space.domain.models.PricedCoffeeItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private val vm: MenuViewModel by viewModels()
    private val adapter by lazy {
        CoffeeAdapter(
            onClick = { vm.onCoffeeClicked(it) },
            onFav = { showAddToOrderConfirm(it) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenu.adapter = adapter

        binding.chipHot.setOnClickListener { vm.loadHot() }
        binding.chipIced.setOnClickListener { vm.loadIced() }
        binding.etSearch.addTextChangedListener { vm.onSearchQuery(it?.toString().orEmpty()) }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collectLatest { s ->
                binding.progress.visibility = if (s.loading) View.VISIBLE else View.GONE
                adapter.submitList(s.items)
                binding.tvError.visibility = if (s.error != null) View.VISIBLE else View.GONE
                binding.tvError.text = s.error
            }
        }

        vm.navigateToDetail.observe(viewLifecycleOwner) { event ->
            val nav = event.getContentIfNotHandled() ?: return@observe
            findNavController().navigate(
                R.id.action_menuFragment_to_coffeeDetailBottomSheet,
                nav.toBundle()
            )
        }

        vm.loadHot()
    }

    private fun showAddToOrderConfirm(item: PricedCoffeeItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add to orders")
            .setMessage("Add ${item.item.title.orEmpty()} to your orders?")
            .setPositiveButton("Add") { _, _ -> vm.onAddToOrder(item) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}