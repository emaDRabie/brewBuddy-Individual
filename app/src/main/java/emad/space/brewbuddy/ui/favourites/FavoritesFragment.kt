package emad.space.brewbuddy.ui.favourites

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import emad.space.brewbuddy.R
import emad.space.brewbuddy.databinding.FragmentFavoritesBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val vm: FavoritesViewModel by viewModels()

    private val adapter by lazy {
        FavoritesAdapter(
            onItemClick = { item -> vm.onCoffeeClicked(item) },
            onToggleFavorite = { item -> vm.onToggleFavorite(item) } // toggle favorite
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2) // grid
        binding.rvFavorites.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.state.collectLatest { s ->
                        adapter.submitList(s.items)
                        binding.tvEmpty.visibility = if (s.items.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    vm.navigateToDetail.collect { nav ->
                        findNavController().navigate(
                            R.id.action_favoritesFragment_to_coffeeDetailBottomSheet,
                            nav.toBundle()
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}