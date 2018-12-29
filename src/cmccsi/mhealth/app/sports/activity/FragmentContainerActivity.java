package cmccsi.mhealth.app.sports.activity;

import android.os.Bundle;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.R;

public class FragmentContainerActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.frament_activity);
		MapStartRunningFragment fragment = new MapStartRunningFragment(
                "map");
		 getSupportFragmentManager().beginTransaction()
         .replace(R.id.frament_activity, fragment).commit();
	}
}
