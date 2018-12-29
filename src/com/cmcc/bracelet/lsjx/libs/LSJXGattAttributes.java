/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmcc.bracelet.lsjx.libs;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class LSJXGattAttributes {
	private static HashMap<String, String> attributes = new HashMap<String, String>();
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	public static String TOUCHUAN_DEVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
	public static String TOUCHUAN_SEND = "0000fff1-0000-1000-8000-00805f9b34fb";
	public static String TOUCHUAN_RECEIVE = "0000fff2-0000-1000-8000-00805f9b34fb";
	static {
		// Sample Services.
		attributes.put("0000fff0-0000-1000-8000-00805f9b34fb",
				"Data Channel Service");
		// Sample Characteristics.
		attributes.put("00002a29-0000-1000-8000-00805f9b34fb",
				"Manufacturer Name String");
		attributes.put("0000fff1-0000-1000-8000-00805f9b34fb",
				"Data Send Characteristic");
		attributes.put("0000fff2-0000-1000-8000-00805f9b34fb",
				"Data Receive Characteristic");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}

}