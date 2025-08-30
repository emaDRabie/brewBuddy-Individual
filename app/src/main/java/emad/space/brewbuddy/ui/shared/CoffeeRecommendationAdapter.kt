package emad.space.brewbuddy.ui.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emad.space.brewbuddy.databinding.RecommendedItemBinding
import emad.space.domain.models.PricedCoffeeItem

class CoffeeRecommendationAdapter(
    private val onClick: (PricedCoffeeItem) -> Unit
) : ListAdapter<PricedCoffeeItem, CoffeeRecommendationAdapter.VH>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            RecommendedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(
        private val binding: RecommendedItemBinding,
        val onClick: (PricedCoffeeItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PricedCoffeeItem) {
            binding.coffeeTitle.text = item.item.title.orEmpty()
            binding.coffeePrice.text = "Rp ${item.price.toPlainString()}"
            Glide.with(binding.recommendedCoffeeImage)
                .load(item.item.image)
                .placeholder(emad.space.brewbuddy.R.drawable.img_recommended)
                .into(binding.recommendedCoffeeImage)
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<PricedCoffeeItem>() {
            override fun areItemsTheSame(a: PricedCoffeeItem, b: PricedCoffeeItem) =
                a.item.id == b.item.id

            override fun areContentsTheSame(a: PricedCoffeeItem, b: PricedCoffeeItem) = a == b
        }
    }
}