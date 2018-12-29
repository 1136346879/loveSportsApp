package cmccsi.mhealth.app.sports.pedo;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.MapApplication;
import cmccsi.mhealth.app.sports.common.GetWindowBitmap;
import cmccsi.mhealth.app.sports.common.ImageUtil;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.utils.LogUtils;
import cmccsi.mhealth.app.sports.R;

public class ShareWeiXin implements OnClickListener{
	private static final int THUMB_SIZE = 200;
	
	private Dialog mCustomDialog;
	private Activity mActivity;
	private Bitmap mBitmapBig = null;
	private Bitmap mBottom;
	private Bitmap mTop;
	
	private boolean mCheckWinXin;
	private View mLayout;
	private String mStrUpdateDate;
	
	private IWXAPI mWeiXinAPI;
	private boolean isShareWithQR=false;//是否分享二维码图片

	public ShareWeiXin(Activity context,String shareDate,View takeScreen) {
		mActivity=context;
		mStrUpdateDate=shareDate;
		this.mLayout=takeScreen;
		this.mTop=null;
		
		mWeiXinAPI = MapApplication.mWeiXinAPI;
		
	}
	
	public ShareWeiXin(Activity context,String shareDate,Bitmap top,View takeScreen) {
		mActivity=context;
		mStrUpdateDate=shareDate;
		this.mLayout=takeScreen;
		this.mTop=top;

		mWeiXinAPI = MapApplication.mWeiXinAPI;
		
	}
	/**
	 * 设置是否拼接二维码图片
	 * @param isShare 
	 */
	public void setShareWithQR(boolean isShare){
		this.isShareWithQR=isShare;
	}

	public void Shared() {
		mCustomDialog = new Dialog(mActivity, R.style.dialog_withStatusBar);
		mCustomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//mCustomDialog.setCancelable(false);
		//mCustomDialog.setCanceledOnTouchOutside(false);
		Window window = mCustomDialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		View view = View.inflate(mActivity, R.layout.popwindow_wenxininfo_new, null);
		LinearLayout WxTop = (LinearLayout) view.findViewById(R.id.linear_null);
		WxTop.setOnClickListener(this);
		mCustomDialog.setContentView(view);
		mCustomDialog.show();

		mCustomDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				if (mBitmapBig != null && !mBitmapBig.isRecycled()) {
					// 回收并且置为null
					mBitmapBig.recycle();
					mBitmapBig = null;
				}
				if (mBottom != null && !mBottom.isRecycled()) {
					mBottom.recycle();
					mBottom = null;
				}

				if (mTop != null && !mTop.isRecycled()) {
					mTop.recycle();
					mTop = null;
				}
				//mActivity.finish();
			}
		});

		RelativeLayout textview_friends = (RelativeLayout) view.findViewById(R.id.textview_weixin_friends);
		textview_friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCheckWinXin = true;
				if(mTop!=null)
				{
					takeScreenAndShare(mTop);
				}
				else
				{
					takeScreenAndShare();
				}
			}

		});
		RelativeLayout textview_quanzi = (RelativeLayout) view.findViewById(R.id.textview_weixin_quan);
		textview_quanzi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCheckWinXin = false;
				if(mTop!=null)
				{
					takeScreenAndShare(mTop);
				}
				else
				{
					takeScreenAndShare();
				}
			}
		});
		TextView cancel = (TextView) view.findViewById(R.id.textview_share_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCustomDialog.dismiss();
				//mActivity.finish();
			}
		});
	}

	private void takeScreenAndShare()
	{
		if(mLayout==null)
		{
			LogUtils.e("cjz", "ShareWeiXin 没设置截图");
			return;
		}
		mBitmapBig= GetWindowBitmap.takeScreenShot(mActivity, mLayout);
		Logger.d("cjz", "分享图片"+(mBitmapBig==null));
		sendTofriends(mBitmapBig);
//		try {
//			if(mBitmapBig!=null){
//				saveMyBitmap("sharePic",mBitmapBig);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		mBitmapBig.recycle();
		mCustomDialog.dismiss();
	}
	//将截图保存到本地
    public void saveMyBitmap(String bitName, Bitmap mBitmap) throws IOException {  
        File f = new File("/sdcard/" + bitName + ".png");  
        f.createNewFile();  
        FileOutputStream fOut = null;  
        try {  
                fOut = new FileOutputStream(f);  
        } catch (FileNotFoundException e) {  
                e.printStackTrace();  
        }  
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);  
        try {  
                fOut.flush();  
        } catch (IOException e) {  
                e.printStackTrace();  
        }  
        try {  
                fOut.close();  
        } catch (IOException e) {  
                e.printStackTrace();  
        }  
}  
	
	
	private void takeScreenAndShare(Bitmap top)
	{
		if(mLayout==null)
		{
			LogUtils.e("cjz", "ShareWeiXin 没设置截图");
			return;
		}
		if(top!=null)
		{
			mBitmapBig= GetWindowBitmap.takeScreenShot(mActivity, mLayout);
//			int mLayoutBitMapHeight=mBitmapBig.getHeight();
			mBitmapBig= ImageUtil.toConformBitmap(mBitmapBig, mTop);
			if(isShareWithQR&&mBitmapBig!=null){
				Bitmap bitmap = null;  
		        try {  
		        	
		            bitmap=BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.share_qr); 
		            bitmap=ImageUtil.zoomImage(bitmap, mBitmapBig.getWidth(), mBitmapBig.getWidth()/3);
		            mBitmapBig=ImageUtil.toConformBitmap(bitmap,mBitmapBig);
		        } catch (OutOfMemoryError e) { 
		        	e.printStackTrace();
		        }finally{
		        	bitmap.recycle();
		        }
			}
//			try {
//			if(mBitmapBig!=null){
//				saveMyBitmap("sharePic",mBitmapBig);
//			}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			sendTofriends(mBitmapBig);
			mBitmapBig.recycle();
			mCustomDialog.dismiss();
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linear_null:
			if (mCustomDialog != null)
				mCustomDialog.dismiss();
			break;

		default:
			break;
		}
	}
	
	private void sendTofriends(Bitmap bmp) {
		if (bmp == null)
		{
			Logger.i("cjz", "没截到图");
			return;
		}
		if (!mWeiXinAPI.isWXAppInstalled()) {
			Toast.makeText(mActivity, "您还没有安装微信", Toast.LENGTH_SHORT).show();
			return;
		} else if (!mWeiXinAPI.isWXAppSupportAPI()) {
			Toast.makeText(mActivity, "您安装的微信版本不支持当前API", Toast.LENGTH_SHORT).show();
			return;
		}
		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		
		Bitmap thumbBmp;
		if (bmp.getHeight() > bmp.getWidth()) {
			thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE * bmp.getWidth() / bmp.getHeight(), THUMB_SIZE,
					true);
//			msg.thumbData = Util.bmpToByteArray(thumbBmp, true); // 设置缩略图
//			thumbBmp.recycle();
		} else {
			thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE * bmp.getHeight() / bmp.getWidth(),
					true);
//			msg.thumbData = Util.bmpToByteArray(thumbBmp, true); // 设置缩略图
//			thumbBmp.recycle();
		}
		bmp=ImageUtil.compressImage(bmp,120);
		WXImageObject imageObject = new WXImageObject(bmp);
		msg.mediaObject = imageObject;
		msg.setThumbImage(thumbBmp);
		// 构造一个Req
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = mCheckWinXin == true ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

		// 调用api接口发送数据到微信
		mWeiXinAPI.sendReq(req);
		bmp.recycle();
		thumbBmp.recycle();
	}
	
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
