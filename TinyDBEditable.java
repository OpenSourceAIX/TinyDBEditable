// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

// Modify by ColinTree(colinycl123@gmail.com)
// Added: param of file name

package cn.colintree.aix;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.errors.*;
import com.google.appinventor.components.runtime.util.*;
import com.google.appinventor.components.runtime.*;
import java.util.*;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;

import java.security.MessageDigest;
/**
* Persistently store YAIL values on the phone using tags to store and retrieve.
*
* @author markf@google.com (Mark Friedman)
*/
@DesignerComponent(version = TinyDBEditable.VERSION,
    description = "TinyDBEditable is a non-visible component that stores data for an app. " +
    "<p> Apps created with App Inventor are initialized each time they run: " +
    "If an app sets the value of a variable and the user then quits the app, " +
    "the value of that variable will not be remembered the next time the app is run. " +
    "In contrast, TinyDBEditable is a <em> persistent </em> data store for the app, " +
    "that is, the data stored there will be available each time the app is " +
    "run. An example might be a game that saves the high score and " +
    "retrieves it each time the game is played. </<p> " +
    "<p> Data items are strings stored under <em>tags</em> . To store a data " +
    "item, you specify the tag it should be stored under.  Subsequently, you " +
    "can retrieve the data that was stored under a given tag. </p>" +
    "<p> There is only one data store per app. Even if you have multiple TinyDBEditable " +
    "components, they will use the same data store. To get the effect of " +
    "separate stores, use different keys. Also each app has its own data " +
    "store. You cannot use TinyDBEditable to pass data between two different apps on " +
    "the phone, although you <em>can</em> use TinyDb to shares data between the " +
    "different screens of a multi-screen app. </p> " +
    "<p>When you are developing apps using the AI Companion, all the apps " +
    "using that companion will share the same TinyDb.  That sharing will disappear " +
    "once the apps are packaged.  But, during development, you should be careful to clear " +
    "the TinyDb each time you start working on a new app.</p>",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/tinyDB.png")

@SimpleObject(external = true)
public class TinyDBEditable extends AndroidNonvisibleComponent implements Component {
    public static final int VERSION = 1;
    
    private SharedPreferences sharedPreferences;
    
    private String tableName="TinyDB1";
    
    private Context context;  // this was a local in constructor and final not private
    
    
    /**
    * Creates a new TinyDBEditable component.
    *
    * @param container the Form that this component is contained in.
    */
    public TinyDBEditable(ComponentContainer container) {
    	super(container.$form());
    	context = (Context) container.$context();
    	sharedPreferences = context.getSharedPreferences("TinyDB1", Context.MODE_PRIVATE);
    }
    
    @SimpleProperty(description = "Return the name of the table that TinyDB uses.",category = PropertyCategory.BEHAVIOR)
    public String TableName(){
    	return tableName;
    }
    
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
    	defaultValue = "TinyDB1")
    @SimpleProperty(description = "The name of the table that TinyDB uses.")
    public void TableName(String name) {
    	tableName=name;
    	sharedPreferences = context.getSharedPreferences(tableName, Context.MODE_PRIVATE);
    }
    
	
    @SimpleFunction(description="Go search \"MD5\" on Google or Baidu")
    public String MD5(Object thing){
    	return MD5it((String)thing);
    }
    public String MD5it(String s) {
    	char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    	
    	try {
    		byte[] btInput = s.getBytes();
    		// 获得MD5摘要算法的 MessageDigest 对象
    		MessageDigest mdInst = MessageDigest.getInstance("MD5");
    		// 使用指定的字节更新摘要
    		mdInst.update(btInput);
    		// 获得密文
    		byte[] md = mdInst.digest();
    		// 把密文转换成十六进制的字符串形式
    		int j = md.length;
    		char str[] = new char[j * 2];
    		int k = 0;
    		for (int i = 0; i < j; i++) {
    			byte byte0 = md[i];
    			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
    			str[k++] = hexDigits[byte0 & 0xf];
    		}
    		return new String(str);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    
    /**
    * Store the given value under the given tag.  The storage persists on the
    * phone when the app is restarted.
    *
    * @param tag The tag to use
    * @param valueToStore The value to store. Can be any type of value (e.g.
    * number, text, boolean or list).
    */
    @SimpleFunction
    public void StoreValue(final String tag, final Object valueToStore) {
    	final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    	try {
    		sharedPrefsEditor.putString(tag, JsonUtil.getJsonRepresentation(valueToStore));
    		sharedPrefsEditor.commit();
    	} catch (JSONException e) {
    		throw new YailRuntimeError("Value failed to convert to JSON.", "JSON Creation Error.");
    	}
    }
    
    /**
    * Retrieve the value stored under the given tag.  If there's no such tag, then return valueIfTagNotThere.
    *
    * @param tag The tag to use
    * @param valueIfTagNotThere The value returned if tag in not in TinyDBEditable
    * @return The value stored under the tag. Can be any type of value (e.g.
    * number, text, boolean or list).
    */
    @SimpleFunction
    public Object GetValue(final String tag, final Object valueIfTagNotThere) {
    	try {
    		String value = sharedPreferences.getString(tag, "");
    		// If there's no entry with tag as a key then return the empty string.
    		//    was  return (value.length() == 0) ? "" : JsonUtil.getObjectFromJson(value);
    		return (value.length() == 0) ? valueIfTagNotThere : JsonUtil.getObjectFromJson(value);
    	} catch (JSONException e) {
    		throw new YailRuntimeError("Value failed to convert from JSON.", "JSON Creation Error.");
    	}
    }
    
    /**
    * Return a list of all the tags in the data store
    *
    * @param
    * @return a list of all keys.
    */
    @SimpleFunction
    public Object GetTags() {
    	List<String> keyList = new ArrayList<String>();
    	Map<String,?> keyValues = sharedPreferences.getAll();
    	// here is the simple way to get keys
    	keyList.addAll(keyValues.keySet());
    	java.util.Collections.sort(keyList);
    	return keyList;
    }
    
    /**
    * Clear the entire data store
    *
    */
    @SimpleFunction
    public void ClearAll() {
    	final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    	sharedPrefsEditor.clear();
    	sharedPrefsEditor.commit();
    }
    
    /**
    * Clear the entry with the given tag
    *
    * @param tag The tag to remove.
    */
    @SimpleFunction
    public void ClearTag(final String tag) {
    	final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    	sharedPrefsEditor.remove(tag);
    	sharedPrefsEditor.commit();
    }
    
    public void onDelete() {
    	final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    	sharedPrefsEditor.clear();
    	sharedPrefsEditor.commit();
    }
}