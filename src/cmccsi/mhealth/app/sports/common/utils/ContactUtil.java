package cmccsi.mhealth.app.sports.common.utils;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.bean.ContactInfo;
import cmccsi.mhealth.app.sports.common.Logger;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * 本地通讯录操作类
 * 
 * @type ContactUtil TODO
 * @author shaoting.chen
 * @time 2015年6月3日下午5:26:14
 */
public class ContactUtil {

	/**
	 * 获取本地通讯录 TODO
	 * 
	 * @param context
	 * @return
	 * @return List<ContactInfo>
	 * @author shaoting.chen
	 * @time 下午5:26:26
	 */
	public static List<ContactInfo> getContactList(Context context) {
		List<ContactInfo> mContactList = new ArrayList<ContactInfo>();

		String[] PHONES_PROJECTION = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER };
		//查询联系人公用信息
		Cursor c = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PHONES_PROJECTION, null,
				null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		if (c.moveToFirst()) {
			do {
				// 获得联系人的ID号
				String contactId = c.getString(0);
				// 获得联系人姓名
				String name = c.getString(1);
				// 查看该联系人有多少个电话号码。如果没有这返回值为0
				int phoneCount = c.getInt(2);
				String number = null;
				if (phoneCount > 0) {
					// 获得联系人的电话号码
					Cursor phones = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
					if (phones.moveToFirst()) {
						do {
							number = phones.getString(0); // 获得联系人的电话号码
							if (StringUtils.isNotBlank(number)) {
								ContactInfo contact = new ContactInfo();
								contact.setPhonename(name);
								contact.setPhonenumber(number);
								mContactList.add(contact);
//								Logger.i("ContactList", "---name:" + name + "--number:" + number);
							}
						} while (phones.moveToNext());
					}
					phones.close();
				}
			} while (c.moveToNext());
		}
		c.close();
		return mContactList;
	}

}
