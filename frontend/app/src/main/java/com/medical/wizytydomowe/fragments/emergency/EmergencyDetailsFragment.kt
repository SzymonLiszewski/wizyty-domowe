package com.medical.wizytydomowe.fragments.emergency

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointmentApi.AppointmentRetrofitInstance
import com.medical.wizytydomowe.api.emergency.EmergencyChangeStatusRequest
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.utils.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale

class EmergencyDetailsFragment : Fragment(R.layout.emergency_details_fragment), OnMapReadyCallback {

    private var emergency: Emergency? = null
    private var addNewEmergencyFlag: Boolean? = null
    private lateinit var preferenceManager: PreferenceManager

    private lateinit var finishEmergencyView: MaterialCardView
    private lateinit var takeOnAEmergencyView: MaterialCardView
    private lateinit var sendEmergencyView: MaterialCardView
    private lateinit var patientView: MaterialCardView
    private lateinit var paramedicView: MaterialCardView
    private lateinit var noParamedicView: MaterialCardView

    //Google maps
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emergency = arguments?.getSerializable("emergency") as? Emergency
        addNewEmergencyFlag = arguments?.getSerializable("addNewEmergencyFlag") as? Boolean

        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()

        finishEmergencyView = view.findViewById(R.id.finishEmergencyView)
        takeOnAEmergencyView = view.findViewById(R.id.takeOnAEmergencyView)
        sendEmergencyView = view.findViewById(R.id.sendEmergencyView)
        patientView = view.findViewById(R.id.patientView)
        paramedicView = view.findViewById(R.id.paramedicView)
        noParamedicView = view.findViewById(R.id.noParamedicView)

        finishEmergencyView.setOnClickListener {
            showFinishEmergencyDialog()
        }

        takeOnAEmergencyView.setOnClickListener {
            showTakeOnEmergencyDialog()
        }

        sendEmergencyView.setOnClickListener {
            showAddNewEmergencyDialog()
        }

        // Inicjalizacja Google Maps
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val mapTouchOverlay = view.findViewById<View>(R.id.map_touch_overlay)
        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.nested_view)

        mapTouchOverlay.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> nestedScrollView.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> nestedScrollView.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        if (userRole == "Paramedic") setParamedicLayout()
        else setPatientLayout()
    }

    private fun setMainView(){
        setDate()
        setAddressData()
        setDescription()
        setStatus()
    }

    private fun setParamedicView(){
        setParamedicData()
    }

    private fun setPatientView(){
        setPatientData()
    }

    private fun setPatientLayout(){
        if (addNewEmergencyFlag == true){
            setPatientView()
            patientView.visibility = View.VISIBLE
            paramedicView.visibility = View.GONE
            noParamedicView.visibility = View.GONE
        }
        else {
            patientView.visibility = View.GONE
            if (emergency?.paramedic == null){
                noParamedicView.visibility = View.VISIBLE
                paramedicView.visibility = View.GONE
            }
            else{
                setParamedicView()
                noParamedicView.visibility = View.GONE
                paramedicView.visibility = View.VISIBLE
            }

        }
        setMainView()
    }

    private fun setParamedicLayout(){
        patientView.visibility = View.VISIBLE
        paramedicView.visibility = View.GONE
        noParamedicView.visibility = View.GONE

        setPatientView()
        setMainView()
    }

    private fun setPatientData(){
        view?.findViewById<TextView>(R.id.firstNamePatientTextView)?.text = "${emergency?.patient?.firstName}"
        view?.findViewById<TextView>(R.id.lastNamePatientTextView)?.text = "${emergency?.patient?.lastName}"
        val phoneNumberConverted = "${emergency?.patient?.phoneNumber?.substring(0,3)}-${emergency?.patient?.phoneNumber?.substring(3,6)}-${emergency?.patient?.phoneNumber?.substring(6)}"
        view?.findViewById<TextView>(R.id.phoneNumberPatientTextView)?.text = "${phoneNumberConverted}"
        view?.findViewById<TextView>(R.id.emailPatientTextView)?.text = "${emergency?.patient?.email}"
    }

    private fun setParamedicData(){
        view?.findViewById<TextView>(R.id.firstNameParamedicTextView)?.text = "${emergency?.paramedic?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameParamedicTextView)?.text = "${emergency?.paramedic?.lastName}"
        view?.findViewById<TextView>(R.id.specializationParamedicTextView)?.text = "Ratownik medyczny"
        view?.findViewById<TextView>(R.id.hospitalParamedicTextView)?.text = "${emergency?.paramedic?.workPlace?.name}"
    }

    private fun setDate(){
        val startDateTextView: TextView? = view?.findViewById(R.id.startDateTextView)
        val startHourTextView : TextView? = view?.findViewById(R.id.startHourTextView)

        setDate(startDateTextView, startHourTextView, emergency?.emergencyReportTime)
    }


    //TUTAJ MAPKA
    private fun setAddressData(){
//        val cityEmergencyTextView =  view?.findViewById<TextView>(R.id.cityEmergencyTextView)
//        val postalCodeEmergencyTextView =  view?.findViewById<TextView>(R.id.postalCodeEmergencyTextView)
//        val streetEmergencyTextView =  view?.findViewById<TextView>(R.id.streetEmergencyTextView)

        setAddress(emergency?.address, null, null, null)
    }

    private fun setDescription(){
        view?.findViewById<TextView>(R.id.descriptionTextView)?.text = "${emergency?.description}"
    }

    private fun setStatus(){
        if (emergency?.status == "Available" && preferenceManager.getRole() == "Paramedic"){
            takeOnAEmergencyView.visibility = View.VISIBLE
        }
        else takeOnAEmergencyView.visibility = View.GONE

        if (emergency?.status == "In_progress" && preferenceManager.getRole() == "Paramedic") {
            finishEmergencyView.visibility = View.VISIBLE
        }
        else finishEmergencyView.visibility = View.GONE

        if (emergency?.status == "Available" && preferenceManager.getRole() != "Paramedic"
            && addNewEmergencyFlag == true){
            sendEmergencyView.visibility = View.VISIBLE
        }
        else sendEmergencyView.visibility = View.GONE
    }

    private fun showTakeOnEmergencyDialog(){
        showDialog(requireContext(),"Czy na pewno chcesz się podjąć zgłoszenia?"){
            sendTakeOnEmergencyRequest()
        }
    }

    private fun finishEmergency(){
        sendFinishEmergencyRequest()
    }

    private fun showFinishEmergencyDialog(){
        showDialog(requireContext(),"Czy na pewno zgłoszenie można uznać za zakończone?"){
            finishEmergency()
        }
    }

    private fun showAddNewEmergencyDialog(){
        showDialog(requireContext(),"Czy na pewno chcesz dodać te zgłoszenie?"){
            sendNewEmergency()
        }
    }

    private fun navigateToEmergencyFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyFragment())
    }

    private fun sendNewEmergency(){
        val token = "Bearer " + preferenceManager.getAuthToken()
        val emergencyRequest = Emergency(emergency?.id, emergency?.patient, emergency?.paramedic,
            emergency?.status, emergency?.emergencyReportTime, emergency?.address, emergency?.description)

        AppointmentRetrofitInstance.appointmentApiService.addNewEmergency(token,
            emergencyRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Zgłoszenie zostało dodane pomyślnie.", Toast.LENGTH_LONG).show()
                    navigateToEmergencyFragment()
                }
                else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Podczas tworzenia zgłoszenia wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendTakeOnEmergencyRequest(){
        val token = "Bearer " + preferenceManager.getAuthToken()
        AppointmentRetrofitInstance.appointmentApiService.assignEmergency(token,
            emergency?.id.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Zgłoszenie zostało przypisane pomyślnie.", Toast.LENGTH_LONG).show()
                    navigateToEmergencyFragment()
                }
                else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Podczas przypisywania zgłoszenia wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendFinishEmergencyRequest(){
        val token = "Bearer " + preferenceManager.getAuthToken()
        val emergencyChangeStatusRequest = EmergencyChangeStatusRequest("Completed")

        AppointmentRetrofitInstance.appointmentApiService.finishEmergency(token,
            emergency?.id.toString(), emergencyChangeStatusRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Zgłoszenie zostało zakończone pomyślnie.", Toast.LENGTH_LONG).show()
                    navigateToEmergencyFragment()
                }
                else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Podczas zakończenia zgłoszenia wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLocation = LatLng(location.latitude, location.longitude)
                map.addMarker(
                    MarkerOptions()
                        .position(userLocation)
                        .title("Twoja lokalizacja")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )

                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                emergency?.address?.let { addressString ->
                    try {
                        val addressList = geocoder.getFromLocationName(addressString, 1)
                        if (addressList != null && addressList.isNotEmpty()) {
                            val address = addressList[0]
                            val emergencyLocation = LatLng(address.latitude, address.longitude)

                            map.addMarker(
                                MarkerOptions()
                                    .position(emergencyLocation)
                                    .title("Lokalizacja zgłoszenia")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            )

                            // Dodanie linii łączącej oba punkty
                            val polylineOptions = PolylineOptions()
                                .add(userLocation)
                                .add(emergencyLocation)
                                .width(8f)
                                .color(Color.GREEN)
                                .geodesic(true)
                            map.addPolyline(polylineOptions)

                            // Ustawienie kamery by objąć oba punkty
                            val boundsBuilder = LatLngBounds.Builder()
                            boundsBuilder.include(userLocation)
                            boundsBuilder.include(emergencyLocation)
                            val bounds = boundsBuilder.build()

                            val padding = 100 // margines w pikselach od krawędzi ekranu
                            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                            map.moveCamera(cameraUpdate)

                        } else {
                            Toast.makeText(requireContext(), "Nie znaleziono lokalizacji: $addressString", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(requireContext(), "Błąd geokodowania: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Nie udało się pobrać lokalizacji użytkownika", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Jeśli pozwolenie zostało udzielone, ponownie załaduj mapę
                val mapFragment = childFragmentManager
                    .findFragmentById(R.id.id_map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }
}