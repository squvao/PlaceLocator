package com.example.kirill.placelocator.LocationActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kirill.placelocator.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;
import java.util.List;
/*
Current activity is responsible for showing a map on the screen and all the objects being placed
in a correct places on this map. Here we can find the current location of the phone and a window
with detailed information about a place becomes available when a phone enters a certain are around
and object on the map.
 */

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String PLACE_TITLE = "placetitle";
    public static final String PLACE_DRAWABLE_ID = "placedrawableid";
    public static final String PLACE_ABOUT = "placeabout";
    //manager to work with location through which we will be able yo receive the coordinates
    //of our device.
    private LocationManager manager;
    //a field representing a fragment of map. A 'fragment' allows layout file to
    //dynamically include different layouts at runtime.
    private GoogleMap mMap;
    // a field representing a list of places (objects).
    private List<Place> listPlaces;
    //this place id was found around the user's location.
    private int objectId = -1;
    //a field representing layout for a 'MoreAbout' button which provides some
    //additional information about a place (object)
    private LinearLayout linearLayoutMoreObject;
    //a field representing a listener for the change of location
    //here we create a new object of this listener.
    //this listener works when there is a change in location of the phone,
    //when connection(any provider) switches on/off
    private LocationListener listener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //if location data are present we then launch a method
            //for checking if we have entered an area when a window
            //with detailed information pops up around the places (objects)
            //if we have entered this area, we will have availability of opening this info window.
            if(location != null)
                objectCheck(location);
        }
        //this method will allow us to act when changes when statuses of our
        //providers of location have changed.
        //Currently this method is present because LocationListener asks for it presence.
        //Though it does not perform any function in our app.
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        //This method works when one or many of the providers are switching on.
        @Override
        public void onProviderEnabled(String provider) {
            //checking for access to location services
            //if the access has not been granted then we will quit this method
            //In android versions before 6.0 a call for access is passed on
            //automatically by android during the installation of the app.
            //this code is needed for android version higher or equal to 6.0
            //because in this version of android a call for permission has
            //to be done by an app during the run time.
            if (ActivityCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LocationActivity.this, "Access denied", Toast.LENGTH_SHORT).show();
                return;
            }
            //while providers switch on we are trying to receive phone's coordinates
            Location location = manager.getLastKnownLocation(provider);
            //if coordinates have been received and location data is present then
            //we check if we have entered any object's zone.
            if(location != null)
                objectCheck(location);
        }

        //a method which allows us to perform any actions when providers switch off.
        //as a method above this one is required to be present but does not perform
        //any tasks in our app.
        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        //through location manager we will be receiving phone's coordinates.
        //receiving location manager from the system services of android.
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //a list of objects. We will be getting information about certain objects: title,
        //description, picture (from drawable) and also the coordinates of the place.
        listPlaces = new LinkedList<Place>(){
            {
                //setting all the places which an app will represent on the map with their full names, locations and pictures uploaded from drawable
                add(new Place(51.277884, 1.088516, R.string.location_activity_st_augustine_abbey, R.string.location_activity_st_augustine_abbey, R.drawable.augustine_abbey));
                add(new Place(51.278731, 1.07786, R.string.location_activity_canterbury_heritage_museum_title, R.string.location_activity_canterbury_heritage_museum, R.drawable.canterbury_heritage_museum));
                add(new Place(51.281441, 1.075839, R.string.location_activity_westgate_title, R.string.location_activity_westgate, R.drawable.westgate));
                add(new Place(51.28088, 1.076016, R.string.location_activity_kent_museum_of_freemasonry_title, R.string.location_activity_kent_museum_of_freemasonry, R.drawable.kent_museum_of_freemasonry));
                add(new Place(51.278651, 1.081495, R.string.location_activity_canterbury_roman_museum_title, R.string.location_activity_canterbury_roman_museum, R.drawable.canterbury_roman_museum));
                add(new Place(51.2756, 1.07452, R.string.location_activity_canterbury_castle_title, R.string.location_activity_canterbury_castle, R.drawable.canterbury_castle));
                add(new Place(51.279793, 1.08288, R.string.location_activity_canterbury_cathedral_title, R.string.location_activity_canterbury_cathedral, R.drawable.canterbury_cathedral));
                add(new Place(51.243764, 0.960475, R.string.location_activity_chilham_castle_title, R.string.location_activity_chilham_castle, R.drawable.chilham_castle));
                add(new Place(51.26742, 1.146913,  R.string.location_activity_howlers_wild_animal_park_title, R.string.location_activity_howlers_wild_animal_park, R.drawable.howletts_map));
                add(new Place(51.279636, 1.079192,  R.string.location_activity_beaney_house_of_art_and_knowledge_title, R.string.location_activity_beaney_house_of_art_and_knowledge, R.drawable.beaney_house_of_art_and_knowledge));
                add(new Place(55.783324, 49.151488, R.string.location_activity_house_title, R.string.location_activity_house, R.drawable.beaney_house_of_art_and_knowledge));
            }
        };

        //linking the 'MoreAbout' button layout with java code.
        //A link with a found layout is written into a variable called linearLayoutMoreObject.
        this.linearLayoutMoreObject = (LinearLayout) this.findViewById(R.id.activity_location_more_object);
        //installing a listener for the 'MoreAbout' button.
        this.linearLayoutMoreObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating an object for opening a new activity. New activity is called ObjectInformationActivity.
                //This activity will be opening in such a way that a dialogue window would do
                //because we installed a theme for it which is called "Theme.AppCompat.Dialog".
                Intent intent = new Intent(LocationActivity.this, ObjectInformationActivity.class);
                //sending data to activity e.g. picture id of the object we found, information about
                //it and title with the right ids for them.
                intent.putExtra(PLACE_DRAWABLE_ID, listPlaces.get(objectId).getIdDrawable());
                intent.putExtra(PLACE_ABOUT, listPlaces.get(objectId).getIdInfo());
                intent.putExtra(PLACE_TITLE, listPlaces.get(objectId).getIdTitle());
                //launching a new activity through intent.
                startActivity(intent);
            }
        });
        //searching for a map fragment through its id.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void location() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                /*Such code is only used in Android 6.0 and higher. this code asks Android for
                 permission to get access to location of the phone. In the earlier options of
                 android this was unnecessary but they introduced it in the new version of OS.
                 The message will pop up if access was denied.. */
            Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            return;
        }
        if (mMap != null) {
            /* Request to all providers to update location of the device with an interval of 0 ms. */
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener); //using gps provider
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener); //using network
            manager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, listener);//using wifi
            Location location;
            //creating an array of providers through which we will be getting access to location services.
            String[] providers = new String[]{LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER};
            //going through the array by using a loop in order to call for each of them.
            for (int i = 0; i < providers.length; i++) {
                String provider = providers[i];
                //a call for phone's coordinates from location manager.
                location = manager.getLastKnownLocation(provider); //receiving location
                /*
                Location might not be distinguished if all the providers do not respond.
                So we check it for information absence.
                 */
                if (location != null) {
                    //if the coordinates information is present then map shifts to the place with phone's location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f));
                    //after we found all the coordinates we launch a validation to check if any objects are close enough.
                    objectCheck(location);
                } else
                    //in case there was no response from any of the providers we then mark the
                    //provider as null in order to in the future check if the location services
                    //are switched on on the user's phone.
                    //if all the location services are not responding then it means they are switched off.
                    providers[i] = null;
            }
            //a validation for location services switched on/off is done through hasLocation method.
            //if location services are not available then we pop up a toast with such a message.
            if(!hasLocation(providers))
                Toast.makeText(this, "Location Service Not available", Toast.LENGTH_SHORT).show();
        }
    }

    //a method to check if location services are working.
    private boolean hasLocation(String[] providers){
        return !(providers[0] == null && providers[1] == null && providers[2] == null);
    }

    private void objectCheck(Location location) {
        float[] distance = new float[2];
        for (int i = 0; i < listPlaces.size(); i++) {
            Place place = listPlaces.get(i);
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    place.getLatitude(), place.getLongitude(), distance);
            if (distance[0] < 200){
                linearLayoutMoreObject.setVisibility(View.VISIBLE);
                objectId = i;
                return;
            }
        }
        linearLayoutMoreObject.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        location();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.removeUpdates(listener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);//установка отображения точки.

        for(Place place : listPlaces){
            LatLng lng = new LatLng(place.getLatitude(), place.getLongitude());
            mMap.addMarker(new MarkerOptions().position(lng).title(getResources().getString(place.getIdInfo())).icon(BitmapDescriptorFactory.fromResource(R.drawable.castle_icon)));
        }
        location();
    }
}
