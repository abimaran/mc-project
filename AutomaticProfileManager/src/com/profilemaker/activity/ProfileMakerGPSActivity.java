package com.profilemaker.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.profilemaker.activity.R;
import com.profilemaker.dao.LocationDataSource;
import com.profilemaker.model.Location;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ProfileMakerGPSActivity extends MapActivity {
	
	MapView mapView;
	MapController mapc;
	Canvas canvas = null;
	private MyLocationOverlay me=null;
	private Button addLocation;
    private Button saveLocation;
    private Button cancelLocation;
	
    private Intent locationIntent;
    private double latitude;
    private double longitude;
    private ArrayList<Location> locations;
    private int profileId;
    public static Context context;
    
	class MapOverlay extends com.google.android.maps.ItemizedOverlay<OverlayItem> {
		
		private Drawable marker=null;
	    private OverlayItem inDrag=null;
	    private ImageView dragImage=null;
	    	    
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
	    private int xDragImageOffset=0;
	    private int yDragImageOffset=0;
	    private int xDragTouchOffset=0;
	    private int yDragTouchOffset=0;
	    	    
		public MapOverlay(Drawable marker) {
			super(marker);
		    this.marker=marker;
		      
			dragImage=(ImageView)findViewById(R.id.drag);
		      xDragImageOffset=dragImage.getDrawable().getIntrinsicWidth()/2;
		      yDragImageOffset=dragImage.getDrawable().getIntrinsicHeight();
		      		    
		      items.add(new OverlayItem(getPoint(6.9319444,79.8477778),"Colombo",
		                                "Capital of Sri Lanka"));
		      populate();
		}
      
		@Override
	    public void draw(Canvas canvas, MapView mapView,
	                      boolean shadow) {
	      super.draw(canvas, mapView, shadow);
	      
	      boundCenterBottom(marker);
	    }
		
        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView) {
          final int action=event.getAction();
          final int x=(int)event.getX();
          final int y=(int)event.getY();
          boolean result=false;
          
          if (action==MotionEvent.ACTION_DOWN) {
            for (OverlayItem item : items) {
              Point p=new Point(0,0);
              
              mapView.getProjection().toPixels(item.getPoint(), p);
              
              if (hitTest(item, marker, x-p.x, y-p.y)) {
                result=true;
                inDrag=item;
                items.remove(inDrag);
                populate();

                xDragTouchOffset=0;
                yDragTouchOffset=0;
                
                setDragImagePosition(p.x, p.y);
                dragImage.setVisibility(View.VISIBLE);

                xDragTouchOffset=x-p.x;
                yDragTouchOffset=y-p.y;
                
                break;
              }
            }
            
          }else if (action==MotionEvent.ACTION_MOVE && inDrag!=null) {
            setDragImagePosition(x, y);
            result=true;
            
          }else if (action==MotionEvent.ACTION_UP && inDrag!=null) {
            dragImage.setVisibility(View.GONE);
            
            GeoPoint pt=mapView.getProjection().fromPixels(x-xDragTouchOffset,y-yDragTouchOffset);
            OverlayItem toDrop=new OverlayItem(pt, inDrag.getTitle(),inDrag.getSnippet());
            
            items.add(toDrop);
            populate();
            
            inDrag=null;
            result=true;
            
            latitude = mapView.getProjection().fromPixels(x, y).getLatitudeE6()/1000000.0;
            longitude = mapView.getProjection().fromPixels(x, y).getLongitudeE6()/1000000.0;
            
            Log.e("location",""+latitude+","+longitude);
          }
          
          return(result || super.onTouchEvent(event, mapView));
        }

		@Override
		protected OverlayItem createItem(int i) {
			return(items.get(i));
		}

		@Override
		public int size() {
			return(items.size());
		}
		
		private void setDragImagePosition(int x, int y) {
		      LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)dragImage.getLayoutParams();
		            
		      lp.setMargins(x-xDragImageOffset-xDragTouchOffset,y-yDragImageOffset-yDragTouchOffset, 0, 0);
		      dragImage.setLayoutParams(lp);
		    }
    } 
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_maker_gps);
        context = getApplicationContext();
        locationIntent = getIntent();
        profileId = locationIntent.getIntExtra("profileId", 0);
        
        mapView = (MapView) findViewById(R.id.mapview);
        addLocation = (Button)findViewById(R.id.btnAddLocation);
        saveLocation = (Button)findViewById(R.id.btnSaveLocation);
        cancelLocation = (Button)findViewById(R.id.btnCancelLocation);
        
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);
        mapc = mapView.getController();
        mapView.getController().setCenter(getPoint(6.9319444, 79.8477778));
        mapView.getController().setZoom(17);
        
        Drawable marker=getResources().getDrawable(R.drawable.marker);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(),
		marker.getIntrinsicHeight());

        mapView.getOverlays().add(new MapOverlay(marker));

        me=new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(me);   
        
        locations = new ArrayList<Location>();
        
        addLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Location location = new Location(profileId, latitude, longitude);
				locations.add(location);
				
				Toast toast = Toast.makeText(context, "Location is added successfully!", Toast.LENGTH_SHORT);
				toast.show();				
			}
		});
        
        saveLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				LocationDataSource locationDataSource = new LocationDataSource(context);
				
				//check whether Locations arraylist is empty................
				if(!locations.isEmpty()){
					Iterator<Location> itr = locations.iterator();
					locationDataSource.open();
					
					//go through location arraylist and save one by one.........
					while(itr.hasNext()){
						Location toDbLocation = itr.next();
						profileId = toDbLocation.getProfileId();
						latitude = toDbLocation.getLatitude();
						longitude = toDbLocation.getLongitude();
						
						Location fromDbLocation = locationDataSource.createLocation(profileId, 
								latitude, longitude);
						
						Log.i("latitude & longitude",fromDbLocation.getLatitude()+","+fromDbLocation.getLongitude());
						Toast toast = Toast.makeText(context, "Location details are saved successfully!", Toast.LENGTH_SHORT);
						toast.show();
					}
					locationDataSource.close();
					
				}else{
					Toast toast = Toast.makeText(context, "Please add Geo Locations from the map!", Toast.LENGTH_SHORT);
					toast.show();					
				}
				
				Intent backIntent = new Intent(ProfileMakerGPSActivity.this,ProfileManagerActivity.class);
				startActivity(backIntent);
			}
		});
        
        cancelLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent backIntent = new Intent(ProfileMakerGPSActivity.this,ProfileManagerActivity.class);
				startActivity(backIntent);
			}
		});
	}
	
	private GeoPoint getPoint(double lat, double lon) {
	    return(new GeoPoint((int)(lat*1000000.0),(int)(lon*1000000.0)));
	  }
		
	@Override
	protected boolean isRouteDisplayed() {		
		return(false);
	}

}

