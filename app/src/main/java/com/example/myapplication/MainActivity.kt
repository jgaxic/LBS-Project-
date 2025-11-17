package com.example.myapplication

//import android.content.Context
//import android.graphics.drawable.ColorDrawable
//import android.view.KeyEvent
/*import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.route.RouteAlternativesOptions
import kotlin.time.Duration.Companion.seconds
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation*/
//import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource

//import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.myapplication.databinding.BottomSheetNavigationBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.common.location.Location
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.plugin.LocationPuck
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
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


class MainActivity : AppCompatActivity(), PermissionsListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var defaultLocationPuck: LocationPuck
    public lateinit var mapView: MapView
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var annotationPlugin: AnnotationPlugin
    private lateinit var mapboxMap: MapboxMap
    public var pointAnnotationManager: PointAnnotationManager? = null
    public var pointAnnotation: PointAnnotation? = null
    public var searchAnnotation: PointAnnotation? = null
    private var lastUserLocation: android.location.Location? = null
    public var openingAnnotations: MutableList<Pair<String, PointAnnotation>> = mutableListOf()
    public var newAnnotations: MutableList<PointAnnotation> = mutableListOf()
    public var blueAnnotations: MutableList<PointAnnotation> = mutableListOf()
    public var greenAnnotations_PointAnnotation_Distance: MutableList<Pair<PointAnnotation, Double>> = mutableListOf()
    public var isMarkerRed = mutableMapOf<Point, Boolean>()
    private var lastClickedAnnotation: PointAnnotation? = null
    private var lastClickedAnnotation_drawableResId: Int? = 0
    private var lastClickedAnnotation_iconSize: Double? = 0.0
    private var lastClickedAnnotation_color: Int? = 0

    private lateinit var mapboxNavigation: MapboxNavigation
    // private lateinit var context : Context
    //private var polylineAnnotation: PolylineAnnotation? = null
    private var polylineAnnotationManager: PolylineAnnotationManager? = null
    private lateinit var searchView: SearchView
    private lateinit var searchSuggestionsAdapter: SearchSuggestionsAdapter
    private var currentRoutes: List<NavigationRoute>? = null
    //private lateinit var navigationLocationProvider = NavigationLocationProvider()
    private lateinit var binding: BottomSheetNavigationBinding
    private lateinit var navigationCamera: NavigationCamera
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource
    //private lateinit var navigationOptions: NavigationOptions
    private var onBackPressedCallback: OnBackPressedCallback? = null
    private lateinit var btnNearestPlace: Button
    private var routeAdded: Boolean = false



    private val navigationLocationProvider = NavigationLocationProvider()
    private val pixelDensity = Resources.getSystem().displayMetrics.density


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

    val name_listOfLocationPic = mapOf(
        "อาคารเฉลิมพระเกียรติ 80 พรรษา บรมราชินีนาถ (ตึกคณะวิทยาศาสตร์และเทคโนโลยี)" to ((100.537716 to 13.712810) to R.drawable.prefix_01),
        "อาคาร 48 (ตึกคณะวิศวกรรมศาสตร์)" to ((100.535066 to 13.713842) to R.drawable.prefix_02),
        "อาคาร 50 (ตึกคณะบริหารธุรกิจ)" to ((100.535764 to 13.712682) to R.drawable.prefix_03),
        "อาคาร 2/1 และ 2/2 (ตึกคณะศิลปศาสตร์)" to ((100.539151 to 13.713961) to R.drawable.prefix_04),
        "อาคาร 50 (คณะนานาชาติ ชั้น 9)" to ((100.535599 to 13.712715) to R.drawable.prefix_05),
        "อาคาร 14/1 (ตึกคณะครุศาสตร์อุตสาหกรรม)" to ((100.535476 to 13.712962) to R.drawable.prefix_06),
        "อาคาร 51 (ตึกคณะอุตสาหกรรมสิ่งทอ)" to ((100.540072 to 13.713107) to R.drawable.prefix_07),
        "อาคาร 4/1 (ตึกคณะเทคโนโลยีคหกรรมศาสตร์)" to ((100.539409 to 13.713499) to R.drawable.prefix_08),
        "ห้องพยาบาล" to ((100.536498 to 13.711825) to R.drawable.prefix_37),
        "UTK Shop" to ((100.536321 to 13.712055) to R.drawable.prefix_20),
        "อาคารปฎิบัติการเทคโนโลยีเชิงสร้างสรรค์" to ((100.536024 to 13.712032) to R.drawable.prefix_22),
        "กองพัฒนานักศึกษา" to ((100.536383 to 13.711734) to R.drawable.prefix_23),
        "Mini Big C" to ((100.539841 to 13.713699) to R.drawable.prefix_29),
        "อาคารเฉลิมพระเกียรติ 80 พรรษา" to ((100.538573 to 13.713426) to R.drawable.prefix_24),
        "อาคารสํานักงานอธิการบดี" to ((100.538509 to 13.714336) to R.drawable.prefix_21),
        "อาคาร 36" to (( 100.537723 to 13.713900) to R.drawable.prefix_40),
        "โรงอาหาร อาคารเฉลิมพระเกียรติ 80 พรรษา" to ((100.538690 to 13.713150) to R.drawable.prefix_31),
        "7-11" to ((100.535518 to 13.713287) to R.drawable.prefix_32),
        "Cafe Amazon" to ((100.538130 to 13.714550) to R.drawable.prefix_33),
        "อาคารสิรินธร" to (( 100.535864 to 13.713773) to R.drawable.prefix_19),
      )





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
                mapboxMap.style?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }

            // update the camera position to account for the new route
            viewportDataSource.onRouteChanged(navigationRoutes.first())
            viewportDataSource.evaluate()
        } else {
            // remove route line from the map
            mapboxMap.style?.let { style ->
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



    companion object {
        /*private const val LATITUDE = 13.714
        private const val LONGITUDE = 100.537*/
        private const val DISTANCE_THRESHOLD = 150.0
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
       /* private const val SCITECH_LONG = 100.537716
        private const val SCITECH_LAT = 13.712810
        private const val ENGINE_LAT = 13.7142
        private const val ENGINE_LONG = 100.536*/
    }

    /**
     * Generates updates for the [routeLineView] with the geometries and properties of the routes that should be drawn on the map.
     */
    private lateinit var routeLineApi: MapboxRouteLineApi

    /**
     * Draws route lines on the map based on the data from the [routeLineApi]
     */
    private lateinit var routeLineView: MapboxRouteLineView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BottomSheetNavigationBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)


        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        searchView = findViewById(R.id.search_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        mapView = findViewById(R.id.mapView)
        defaultLocationPuck = mapView.location.locationPuck

        permissionsManager = PermissionsManager(this)

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeMap()
        } else {
            permissionsManager.requestLocationPermissions(this)
        }

        mapboxNavigation = if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.retrieve()
        } else {
            MapboxNavigationProvider.create(
                NavigationOptions.Builder(this.applicationContext)
                    .build()
            )
        }
        searchSuggestionsAdapter = SearchSuggestionsAdapter(this,this,this,mapView, searchView, lastUserLocation, mapboxNavigation, polylineAnnotationManager)

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
        // initialize the location puck
        /*mapView.location.apply {
            this.locationPuck = LocationPuck2D(
                bearingImage = ImageHolder.Companion.from(
                    R.drawable.mapbox_navigation_puck_icon
                )
            )
            setLocationProvider(navigationLocationProvider)

            puckBearingEnabled = true
            enabled = true
        }*/

        // initialize route line, the routeLineBelowLayerId is specified to place
        // the route line below road labels layer on the map
        // the value of this option will depend on the style that you are using
        // and under which layer the route line should be placed on the map layers stack
        val mapboxRouteLineOptions = MapboxRouteLineViewOptions.Builder(this)
            .routeLineBelowLayerId("road-label")
            .build()
        routeLineApi = MapboxRouteLineApi(MapboxRouteLineApiOptions.Builder().build())
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        // We recommend starting a trip session for routes preview to get, display,
        // and use for route request a map matched location.
        // See [PreviewActivity#locationObserver].
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mapboxNavigation.startTripSession()
        btnNearestPlace = findViewById(R.id.btn_nearest_place)
        btnNearestPlace.setOnClickListener {
            showNearestPlaces()
        }

    }

    private fun showNearestPlaces() {
        if(greenAnnotations_PointAnnotation_Distance.isEmpty()){
            Toast.makeText(this, "No place within $DISTANCE_THRESHOLD meters", Toast.LENGTH_SHORT).show()
        }else {
            ListNameClass(this, this).show()
        }
    }


    /*
    private fun NearestPlace() {
        val userLocation = lastUserLocation
        if (userLocation != null) {
            val nearestPlace = findNearestPlace(userLocation)
            if (nearestPlace != null) {
                // Show the nearest place on the map with a red icon
                updateAnnotation(nearestPlace, R.drawable.ic_green_location, 1.5, Color.RED)
                isMarkerRed[nearestPlace.point] = true

                // Show the list of nearest places
                showNearestPlacesList(nearestPlace)
            } else {
                Toast.makeText(this, "No nearest place found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User  location not available", Toast.LENGTH_SHORT).show()
        }
    }
    */

    /*
    private fun findNearestPlace(userLocation: android.location.Location): PointAnnotation? {
        var nearestPlace: PointAnnotation? = null
        var minDistance = Double.MAX_VALUE


        for (annotation in pointAnnotationManager!!.annotations) {
        //for (annotation in openingAnnotations) { // Revised at: 202404111045 (Avoid using openingAnnotations)
            val markerLocation = android.location.Location("").apply {
                latitude = annotation.point.latitude()
                longitude = annotation.point.longitude()
            }

            val distance = userLocation.distanceTo(markerLocation).toDouble()

            if (distance < 150 && distance < minDistance) {
                minDistance = distance
                nearestPlace = annotation
            }
        }

        return nearestPlace
    }
    */

    /*
    private fun showNearestPlacesList(nearestPlace: PointAnnotation) {
        val nearestPlaces = mutableListOf<PointAnnotation>()
        for (annotation in pointAnnotationManager!!.annotations) {
        //for (annotation in openingAnnotations) { // Revised at: 202404111045 (Avoid using openingAnnotations)
            val markerLocation = android.location.Location("").apply {
                latitude = annotation.point.latitude()
                longitude = annotation.point.longitude()
            }

            val distance = lastUserLocation!!.distanceTo(markerLocation)
            if (distance < 150) {
                nearestPlaces.add(annotation)
            }
        }

        val nearestPlacesList = nearestPlaces.sortedBy { annotation ->
            val markerLocation = android.location.Location("").apply {
                latitude = annotation.point.latitude()
                longitude = annotation.point.longitude()
            }

            lastUserLocation!!.distanceTo(markerLocation)
        }

        val nearestPlacesText = nearestPlacesList.joinToString("\n") { annotation ->
            annotation.textField ?: ""
        }

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Nearest Places")
        alertDialog.setMessage(nearestPlacesText)
        alertDialog.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
    */

    private fun initializeMap() {
        val customStyleUrl = "mapbox://styles/jgaxic08/cm6iqieg700if01r52st1709u"
        mapView.mapboxMap.loadStyle(customStyleUrl) {
            mapboxMap = mapView.mapboxMap
            annotationPlugin = mapView.annotations
            val annotationConfig = AnnotationConfig()
            pointAnnotationManager = mapView.annotations.createPointAnnotationManager(annotationConfig)
            setCameraPosition()
            enableLocationComponent()
            createOpeningMarkers()
            clickMarker()
            monitorProximityToUserLocation()
            clearRoute()

            mapboxMap.subscribeCameraChanged {
                val cameraPosition = mapboxMap.cameraState
                updateAnnotationsVisibility(cameraPosition.zoom)
            }
        }
    }




    private fun updateAnnotationsVisibility(zoom: Double) {
        if(blueAnnotations.isNotEmpty() || searchView.isShown){
            return
        }

        if (zoom < 17.5) {
            // Hide annotations if zoom level is greater than 15
            hideOpeningMarkers()
        } else {
            // Show annotations if zoom level is less than or equal to 15
            showOpeningMarkers()
        }
    }


    private fun setCameraPosition() {
        lastUserLocation?.let { userLocation ->
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(userLocation.longitude, userLocation.latitude))
                    .zoom(17.5)
                    .build()
            )
        } /*?: run {
            // If the user's location is not available, use the default location
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(LONGITUDE, LATITUDE))
                    .zoom(17.5)
                    .build()
            )
        }*/
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
                        setCameraPosition()
                    }
                }?: run {
                    // If lastUserLocation is null, update it with the new location
                    lastUserLocation = userLocation
                    setCameraPosition()
                }
            }
        }
    }

    private fun openingmarker(point: Point, text: String, iconResId: Int) {
        val context = this // Replace with the activity context

        val drawable = ResourcesCompat.getDrawable(context.resources, iconResId, null)
        if (drawable != null){
            val bitmap = bitmapFromDrawable(drawable)
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(bitmap)
                .withIconSize(1.5)
                .withTextField(text)
                .withTextSize(14.0)
                .withTextColor(Color.GRAY)
                .withTextAnchor(TextAnchor.TOP)
                .withTextOffset(listOf(0.0, 1.0))

            pointAnnotationManager?.let { manager ->
                val annotation = manager.create(pointAnnotationOptions)

                // Revised at: 202411041053 (Avoid using openingAnnotations)
                /*
                openingAnnotations.add(annotation)
                isMarkerRed[annotation.point] = false
                */
            }
        }
    }

    fun clearOpeningMarker() {
        for (annotation in pointAnnotationManager!!.annotations) {
        //for (annotation in openingAnnotations) { // Revised at: 202404111045 (Avoid using openingAnnotations)
            pointAnnotationManager?.delete(annotation)
        }
    }

    fun clearAllMarkersExceptRed() {
        for (annotation in pointAnnotationManager!!.annotations) {
            if(annotation.textColorInt != Color.RED) {
                pointAnnotationManager?.delete(annotation)
            }
        }
    }

    fun hideAllMarkersExceptRed() {
        pointAnnotationManager?.annotations?.forEach {
            if(it.textColorInt != Color.RED) {
                it.iconOpacity = 0.0
                it.textOpacity = 0.0
            }
        }
    }

    fun createOpeningMarkers() {
        //Toast.makeText(this, "In showOpeningMarker", Toast.LENGTH_SHORT).show()

        for(key in name_listOfLocationPic.keys){
            val latLongPair = name_listOfLocationPic[key]?.first
            openingmarker(Point.fromLngLat(latLongPair!!.first, latLongPair.second), key, R.drawable.ic_location)
        }
    }

    fun showOpeningMarkers() {
        //Toast.makeText(this, "In showOpeningMarker", Toast.LENGTH_SHORT).show()
        val drawable = ResourcesCompat.getDrawable(this.resources, R.drawable.ic_location, null)
        val bitmap = bitmapFromDrawable(drawable!!)
        pointAnnotationManager?.annotations?.forEach {
            it.iconOpacity = 100.0
            it.textOpacity = 100.0
            it.iconImageBitmap = bitmap
            it.iconSize = 1.5
            it.textSize = 14.0
            it.textColorInt = Color.GRAY
        }
    }

    fun hideOpeningMarkers() {
        pointAnnotationManager?.annotations?.forEach {
            it.iconOpacity = 0.0
            it.textOpacity = 0.0
        }
    }

    fun updateAnnotation(annotation: PointAnnotation, drawableResId: Int, iconSize: Double, textColor: Int) {
        val drawable = resources.getDrawable(drawableResId, null)
        val bitmap = bitmapFromDrawable(drawable)

        annotation.apply {
            this.iconImageBitmap = bitmap
            this.iconSize = iconSize
            this.textSize = 14.0
            this.textColorInt = textColor
            this.textOffset = listOf(0.0, 1.0)
        }
        pointAnnotationManager?.update(annotation)
    }

     private fun clickMarker() {
        pointAnnotationManager?.addClickListener { annotation ->
           //Toast.makeText(this@MainActivity, "annotation: " + annotation.point.toJson(), Toast.LENGTH_SHORT).show()

            // Restore the previous marker to its original state
            lastClickedAnnotation?.let {
                updateAnnotation(it, lastClickedAnnotation_drawableResId!!, lastClickedAnnotation_iconSize!!, lastClickedAnnotation_color!!)
                isMarkerRed[it.point] = false
            }

            lastClickedAnnotation = annotation
            lastClickedAnnotation_iconSize = annotation.iconSize
            lastClickedAnnotation_color = annotation.textColorInt
            lastClickedAnnotation_drawableResId = when (lastClickedAnnotation_color) {
                Color.BLUE -> R.drawable.ic_blue_location
                Color.GRAY -> R.drawable.ic_location
                Color.DKGRAY -> R.drawable.ic_green_location
                else -> R.drawable.ic_red_location
            }

            // Update the clicked marker
            updateAnnotation(annotation, R.drawable.ic_red_location, 1.7, Color.RED)
            isMarkerRed[annotation.point] = true

            // Update the camera to center on the clicked marker
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(annotation.point)
                    .zoom(18.0)
                    .build()
            )

            // Set the last clicked marker
            val bottomSheetDialog = BottomSheetDialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog, null)
            bottomSheetDialog.setContentView(view)

            val imageView = view.findViewById<ImageView>(R.id.imageView)
            imageView.setImageResource(name_listOfLocationPic[annotation.textField]!!.second)

            val directionText = view.findViewById<TextView>(R.id.directionTextView)
            directionText.text = annotation.textField

            val directionButton = view.findViewById<Button>(R.id.directionButton)

            directionButton.setOnClickListener {
               // Toast.makeText(this@MainActivity, "DDDDDDDDDDDDDDDD", Toast.LENGTH_SHORT).show()
                // Code to handle direction button click
                hideAllMarkersExceptRed()
                bottomSheetDialog.dismiss()
                clearRoute()
                btnNearestPlace.findViewById<Button>(R.id.btn_nearest_place).visibility = View.GONE
                lastUserLocation?.let { userLocation ->
                    // Define the origin and destination points
                    val originPoint =
                        Point.fromLngLat(userLocation.longitude, userLocation.latitude)
                    val destinationPoint = annotation.point  // The point of the clicked marker

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

                                    currentRoutes = routes

                                    // Draw the route on the map
                                    drawRoute(route)

                                    // Set navigation routes to start navigation (if required)
                                    //mapboxNavigation.setNavigationRoutes(routes)

                                    mapView.mapboxMap.setCamera(
                                        CameraOptions.Builder()
                                            .center(Point.fromLngLat(
                                                (originPoint.longitude() + destinationPoint.longitude()) / 2,
                                                (originPoint.latitude() + destinationPoint.latitude()) / 2
                                            ))
                                            .zoom(17.5)
                                            .build()
                                    )
                                    supportActionBar?.hide()

                                    /*
                                    val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_navigation, null)
                                    val newBottomSheetDialog = BottomSheetDialog(this@MainActivity).apply {
                                        setContentView(bottomSheetView)
                                        window?.setDimAmount(0f)

                                        // Revised at: 202411041120 (?? Take it out so that we can hit Back to go back to main map layout)
                                        /*
                                        setCancelable(false)
                                        setCanceledOnTouchOutside(false)
                                        */
                                    }
                                    */

                                    // Handle Start Navigation button click
                                    val startNavigation_btn = findViewById<Button>(R.id.btn_startnavigation)
                                    startNavigation_btn.visibility = View.VISIBLE

                                    val finishNavigation_btn = findViewById<Button>(R.id.btn_finishnavigation)
                                    startNavigation_btn.setOnClickListener {
                                    //bottomSheetView.findViewById<Button>(R.id.buttonStartActiveGuidance).setOnClickListener {
                                        // Start navigation or perform another action here
                                        //Toast.makeText(this@MainActivity, "Starting navigation...", Toast.LENGTH_SHORT).show()

                                        // You can initiate turn-by-turn navigation here
                                        // Example: mapboxNavigation.startTripSession()
                                        //mapboxNavigation.startTripSession()

                                        startNavigation_btn.visibility = View.GONE
                                        finishNavigation_btn.visibility = View.VISIBLE

                                        startActiveGuidance(routes)
                                        clearRoute()
                                        // initialize the location puck
                                        mapView.location.apply {
                                            this.locationPuck = LocationPuck2D(
                                                bearingImage = ImageHolder.Companion.from(
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

                                    finishNavigation_btn.setOnClickListener {
                                    //bottomSheetView.findViewById<Button>(R.id.buttonFinishActiveGuidance).setOnClickListener {
                                        mapboxNavigation.setNavigationRoutes(emptyList())

                                        startNavigation_btn.visibility = View.GONE
                                        finishNavigation_btn.visibility = View.GONE
                                        btnNearestPlace.visibility = View.VISIBLE

                                        //newBottomSheetDialog.dismiss()

                                        showOpeningMarkers()
                                        clearRoute()
                                        mapView.location.apply {
                                            this.locationPuck = defaultLocationPuck
                                            setLocationProvider(navigationLocationProvider)
                                            puckBearingEnabled = false
                                            enabled = true
                                        }

                                        // Show the app bar
                                        supportActionBar?.show()
                                        supportActionBar?.collapseActionView()
                                        //btnNearestPlace.findViewById<Button>(R.id.btn_nearest_place).visibility = View.VISIBLE
                                    }

                                    //newBottomSheetDialog.show()

                                    /*
                                    binding.buttonStartActiveGuidance.apply {
                                            startActiveGuidance(routes)
                                        }
                                    }
                                     */

                                } else {
                                    // Handle no routes found
                                    Toast.makeText(
                                        this@MainActivity,
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
                                    this@MainActivity,
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
                                    this@MainActivity,
                                    "Route request canceled",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.w("RouteError", "Route request was canceled")
                            }
                        }
                    )
                } ?: run {
                    // Handle the case when the user's location is not available
                    Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            bottomSheetDialog.show()

            true
        }
    }


    /*
    fun addMarker(point: Point, text: String,  iconResId: Int) {
        val drawable = resources.getDrawable(iconResId, null)
        val bitmap = bitmapFromDrawable(drawable)

        val pointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(bitmap)
            .withIconSize(1.7)
            .withTextField(text)
            .withTextSize(14.0)
            .withTextColor(Color.RED)
            .withTextAnchor(TextAnchor.TOP)
            .withTextOffset(listOf(0.0, 1.0))

        pointAnnotationManager?.let { manager ->
            searchAnnotation = manager.create(pointAnnotationOptions)
            newAnnotations.add(searchAnnotation!!)
            Log.d("MainActivity", "Marker added at: ${point.latitude()}, ${point.longitude()}")
        } ?: Log.d("MainActivity", "PointAnnotationManager is null")
    }

    fun clearMarker() {
        //Toast.makeText(this, "IIIIIIIIIIIIIII", Toast.LENGTH_SHORT)
        searchAnnotation?.let { annotation ->
            pointAnnotationManager?.delete(annotation)
            pointAnnotation = null
        }
        // Iterate through each annotation in the newAnnotations list
        /*
        newAnnotations.forEach {
            // Check if the current annotation is not null
            it.let { annotation ->
                // Delete the current annotation from the pointAnnotationManager
                // This removes the marker associated with this annotation from the map
                pointAnnotationManager?.delete(annotation)
            }
        }
        // Clear the newAnnotations list to remove all references to the deleted annotations
        // This ensures that the list is empty and ready for future use
        newAnnotations.clear()
        */
    }
    */

    private fun monitorProximityToUserLocation() {
        val locationComponent = mapView.location

        locationComponent.addOnIndicatorPositionChangedListener { point ->

            if(blueAnnotations.isNotEmpty()){
                return@addOnIndicatorPositionChangedListener
            }

            val userLocation = android.location.Location("").apply {
                latitude = point.latitude()
                longitude = point.longitude()
            }

            greenAnnotations_PointAnnotation_Distance.clear()
            for (annotation in pointAnnotationManager!!.annotations) {
            //for (annotation in openingAnnotations) { // Revised at: 202404111045 (Avoid using openingAnnotations)
                val markerLocation = android.location.Location("").apply {
                    latitude = annotation.point.latitude()
                    longitude = annotation.point.longitude()
                }

                val distance = userLocation.distanceTo(markerLocation).toDouble()

                // For nearest places testing while user not on campus
                /*
                val latLongPair = name_listOfLocationPic["Faculty of Science and Technology"]?.first
                val tempLocation = android.location.Location("").apply {
                    latitude = latLongPair!!.second
                    longitude = latLongPair.first
                }
                val distance = tempLocation.distanceTo(markerLocation).toDouble()
                 */

                if (distance <= DISTANCE_THRESHOLD) {
                    updateAnnotation(annotation, R.drawable.ic_green_location, 1.5, Color.DKGRAY)
                    isMarkerRed[annotation.point] = true

                    greenAnnotations_PointAnnotation_Distance.add(annotation to distance)
                }
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        //searchView?.queryHint = "Search Location"

        searchView?.let {
            it.queryHint = "Search Location"
            it.suggestionsAdapter = SearchSuggestionsAdapter(this,this,this, mapView, searchView, lastUserLocation, mapboxNavigation, polylineAnnotationManager)
            /*
            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!newText.isNullOrEmpty()) {
                        clearOpeningMarker() // Clear the opening marker
                    }
                    if (newText.isNullOrEmpty()) {
                        clearMarker() // Clear the search marker
                        showOpeningMarkers()
                        supportActionBar?.show()
                    }
                    return false
                }
            })*/
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                // Handle search action
                //val intent = Intent(this, LocationSearchActivity::class.java)
                //startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, you can now start the trip session
                    Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    // Permission denied
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        // Provide an explanation to the user about why the permissions are needed
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            initializeMap()
        } else {
            // Handle the case where the user denied the permission
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.SciTech-> {
                // Handle Campus Information click
                val intent = Intent(this, SciTechActivity::class.java)
                startActivity(intent)
            }
            R.id.Engineer-> {
                // Handle Campus Information click
                val intent = Intent(this, EngineerActivity::class.java)
                startActivity(intent)
            }
            R.id.Education-> {
                // Handle Campus Information click
                val intent = Intent(this, EducationActivity::class.java)
                startActivity(intent)
            }
            R.id.Eco-> {
                // Handle Campus Information click
                val intent = Intent(this, EcoActivity::class.java)
                startActivity(intent)
            }
            R.id.Business-> {
                // Handle Campus Information click
                val intent = Intent(this, BusinessActivity::class.java)
                startActivity(intent)
            }
            R.id.industry-> {
                // Handle Campus Information click
                val intent = Intent(this, IndustryActivity::class.java)
                startActivity(intent)
            }
            R.id.Inter-> {
                // Handle Campus Information click
                val intent = Intent(this, InterActivity::class.java)
                startActivity(intent)
            }
            R.id.LibArt-> {
                // Handle Campus Information click
                val intent = Intent(this, LibArtActivity::class.java)
                startActivity(intent)
            }
            R.id.building36-> {
                // Handle Campus Information click
                val intent = Intent(this, Building36::class.java)
                startActivity(intent)
            }
            R.id.building37-> {
                // Handle Campus Information click
                val intent = Intent(this, Building37::class.java)
                startActivity(intent)
            }
            R.id.building80-> {
                // Handle Campus Information click
                val intent = Intent(this, Building80::class.java)
                startActivity(intent)
            }
            R.id.buildingcreative-> {
                // Handle Campus Information click
                val intent = Intent(this, Buildingcreative::class.java)
                startActivity(intent)
            }


        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("onBackPressed is deprecated. Use onBackPressedDispatcher instead.", ReplaceWith("onBackPressedDispatcher"))
    override fun onBackPressed() {
        super.onBackPressed()
        newAnnotations.forEach {
            pointAnnotationManager?.delete(it)
        }

        // When "Start Navigation" Button is pressed, supportActionBar will be hidden.
        if (supportActionBar?.isShowing == false) {
            // Show the origin point
            lastUserLocation?.let { userLocation ->
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(userLocation.longitude, userLocation.latitude))
                        .zoom(17.5)
                        .build()
                )
            }

            // Show the app bar
           // supportActionBar?.show()
            //Toast.makeText(this, "xxxxxxxxxxxxx", Toast.LENGTH_SHORT).show()
            clearRoute()
            showOpeningMarkers()
            btnNearestPlace.findViewById<Button>(R.id.btn_nearest_place).visibility = View.VISIBLE
        }else {
            // Get the currently selected annotation (if any)
            if (lastClickedAnnotation != null) {
                //Toast.makeText(this, "yyyyyyyyyyy", Toast.LENGTH_SHORT).show()
                // Reset all annotations to their original state
                for (annotation in pointAnnotationManager!!.annotations) {
                    //for (annotation in openingAnnotations) { // Revised at: 202404111045 (Avoid using openingAnnotations)
                    updateAnnotation(annotation, R.drawable.ic_location, 1.5, Color.GRAY)
                    isMarkerRed[annotation.point] = false
                }

                lastClickedAnnotation = null
                lastClickedAnnotation_iconSize = 0.0
                lastClickedAnnotation_color = 0
                lastClickedAnnotation_drawableResId = 0
                clearRoute()
            } else {
                clearRoute()
                onBackPressedDispatcher.onBackPressed()
                onBackPressedCallback?.handleOnBackPressed()
            }
        }
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
        Log.d("clearRoute", "Clearing the route")
        polylineAnnotationManager?.deleteAll()
        searchSuggestionsAdapter.clearRoute()
    }
    private fun checkPermissions(): Boolean {
        // Check if the necessary location permissions are granted
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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

    override fun onStart() {
        super.onStart()
        mapboxNavigation.registerLocationObserver(locationObserver)
        mapboxNavigation.registerRoutesObserver(routesObserver)
    }
    override fun onStop() {
        super.onStop()
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        mapboxNavigation.unregisterRoutesObserver(routesObserver)

    }
    override fun onDestroy() {
        super.onDestroy()
        MapboxNavigationProvider.destroy()
        onBackPressedCallback?.remove()
    }
}





