package cmccsi.mhealth.app.sports.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentAdapter extends FragmentPagerAdapter {

	ArrayList<Fragment> list;  
    public MyFragmentAdapter(FragmentManager fm,ArrayList<Fragment> listViews) {  
        super(fm);  
        this.list = listViews;  
          
    }  
    
	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

}
