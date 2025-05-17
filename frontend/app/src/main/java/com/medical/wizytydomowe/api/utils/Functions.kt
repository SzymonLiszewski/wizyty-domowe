package com.medical.wizytydomowe.api.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.medical.wizytydomowe.R
import java.text.SimpleDateFormat
import java.util.Locale


fun convertToDateFormat(dateString: String?, inputFormatDate: String, outputFormatDate: String): String? {
    try {
        if (!dateString.isNullOrEmpty()){
            val inputFormat = SimpleDateFormat(inputFormatDate, Locale.getDefault())
            val outputFormat = SimpleDateFormat(outputFormatDate, Locale.getDefault())
            val date = inputFormat.parse(dateString)
            return date?.let { outputFormat.format(it) }
        }
        return null
    } catch (e: Exception) {
        return null
    }
}

fun setAddressAsUnknown(cityTextView: TextView?, postalCodeTextView: TextView?,
                        streetTextView: TextView?){
    cityTextView?.text = "None"
    postalCodeTextView?.text = "None"
    streetTextView?.text = "None"
}

fun splitAddress(address: String?) : List<String>{
    val splitAddress = mutableListOf<String>()
    val parts = address?.split(",".toRegex(), 3)
    if (parts?.size == 3) {
        splitAddress.add(parts[0].trim())
        splitAddress.add(parts[1].trim())
        splitAddress.add("ul. " + parts[2].trim())
    }
    return splitAddress
}

fun setAddress(address: String?, cityTextView: TextView?, postalCodeTextView: TextView?, streetTextView: TextView?){
    if (!address.isNullOrEmpty()){
        val parts = splitAddress(address)
        if (parts.size == 3){
            cityTextView?.text = parts[0]
            postalCodeTextView?.text = parts[1]
            streetTextView?.text = parts[2]
        }
        else setAddressAsUnknown(cityTextView, postalCodeTextView, streetTextView)
    }
    else setAddressAsUnknown(cityTextView, postalCodeTextView, streetTextView)
}

fun isValidDate(dateString: String?): Boolean {
    try {
        if (!dateString.isNullOrEmpty()){
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            format.isLenient = false
            format.parse(dateString)
            return true
        }
        return false
    } catch (e: Exception) {
        return false
    }
}

fun setDateAsUnknown(dateTextView: TextView?, hourTextView : TextView?){
    hourTextView?.text = "None"
    dateTextView?.text = "None"
}

fun setDate(dateTextView: TextView?, hourTextView : TextView?, dateString: String?){
    val parts = dateString?.split("T".toRegex(), 2)
    if (parts?.size == 2) {
        val date = convertToDateFormat(parts[0].trim(), "yyyy-MM-dd", "dd-MM-yyyy")
        val time = parts[1].trim()
        if (!date.isNullOrEmpty()) {
            dateTextView?.text = "${date}"
            hourTextView?.text = "${time}"
        }
        else setDateAsUnknown(dateTextView, hourTextView)
    }
    else setDateAsUnknown(dateTextView, hourTextView)
}

fun showDialog(context: Context, message: String, onConfirm: () -> Unit){
    val dialog = MaterialAlertDialogBuilder(context)
        .setTitle("Uwaga!")
        .setIcon(R.drawable.status_information_svgrepo_com)
        .setMessage(message)
        .setPositiveButton("Tak") { dialog, which ->
            onConfirm()
            dialog.dismiss()
        }
        .setNegativeButton("Nie") { dialog, which ->
            dialog.dismiss()
        }
        .show()
    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(context, android.R.color.black))
    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(context, android.R.color.black))
}