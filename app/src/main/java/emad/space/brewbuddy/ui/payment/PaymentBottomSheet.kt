package emad.space.brewbuddy.ui.payment

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import emad.space.brewbuddy.R
import java.math.BigDecimal
import java.math.RoundingMode
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentBottomSheet : BottomSheetDialogFragment() {

    private val prefsName = "payment_prefs"
    private val keyAddress = "address"

    private val vm: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog
        )
        isCancelable = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(requireContext(), theme).apply {
            setOnShowListener { dlg ->
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
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_payment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Address
        val tvAddress = view.findViewById<TextView>(R.id.tv_no_address)
        val btnEdit = view.findViewById<TextView>(R.id.edit_address)

        // Order views
        val tvOrderItem = view.findViewById<TextView>(R.id.tv_order_item)
        val tvOrderItemPrice = view.findViewById<TextView>(R.id.tv_order_item_price)
        val tvSubTotal = view.findViewById<TextView>(R.id.tv_sub_total_price)
        val tvDelivery = view.findViewById<TextView>(R.id.tv_delivery_fee_price)
        val tvPackaging = view.findViewById<TextView>(R.id.tv_packaging_fee_price)
        val tvPromo = view.findViewById<TextView>(R.id.tv_promo_price)
        val tvTotal = view.findViewById<TextView>(R.id.tv_total_price)

        // Buttons
        val btnCancel = view.findViewById<View>(R.id.btn_cancel)
        val btnPlaceOrder = view.findViewById<View>(R.id.btn_place_order)

        // Persisted address
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val savedAddress = prefs.getString(keyAddress, null)
        tvAddress.text = if (savedAddress.isNullOrBlank()) "No saved address" else savedAddress

        // Read order args
        val title = arguments?.getString("orderTitle").orEmpty()
        val qty = arguments?.getInt("orderQty") ?: 1
        val unitPrice = arguments?.getString("orderPrice")?.toBigDecimalOrNull() ?: BigDecimal.ZERO

        tvOrderItem.text = "${qty}x $title"
        tvOrderItemPrice.text = formatPrice(unitPrice) // unit price

        // Fees
        val delivery = BigDecimal("3")
        val packaging = BigDecimal("5")
        val promo = BigDecimal.ZERO

        val subTotal = unitPrice * BigDecimal(qty)
        val total = subTotal + delivery + packaging - promo

        tvSubTotal.text = formatPrice(subTotal)
        tvDelivery.text = formatPrice(delivery)
        tvPackaging.text = formatPrice(packaging)
        tvPromo.text = formatPrice(promo)
        tvTotal.text = formatPrice(total)

        // Edit address dialog with custom background
        btnEdit.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_edit_address, null)
            val input = dialogView.findViewById<EditText>(R.id.et_address).apply {
                inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                setText(prefs.getString(keyAddress, ""))
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Enter your address")
                .setView(dialogView) // has rounded background
                .setPositiveButton("Save") { _, _ ->
                    val text = input.text?.toString()?.trim().orEmpty()
                    prefs.edit().putString(keyAddress, text).apply()
                    tvAddress.text = if (text.isBlank()) "No saved address" else text
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnCancel.setOnClickListener { dismiss() }
        btnPlaceOrder.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                vm.placeOrderFromArgs(requireArguments())
                Toast.makeText(requireContext(), "Ordered Successfully", Toast.LENGTH_SHORT).show();
                dismiss()
            }
        }
    }

    private fun formatPrice(amount: BigDecimal): String {
        // Force two decimals with a dot to match other app displays (toPlainString usage)
        return "Rp ${amount.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
    }
}