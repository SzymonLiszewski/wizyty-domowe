package com.medical.wizytydomowe.api.medicalReports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.R

class MedicalReportAdapter(
    private var medicalReports: List<MedicalReport>,
    private val onMedicalReportDetailsClick: (MedicalReport) -> Unit
) : RecyclerView.Adapter<MedicalReportAdapter.MedicalReportViewHolder>() {

    inner class MedicalReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMedicalReportTime: TextView = itemView.findViewById(R.id.tvMedicalReportTime)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnMedicalReportDetails: Button = itemView.findViewById(R.id.btnMedicalReportDetails)

        fun bind(medicalReport: MedicalReport) {
            tvMedicalReportTime.text = "${medicalReport.date}"
            tvStatus.text = "${medicalReport.status}"

            btnMedicalReportDetails.setOnClickListener {
                onMedicalReportDetailsClick(medicalReport)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medical_report_item, parent, false)
        return MedicalReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicalReportViewHolder, position: Int) {
        holder.bind(medicalReports[position])
    }

    override fun getItemCount(): Int = medicalReports.size

}