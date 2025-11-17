package com.example.myapplication

import android.Manifest
import com.example.myapplication.MainActivity
import android.util.Log
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.app.SearchManager
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import androidx.cursoradapter.widget.ResourceCursorAdapter
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import androidx.appcompat.widget.SearchView
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.ImageView
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.BottomSheetNavigationBinding
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.common.location.Location
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.LineString
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.plugin.LocationPuck
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions



class SearchSuggestionsAdapter(
    private val context: Context,
    private val activity: AppCompatActivity,
    private val mainActivity: MainActivity,
    private val mapView: MapView,
    private val searchView: SearchView,
    private var lastUserLocation: android.location.Location?,
    private var mapboxNavigation: MapboxNavigation,
    private var polylineAnnotationManager: PolylineAnnotationManager? = null,
    //private var binding : BottomSheetNavigationBinding
) : ResourceCursorAdapter(context, R.layout.search_suggestion_item, null, 0) {

    private var pointAnnotationManager: PointAnnotationManager? = null
    /*
    private var pointAnnotation: PointAnnotation? = null
    private var searchAnnotation: PointAnnotation? = null
    */
    private lateinit var binding : BottomSheetNavigationBinding
    private lateinit var navigationCamera: NavigationCamera
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource
    private val navigationLocationProvider = NavigationLocationProvider()
    private val pixelDensity = Resources.getSystem().displayMetrics.density
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var defaultLocationPuck: LocationPuck
    //private var count: Int = 0

    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    /**
     * The observer gets notified with location updates.
     *
     * Exposes raw updates coming directly from the location services
     * and the updates enhanced by the Navigation SDK (cleaned up and matched to the road).
     */

    private val locationObserver = object : LocationObserver {
        var firstLocationUpdateReceived = false

        override fun onNewRawLocation(rawLocation: Location) {
            // Use raw location only for cycling and walking cases.
            // For vehicles use map matched location.
        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation
            // update location puck's position on the map
            navigationLocationProvider.changePosition(
                location = enhancedLocation,
                keyPoints = locationMatcherResult.keyPoints,
            )

            // update camera position to account for new location
            viewportDataSource.onLocationChanged(enhancedLocation)
            viewportDataSource.evaluate()

            // if this is the first location update the activity has received,
            // it's best to immediately move the camera to the current user location
            if (!firstLocationUpdateReceived) {
                firstLocationUpdateReceived = true
                navigationCamera.requestNavigationCameraToOverview(
                    stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                        .maxDuration(0) // instant transition
                        .build()
                )
            }
        }
    }


    /**
     * The observer gets notified whenever the tracked routes change.
     * Use this observer to draw routes during active guidance or to cleanup when navigation switches to free drive.
     * The observer isn't triggered in free drive.
     */

    private val routesObserver = RoutesObserver { routeUpdateResult ->
        val navigationRoutes = routeUpdateResult.navigationRoutes
        if (navigationRoutes.isNotEmpty()) {
            routeLineApi.setNavigationRoutes(
                navigationRoutes,
                // alternative metadata is available only in active guidance.
                mapboxNavigation.getAlternativeMetadataFor(navigationRoutes)
            ) { value ->
                mapView.mapboxMap.style?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }

            // update the camera position to account for the new route
            viewportDataSource.onRouteChanged(navigationRoutes.first())
            viewportDataSource.evaluate()
        } else {
            // remove route line from the map
            mapView.mapboxMap.style?.let { style ->
                routeLineApi.clearRouteLine { value ->
                    routeLineView.renderClearRouteLineValue(
                        style,
                        value
                    )
                }
            }
            // remove the route reference from camera position evaluations
            viewportDataSource.clearRouteData()
            viewportDataSource.evaluate()
            navigationCamera.requestNavigationCameraToOverview()
        }
    }

    init {
        binding = BottomSheetNavigationBinding.inflate(LayoutInflater.from(context))
        viewportDataSource = MapboxNavigationViewportDataSource(mapView.mapboxMap)
        navigationCamera = NavigationCamera(
            mapView.mapboxMap,
            mapView.camera,
            viewportDataSource
        )
        // set camera paddings
        viewportDataSource.overviewPadding = overviewPadding
        viewportDataSource.followingPadding = followingPadding
        // set the animations lifecycle listener to ensure the NavigationCamera stops
        // automatically following the user location when the map is interacted with
        mapView.camera.addCameraAnimationsLifecycleListener(
            NavigationBasicGesturesHandler(navigationCamera)
        )
        val mapboxRouteLineOptions = MapboxRouteLineViewOptions.Builder(context)
            .routeLineBelowLayerId("road-label")
            .build()
        routeLineApi = MapboxRouteLineApi(MapboxRouteLineApiOptions.Builder().build())
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)


        defaultLocationPuck = mapView.location.locationPuck

        pointAnnotationManager = mainActivity.pointAnnotationManager
        // Check if permissions are granted before starting the trip session
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            // Permissions are granted, start the trip session
            mapboxNavigation.startTripSession()
        } else {
            // Permissions are not granted, request them
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }




    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.search_suggestion_item, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val textView = view.findViewById<TextView>(R.id.suggestion_text)
        val columnIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)

        if (columnIndex >= 0) {
            val suggestion = cursor.getString(columnIndex)
            textView.text = suggestion

            // Add an OnClickListener to the TextView
            textView.setOnClickListener {
                searchView.setQuery(suggestion, true)
            }
        } else {
            textView.text = "No suggestion"
        }
        enableLocationComponent()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query?.isEmpty() == false) {
                    //Log.d("clearRoute", "Clearing the route")
                    // Show all faculty locations on the map
                    (context as? MainActivity).apply {
                        //Toast.makeText(context, "aaaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show()
                        var textFound = false
                        mainActivity.name_listOfLocationPic.keys.forEach { caption ->

                            if (caption.contains(query, ignoreCase = true)) {
                                val latLongPair = mainActivity.name_listOfLocationPic[caption]?.first
                                val point = Point.fromLngLat(latLongPair!!.first, latLongPair.second)

                                if(!textFound) {
                                    mainActivity.hideOpeningMarkers()
                                    textFound = true

                                    this!!.mapView.mapboxMap.setCamera(
                                        CameraOptions.Builder()
                                            .center(point) // Set the camera to a default location
                                            .zoom(17.5) // Set the zoom level to show all faculty locations
                                            .build()
                                    )
                                }

                                addMarkerSearch(point, caption)

                            }
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    mainActivity.hideOpeningMarkers() // Clear the opening marker
                }
                if (newText.isNullOrEmpty()) {

                    //Toast.makeText(context, "bbbbbbbbbbbbbbb", Toast.LENGTH_SHORT).show()

                    mainActivity.clearRoute()
                    mainActivity.blueAnnotations.clear()

                    mainActivity.showOpeningMarkers()
                    activity.supportActionBar?.show()
                }
                return false
            }
        })
       onstart()
    }

    private fun drawRoute(route: DirectionsRoute) {
        val routeGeometry = route.geometry() ?: return
        val routeLineString = LineString.fromPolyline(routeGeometry, PRECISION_6)

        if (polylineAnnotationManager == null) {
            polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()
        }

        // Clear previous route before drawing the new one
        polylineAnnotationManager?.deleteAll()

        // Convert the LineString coordinates to List<Point>
        val routeCoordinates: List<Point> = routeLineString.coordinates()

        val polylineAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(routeCoordinates)
            .withLineColor(Color.parseColor("#3bb2d0"))
            .withLineWidth(10.0)

        // Add the polyline to the map
        polylineAnnotationManager?.create(polylineAnnotationOptions)
    }

    fun clearRoute() {
        Log.d("SearchSuggestionsAdapter", "clearRoute called")
        polylineAnnotationManager?.deleteAll()

    }

    private fun addMarkerSearch(point: Point, caption: String) {
        /*count += 1
        Toast.makeText(context, "count: $count, caption = $caption", Toast.LENGTH_SHORT).show()
        */

        val drawable = context.resources.getDrawable(R.drawable.ic_blue_location, null)
        val bitmap = bitmapFromDrawable(drawable)

        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(bitmap)
            .withIconSize(1.7)
            .withTextField(caption)
            .withTextSize(14.0)
            .withTextColor(Color.BLUE)
            .withTextAnchor(TextAnchor.TOP)
            .withTextOffset(listOf(0.0, 1.0))

        // Create the point annotation
        val blueAnnotation = pointAnnotationManager?.create(pointAnnotationOptions)

        mainActivity.blueAnnotations.add(blueAnnotation!!)

    }

    private fun bitmapFromDrawable(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun runQueryOnBackgroundThread(constraint: CharSequence?): Cursor {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))

        mainActivity.name_listOfLocationPic.keys.forEachIndexed { index, caption ->
            if (constraint != null && caption.contains(constraint, ignoreCase = true)) {
                cursor.addRow(arrayOf(index.toString(), caption))
            }
        }
        return cursor
    }
    private fun enableLocationComponent() {
        val locationComponent = mapView.location
        locationComponent.apply {
            enabled = true
            pulsingEnabled = true
            addOnIndicatorPositionChangedListener { point ->
                // Handle location updates here
                val userLocation = android.location.Location("").apply {
                    latitude = point.latitude()
                    longitude = point.longitude()
                }
                lastUserLocation?.let { lastLocation ->
                    // Check if the new location is significantly different from the last one
                    if (lastLocation.distanceTo(userLocation) > 10) {
                        // Update your user location logic here
                        lastUserLocation = userLocation
                    }
                } ?: run {
                    // If lastUserLocation is null, update it with the new location
                    lastUserLocation = userLocation
                }
            }
        }
    }
    private fun startActiveGuidance(routes: List<NavigationRoute>) {
        //binding.buttonStartActiveGuidance.visibility = View.GONE
        // Set routes to switch navigator from free drive to active guidance state.
        // In active guidance navigator emits your route progress, voice and banner instructions, etc.
        mapboxNavigation.setNavigationRoutes(routes)
        navigationCamera.requestNavigationCameraToFollowing()

        binding.buttonFinishActiveGuidance.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                visibility = View.GONE
                // Set an empty list to finish active guidance and switch back to free drive state.
                mapboxNavigation.setNavigationRoutes(emptyList())
            }
        }
    }
    fun onstart(){
        mapboxNavigation.registerLocationObserver(locationObserver)
        mapboxNavigation.registerRoutesObserver(routesObserver)
    }

    /*
    fun showBottomSheetDialog(context: Context, suggestion: String, location: Point?) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val sheetview = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(sheetview)

        // Set the image and direction text based on the annotation text
        val imageView = sheetview.findViewById<ImageView>(R.id.imageView)
        val directionTextView = sheetview.findViewById<TextView>(R.id.directionTextView)
        val directionButton = sheetview.findViewById<Button>(R.id.directionButton)


        if (imageView != null && directionTextView != null && directionButton != null) {
            when (suggestion) {
                "Faculty of Science and Technology" -> {
                    imageView.setImageResource(R.drawable.building_image)
                    directionTextView.text = "Faculty of Science and Technology"
                }
                // ...
            }

            directionButton.setOnClickListener {
                // Code to handle direction button click
                bottomSheetDialog.dismiss()
                clearRoute()
                lastUserLocation?.let { userLocation ->
                    // Define the origin and destination points
                    val originPoint = Point.fromLngLat(userLocation.longitude, userLocation.latitude)
                    val destinationPoint = location  // The point of the clicked marker

                    // Define the route options for walking (you can change the profile)
                    val routeOptions = RouteOptions.builder()
                        .coordinatesList(listOf(originPoint, destinationPoint))
                        .profile(DirectionsCriteria.PROFILE_WALKING)  // Change to PROFILE_DRIVING or PROFILE_CYCLING if needed
                        .alternatives(false)
                        .steps(true) // Include steps for turn-by-turn navigation
                        .build()

                    // Request route from Mapbox API
                    mapboxNavigation.requestRoutes(
                        routeOptions,
                        object : NavigationRouterCallback {
                            override fun onRoutesReady(
                                routes: List<NavigationRoute>,
                                @RouterOrigin routerOrigin: String
                            ) {
                                // Check if routes are available
                                if (routes.isNotEmpty()) {
                                    // Get the first route
                                    val route = routes.first().directionsRoute

                                    // Draw the route on the map
                                    drawRoute(route)

                                    // Set navigation routes to start navigation (if required)
                                    //mapboxNavigation.setNavigationRoutes(routes)
                                    mapView.mapboxMap.setCamera(
                                        CameraOptions.Builder()
                                            .center(Point.fromLngLat(
                                                (originPoint.longitude() + destinationPoint!!.longitude()) / 2,
                                                (originPoint.latitude() + destinationPoint.latitude()) / 2
                                            ))
                                            .zoom(16.5)
                                            .build()
                                    )
                                    activity.supportActionBar?.hide()

                                    val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_navigation, null)
                                    val newBottomSheetDialog = BottomSheetDialog(context)
                                    newBottomSheetDialog.setContentView(bottomSheetView)


                                    newBottomSheetDialog.window?.setDimAmount(0f)

                                    // Handle Start Navigation button click
                                    bottomSheetView.findViewById<Button>(R.id.buttonStartActiveGuidance).setOnClickListener {
                                        // Start navigation or perform another action here
                                        Toast.makeText(context, "Starting navigation...", Toast.LENGTH_SHORT).show()

                                        // You can initiate turn-by-turn navigation here
                                        // Example: mapboxNavigation.startTripSession()
                                        //mapboxNavigation.startTripSession()

                                        bottomSheetView.findViewById<Button>(R.id.buttonStartActiveGuidance).visibility = View.GONE
                                        bottomSheetView.findViewById<Button>(R.id.buttonFinishActiveGuidance).visibility = View.VISIBLE

                                        startActiveGuidance(routes)
                                        clearRoute()
                                        // initialize the location puck
                                        mapView.location.apply {
                                            this.locationPuck = LocationPuck2D(
                                                bearingImage = ImageHolder.from(
                                                    R.drawable.mapbox_navigation_puck_icon
                                                )
                                            )

                                            setLocationProvider(navigationLocationProvider)

                                            puckBearingEnabled = true
                                            enabled = true
                                        }

                                        // Dismiss the new bottom sheet
                                        //newBottomSheetDialog.dismiss()
                                    }

                                    bottomSheetView.findViewById<Button>(R.id.buttonFinishActiveGuidance).setOnClickListener {
                                        mapboxNavigation.setNavigationRoutes(emptyList())
                                        bottomSheetView.findViewById<Button>(R.id.buttonStartActiveGuidance).visibility = View.VISIBLE
                                        bottomSheetView.findViewById<Button>(R.id.buttonFinishActiveGuidance).visibility = View.VISIBLE
                                        newBottomSheetDialog.dismiss()
                                        clearRoute()
                                        mapView.location.apply {
                                            this.locationPuck = defaultLocationPuck
                                            setLocationProvider(navigationLocationProvider)
                                            puckBearingEnabled = false
                                            enabled = true
                                        }

                                        // Show the app bar
                                        mainActivity.supportActionBar?.show()

                                    }

                                    newBottomSheetDialog.show()

                                    /*
                                    binding.buttonStartActiveGuidance.apply {
                                            startActiveGuidance(routes)
                                        }
                                    }
                                     */

                                } else {
                                    // Handle no routes found
                                    Toast.makeText(
                                        context,
                                        "No routes found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            // Handle failures during route request
                            override fun onFailure(
                                reasons: List<RouterFailure>,
                                routeOptions: RouteOptions
                            ) {
                                val errorMessage =
                                    reasons.firstOrNull()?.message ?: "Route request failed"
                                Toast.makeText(
                                    context,
                                    "Route request failed: $errorMessage",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("RouteError", "Failed to fetch route: $reasons")
                            }

                            // Handle route request cancellations
                            override fun onCanceled(
                                routeOptions: RouteOptions,
                                @RouterOrigin routerOrigin: String
                            ) {
                                Toast.makeText(
                                    context,
                                    "Route request canceled",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.w("RouteError", "Route request was canceled")
                            }
                        }
                    )
                } ?: run {
                    // Handle the case when the user's location is not available
                    Toast.makeText(context, "User  location not available", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Log.e("SearchSuggestionsAdapter", "One or more views are null")
        }
        bottomSheetDialog.show()
    }

     */
}
