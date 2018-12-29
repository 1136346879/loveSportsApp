package cmccsi.mhealth.app.sports.basic;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.widget.RemoteViews;
import cmccsi.mhealth.app.sports.pedo.PedometerActivity;
import cmccsi.mhealth.app.sports.R;

public class SendMsgBroadCast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int icon = R.drawable.icon_notify;
		CharSequence tickerText = "爱动力通知";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_phone_sendmsg);
		contentView.setTextViewText(R.id.notification_phone_sendmsg_text, "请您在结束今天的运动后，下拉运动页面，及时上传运动数据，以免影响您的排名");
		
		notification.contentView = contentView;
		Intent notificationIntent = new Intent(context, cmccsi.mhealth.app.sports.pedo.PedometerActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
		PendingIntent contentIntent = PendingIntent.getActivity(context, 1, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.contentIntent = contentIntent;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		mNotificationManager.notify(22, notification);
	}
}
