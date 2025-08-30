package emad.space.brewbuddy.ui.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emad.space.brewbuddy.databinding.ItemCoffeeBinding
import emad.space.domain.models.PricedCoffeeItem

class CoffeeAdapter(
    private val onClick: (PricedCoffeeItem) -> Unit,
    private val onFav: (PricedCoffeeItem) -> Unit
) : ListAdapter<PricedCoffeeItem, CoffeeAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCoffeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding, onClick, onFav)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemCoffeeBinding,
        private val onClick: (PricedCoffeeItem) -> Unit,
        private val onFav: (PricedCoffeeItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PricedCoffeeItem) {
            binding.tvTitle.text = item.item.title.orEmpty()
            binding.tvPrice.text = "Rp ${item.price.toPlainString()}"
            Glide.with(binding.ivImage).load(item.item.image).into(binding.ivImage)
            binding.root.setOnClickListener { onClick(item) }
            binding.btnFav.setOnClickListener { onFav(item) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PricedCoffeeItem>() {
            override fun areItemsTheSame(oldItem: PricedCoffeeItem, newItem: PricedCoffeeItem) =
                oldItem.item.id == newItem.item.id

            override fun areContentsTheSame(oldItem: PricedCoffeeItem, newItem: PricedCoffeeItem) =
                oldItem == newItem
        }
    }
}