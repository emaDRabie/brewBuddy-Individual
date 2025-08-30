package emad.space.brewbuddy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import emad.space.brewbuddy.R
import emad.space.brewbuddy.databinding.FragmentHomeBinding
import emad.space.brewbuddy.ui.shared.CoffeeRecommendationAdapter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val vm: HomeViewModel by viewModels()

    private val recAdapter by lazy {
        CoffeeRecommendationAdapter { vm.onRecommendationClicked(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvRecommendations.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.uiState.collect { state ->

                        val best = state.bestSeller
                        binding.cvBestSeller.isVisible = best != null
                        if (best != null) {
                            binding.tvCoffeeTitle.text = best.item.title.orEmpty()
                            Glide.with(binding.ivBestImage)
                                .load(best.item.image)
                                .placeholder(R.drawable.img_recommended)
                                .into(binding.ivBestImage)
                            binding.cvBestSeller.setOnClickListener { vm.onBestSellerClicked() }
                            binding.tvMoreInfo.setOnClickListener { vm.onBestSellerClicked() }
                        } else {
                            binding.cvBestSeller.setOnClickListener(null)
                            binding.tvMoreInfo.setOnClickListener(null)
                        }

                        recAdapter.submitList(state.recommendations)
                    }
                }
                launch {
                    vm.navigateToDetail.collect { item ->
                        val bundle = Bundle().apply {
                            putInt("coffeeId", item.item.id ?: -1)
                            putString("title", item.item.title)
                            putString("image", item.item.image)
                            putString("description", item.item.description) // NEW
                            putString("category", item.category.name)
                            putString("price", item.price.toPlainString())
                        }
                        findNavController().navigate(
                            R.id.action_homeFragment_to_coffeeDetailBottomSheet,
                            bundle
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