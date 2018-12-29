package cmccsi.mhealth.app.sports.common;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import cmccsi.mhealth.app.sports.R;

public class AlertDialogs {
	private static int choicedTeam = 0;
	public static void creatSingleChoiceDialog(String title, Context context, String[] arrayFruit,final onChoicedTeamListener listener) {
		creatSingleChoiceDialog(title, context, arrayFruit, 0, listener);
	}
	public static void creatSingleChoiceDialog(String title, Context context, String[] arrayFruit,int defaultitem, final onChoicedTeamListener listener) {
		defaultitem = defaultitem == -1 ? 0 : defaultitem;
		choicedTeam = defaultitem;
		Dialog dialog = new AlertDialog.Builder(context).setTitle(title).setIcon(R.drawable.sport_running)
		  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onChoicedTeam(choicedTeam);
			}
		}).setSingleChoiceItems(arrayFruit, choicedTeam, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				choicedTeam = which;
			}
		}).setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onChoicedTeam(1);
			}
		}).create();
		dialog.show();
	}

	public static void showOKorNODialog(String title, Context context, OnClickListener listener) {
		Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setPositiveButton("是的", listener);
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	public interface onChoicedTeamListener {
		abstract void onChoicedTeam(int team);
	}
}
