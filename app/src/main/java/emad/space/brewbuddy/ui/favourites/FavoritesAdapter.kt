package emad.space.brewbuddy.ui.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emad.space.brewbuddy.databinding.ItemFavoriteBinding
import emad.space.domain.models.PricedCoffeeItem

class FavoritesAdapter(
    private val onItemClick: (PricedCoffeeItem) -> Unit,
    private val onToggleFavorite: (PricedCoffeeItem) -> Unit
) : ListAdapter<PricedCoffeeItem, FavoritesAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding, onItemClick, onToggleFavorite)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(
        private val binding: ItemFavoriteBinding,
        private val onItemClick: (PricedCoffeeItem) -> Unit,
        private val onToggleFavorite: (PricedCoffeeItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PricedCoffeeItem) {
            binding.coffeeTitle.text = item.item.title.orEmpty()
            binding.coffeePrice.text = "Rp ${item.price.toPlainString()}"
            Glide.with(binding.recommendedCoffeeImage)
                .load(item.item.image)
                .into(binding.recommendedCoffeeImage)

            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnFav.setOnClickListener { onToggleFavorite(item) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PricedCoffeeItem>() {
            override fun areItemsTheSame(a: PricedCoffeeItem, b: PricedCoffeeItem) =
                a.item.id == b.item.id
            override fun areContentsTheSame(a: PricedCoffeeItem, b: PricedCoffeeItem) = a == b
        }
    }
}