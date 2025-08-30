package emad.space.brewbuddy.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emad.space.brewbuddy.R
import emad.space.brewbuddy.databinding.ItemOrderBinding
import emad.space.domain.models.Order
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter : ListAdapter<Order, OrdersAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        private val df = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

        fun bind(order: Order) {
            // Image: show the first item's image if available
            val firstImage = order.items.firstOrNull()?.coffee?.item?.image
            if (!firstImage.isNullOrBlank()) {
                Glide.with(binding.ivOrderImage)
                    .load(firstImage)
                    .placeholder(R.drawable.img_recommended)
                    .into(binding.ivOrderImage)
            } else {
                binding.ivOrderImage.setImageResource(R.drawable.img_recommended)
            }

            binding.tvItems.text =
                order.items.joinToString { "x${it.quantity} ${it.coffee.item.title.orEmpty()}" }

            // Details line: date • total
            val placedAt = df.format(Date(order.placedAtEpochMillis))
            binding.tvDetails.text = "$placedAt • Rp ${order.total.toPlainString()}"

        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Order, newItem: Order) = oldItem == newItem
        }
    }
}