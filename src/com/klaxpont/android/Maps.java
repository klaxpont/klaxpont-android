package com.klaxpont.android;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Maps extends MapActivity implements LocationListener {
	
	private MapView mapView;
	private MapController mc;
	private LocationManager lm;
	private double lat = 0;
	private double lng = 0;
	private MyLocationOverlay myLocation = null;
	private MapsItems mItems;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.maps);
	   mapView = (MapView) this.findViewById(R.id.mapView);
	   mapView.setBuiltInZoomControls(true);
	   mc = mapView.getController();
	   mc.setZoom(12);
	   myLocation = new MyLocationOverlay(getApplicationContext(), mapView);
	   mapView.getOverlays().add(myLocation);
	   myLocation.enableMyLocation();
	   myLocation.enableCompass();
	   lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
	   lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
	   lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
	   
	   List<Overlay> mapOverlays = mapView.getOverlays();
	   Drawable drawable = this.getResources().getDrawable(R.drawable.small_marker);
	   mItems = new MapsItems(drawable, this);
	   GeoPoint point = new GeoPoint(19240000,-99120000);
	   OverlayItem overlayitem = new OverlayItem(point, "Mexico city", "I'm in Mexico City!");
	   GeoPoint point2 = new GeoPoint(35410000, 139460000);
	   OverlayItem overlayitem2 = new OverlayItem(point2, "Sekai", "konichiwa!");
	   GeoPoint BouinuxPoint = new GeoPoint(47455719, -00545025);
	   OverlayItem overlayitem3 = new OverlayItem(BouinuxPoint, "Bouinux", "Welcome Home!");
	   mItems.addOverlay(overlayitem);
	   mItems.addOverlay(overlayitem2);
	   mItems.addOverlay(overlayitem3);
	   mapOverlays.add(mItems);
	 }
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_S)
		{
			mapView.setSatellite(!mapView.isSatellite());
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onLocationChanged(Location location) {
		lat = location.getLatitude();
		lng = location.getLongitude();
		Toast.makeText(getBaseContext(),
				"Location change to : Latitude = " + lat + " Longitude = " + lng,
				Toast.LENGTH_SHORT).show();
		GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		mc.animateTo(p);
		mc.setCenter(p);
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}