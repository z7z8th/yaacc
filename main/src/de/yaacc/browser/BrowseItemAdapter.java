package de.yaacc.browser;

import java.util.LinkedList;
import java.util.List;

import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.AudioItem;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.PlaylistItem;
import org.teleal.cling.support.model.item.TextItem;
import org.teleal.cling.support.model.item.VideoItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.yaacc.R;
import de.yaacc.R.drawable;
import de.yaacc.R.id;
import de.yaacc.R.layout;
import de.yaacc.R.string;
import de.yaacc.upnp.ContentDirectoryBrowseResult;

public class BrowseItemAdapter extends BaseAdapter{
	
	private LayoutInflater inflator;
	private List<DIDLObject> objects;
	
	public BrowseItemAdapter(Context ctx, String objectId){
		
		BrowseActivity.uClient.storeNewVisitedObjectId(objectId);
		
		inflator = LayoutInflater.from(ctx);
		
		ContentDirectoryBrowseResult result = BrowseActivity.uClient.browseSync(BrowseActivity.uClient.getProviderDevice(),objectId);
    	
		DIDLContent a = result.getResult();
		
		if(a != null){
			
			objects = new LinkedList<DIDLObject>();
			
			//Add all children in two steps to get containers first
			objects.addAll(a.getContainers());
			objects.addAll(a.getItems());
			
		}else  {

			String text = ctx.getString(R.string.error_upnp_generic);
			int duration = Toast.LENGTH_SHORT;

			if(result.getUpnpFailure() != null){
				text = ctx.getString(R.string.error_upnp_specific)+" "+result.getUpnpFailure();
			}
			
			Log.e("ResoveError",text);
			
    		Toast toast = Toast.makeText(ctx, text, duration);
    		toast.show();
		}
		
		
		
    	
	}

	@Override
	public int getCount() {
		if(objects == null){
			return 0;
		}
		return objects.size();
	}

	@Override
	public Object getItem(int arg0) {
		return objects.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
		//return folders.get(arg0).getId();
	}

	@Override
	public View getView(int position, View arg1, ViewGroup parent) {
		ViewHolder holder;
		
		if(arg1 == null){
			arg1 = inflator.inflate(R.layout.browse_item,parent,false);
			
			holder = new ViewHolder();
			holder.icon = (ImageView) arg1.findViewById(R.id.browseItemIcon);
			holder.name = (TextView) arg1.findViewById(R.id.browseItemName);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
				
		DIDLObject currentObject = (DIDLObject)getItem(position);

		holder.name.setText(currentObject.getTitle());
		
		if(currentObject instanceof Container){
			holder.icon.setImageResource(R.drawable.folder);
		} else if(currentObject instanceof AudioItem){
			holder.icon.setImageResource(R.drawable.cdtrack);
		} else if(currentObject instanceof ImageItem){
			holder.icon.setImageResource(R.drawable.image);
		} else if(currentObject instanceof VideoItem){
			holder.icon.setImageResource(R.drawable.video);
		} else if(currentObject instanceof PlaylistItem){
			holder.icon.setImageResource(R.drawable.playlist);
		} else if(currentObject instanceof TextItem){
			holder.icon.setImageResource(R.drawable.txt);
		} else {
			holder.icon.setImageResource(R.drawable.unknown);
		}

		return arg1;
	}
	
	
	static class ViewHolder{
		ImageView icon;
		TextView name;
	}
	
	public DIDLObject getFolder(int position){
		if(objects == null){
			return null;
		}
		return objects.get(position);
	}



}