package com.mdm.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.PackageInfo;

/**
 * כלי עזר להמרת POJOs ל-JSON ולהיפך באמצעות org.json המובנה של אנדרואיד.
 */
public class JsonUtil {

    // --- Serialization ---
/*
    public static JSONObject storeItemToJson(StoreItem item) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("packageName", item.packageName);
        json.put("title", item.title);
        json.put("currentVersion", item.currentVersion);
        json.put("latestVersion", item.latestVersion);
        json.put("source", item.source);
        json.put("itemSourceType", item.itemSourceType.name());
        json.put("customLink", item.customLink != null ? item.customLink : JSONObject.NULL);
        json.put("excludedFromUpdates", item.excludedFromUpdates);
        json.put("downloadLink", item.downloadLink != null ? item.downloadLink : JSONObject.NULL);
        json.put("signature", item.signature != null ? item.signature : JSONObject.NULL);
        json.put("versionCode", item.versionCode);
        json.put("updateAvailable", item.updateAvailable);
        return json;
    }*/
/*
    public static JSONObject configToJson(StoreConfiguration config) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("selectedSources", new JSONArray(config.selectedSources));
        json.put("defaultSource", config.defaultSource);
        json.put("checkInstalledAppsForUpdates", config.checkInstalledAppsForUpdates);
        json.put("installedAppsCheckList", new JSONArray(config.installedAppsCheckList));
        json.put("defaultItemsSource", config.defaultItemsSource.name());
        json.put("defaultItemsLink", config.defaultItemsLink != null ? config.defaultItemsLink : JSONObject.NULL);
        return json;
    }
*/
    /** מייצא את ה-ExportData לאובייקט JSON מלא. */
    /*public static String exportDataToJson(ExportData data) throws JSONException {
        JSONObject root = new JSONObject();
        root.put("version", data.version);
        root.put("exportTimestamp", data.exportTimestamp);
        root.put("storeConfiguration", configToJson(data.storeConfiguration));

        JSONArray itemsArray = new JSONArray();
        for (StoreItem item : data.manualAndCustomItems) {
            itemsArray.put(storeItemToJson(item));
        }
        root.put("manualAndCustomItems", itemsArray);
        return root.toString(4); // פורמט יפה לקריאה
    }*/

    // --- Deserialization ---
/*
    public static StoreItem storeItemFromJson(JSONObject json) throws JSONException {
        // שימוש ב-optString במקום getString כדי לטפל ב-JSONObject.NULL
        String packageName = json.getString("packageName");
        String title = json.getString("title");
        String currentVersion = json.getString("currentVersion");
        String latestVersion = json.getString("latestVersion");
        String source = json.getString("source");
        StoreItem.ItemSourceType itemSourceType = StoreItem.ItemSourceType.valueOf(json.getString("itemSourceType"));
        String customLink = json.optString("customLink", null);
        boolean excludedFromUpdates = json.getBoolean("excludedFromUpdates");
        String downloadLink = json.optString("downloadLink", null);
        String signature = json.optString("signature", null);
        int versionCode = json.getInt("versionCode");
        boolean updateAvailable = json.getBoolean("updateAvailable");

        return new StoreItem(packageName,ItemsManager.getItemsManager().getTitle(packageName).equals("N/A")? title:ItemsManager.getItemsManager().getTitle(packageName), ItemsManager.getItemsManager().getIcon(packageName), currentVersion, latestVersion, source, 
                             itemSourceType, customLink, excludedFromUpdates, downloadLink, 
                             signature, versionCode, updateAvailable);
    }*/
/*
    public static StoreConfiguration configFromJson(JSONObject json) throws JSONException {
        StoreConfiguration config = new StoreConfiguration();

        JSONArray sourcesArray = json.getJSONArray("selectedSources");
        config.selectedSources = jsonArrayToStringList(sourcesArray);

        config.defaultSource = json.getString("defaultSource");
        config.checkInstalledAppsForUpdates = json.getBoolean("checkInstalledAppsForUpdates");

        JSONArray checkListArray = json.getJSONArray("installedAppsCheckList");
        config.installedAppsCheckList = jsonArrayToStringList(checkListArray);

        config.defaultItemsSource = StoreConfiguration.DefaultSourceType.valueOf(json.getString("defaultItemsSource"));
        config.defaultItemsLink = json.optString("defaultItemsLink", null);

        return config;
    }*/

    private static List<String> jsonArrayToStringList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }
/*
    public static ExportData importDataFromJson(String jsonString) throws JSONException {
        JSONObject root = new JSONObject(jsonString);

        StoreConfiguration config = configFromJson(root.getJSONObject("storeConfiguration"));

        List<StoreItem> items = new ArrayList<StoreItem>();
        JSONArray itemsArray = root.getJSONArray("manualAndCustomItems");
        for (int i = 0; i < itemsArray.length(); i++) {
            items.add(storeItemFromJson(itemsArray.getJSONObject(i)));
        }

        // יצירת ExportData עם הנתונים המיובאים
        return new ExportData(config, items);
    }
    */

        // --- Serialization (Java -> JSON String) ---
/*
        public static String serializeStoreConfiguration(StoreConfiguration config) throws JSONException {
            JSONObject json = new JSONObject();
            json.put("checkInstalledAppsForUpdates", config.checkInstalledAppsForUpdates);
            json.put("defaultSource", config.defaultSource.name());
            json.put("defaultSourceLink", config.defaultSourceLink != null ? config.defaultSourceLink : "");

            JSONArray installedAppsCheckListJson = new JSONArray();
            for (String pn : config.installedAppsCheckList) {
                installedAppsCheckListJson.put(pn);
            }
            json.put("installedAppsCheckList", installedAppsCheckListJson);

            return json.toString();
        }*/

        public static String serializeStoreItems(List<StoreItem> items,ItemsManager itemsManager) throws JSONException {
            JSONArray array = new JSONArray();
            List<StoreItem> itemstoexport=new ArrayList<>();
           // List<StoreItem> manual=new ArrayList<>();
            JSONArray installedAppsCheckListJson = new JSONArray();
            for(StoreItem si:items){
                if(!si.itemSourceType.equals(StoreItem.ItemSourceType.INSTALLED_APP)){
                    itemstoexport.add(si);
                }else{
                    installedAppsCheckListJson.put(si.packageName);
                }
            }
            /*final List<String> currentCheckList = itemsManager.getConfigManager().getConfig().installedAppsCheckList;
            JSONObject jsoncl = new JSONObject();
            jsoncl.put("cl",currentCheckList);
            array.put(jsoncl);*/
            JSONObject jsoncl = new JSONObject();
            /*JSONArray installedAppsCheckListJson = new JSONArray();
            for (String pn : itemsManager.getConfigManager().getConfig().installedAppsCheckList) {
                installedAppsCheckListJson.put(pn);
            }*/
            jsoncl.put("cl", installedAppsCheckListJson);
            array.put(jsoncl);
            /*
            jsoncl = new JSONObject();
            installedAppsCheckListJson = new JSONArray();
            for (StoreItem si : manual) {
                installedAppsCheckListJson.put(si.packageName);
            }
            jsoncl.put("clm", installedAppsCheckListJson);
            array.put(jsoncl);
            
            jsoncl = new JSONObject();
            installedAppsCheckListJson = new JSONArray();
            for (StoreItem si : manual) {
                installedAppsCheckListJson.put(si.title);
            }
            jsoncl.put("clmti", installedAppsCheckListJson);
            array.put(jsoncl);*/
            for (StoreItem item : itemstoexport) {
                JSONObject json = new JSONObject();
                json.put("itemSourceType", item.itemSourceType.name());
                json.put("packageName", item.packageName);
                json.put("title", item.title);
                json.put("customLink", item.customLink != null ? item.customLink : "");
                json.put("isDrive", item.isDrive);
                array.put(json);
            }
            return array.toString(2);
        }
        
        // --- Deserialization (JSON String -> Java Objects) ---
/*
        public static StoreConfiguration deserializeStoreConfiguration(String jsonString) throws JSONException {
            JSONObject json = new JSONObject(jsonString);
            boolean checkInstalledAppsForUpdates = json.getBoolean("checkInstalledAppsForUpdates");
            String defaultSourceLink = json.getString("defaultSourceLink");

            StoreConfiguration.DefaultSourceType defaultSource = StoreConfiguration.DefaultSourceType.valueOf(json.getString("defaultSource"));

            List<String> installedAppsCheckList = new ArrayList<String>();
            JSONArray installedAppsCheckListJson = json.optJSONArray("installedAppsCheckList");
            if (installedAppsCheckListJson != null) {
                for (int i = 0; i < installedAppsCheckListJson.length(); i++) {
                    installedAppsCheckList.add(installedAppsCheckListJson.getString(i));
                }
            }

            return new StoreConfiguration(checkInstalledAppsForUpdates, installedAppsCheckList, defaultSource, defaultSourceLink);
        }
*/
        public static List<StoreItem> deserializeStoreItems(String jsonString, ItemsManager itemsManager) throws JSONException {
            List<StoreItem> items = new ArrayList<StoreItem>();
            JSONArray array = new JSONArray(jsonString);
            JSONObject jsoncl = array.getJSONObject(0);
            List<String> installedAppsCheckList = new ArrayList<String>();
            JSONArray installedAppsCheckListJson = jsoncl.optJSONArray("cl");
            if (installedAppsCheckListJson != null) {
                for (int i = 0; i < installedAppsCheckListJson.length(); i++) {
                    installedAppsCheckList.add(installedAppsCheckListJson.getString(i));
                    
                }
            }
            //itemsManager.getConfigManager().getConfig().installedAppsCheckList=installedAppsCheckList;
            //add it now in storeitem list
            List<PackageInfo> allInstalledApps = itemsManager.getInstalledApps(true); 
            allInstalledApps.addAll(itemsManager.getInstalledApps(false)); 
            boolean faund=true;
            /*while(faund){
             faund=false;
             for(PackageInfo pi:allInstalledApps){
             for(StoreItem si:allItems){
             if(si.itemSourceType.equals(StoreItem.ItemSourceType.CUSTOM_LINK)&&si.packageName.equals(pi.packageName)){
             allInstalledApps.remove(pi);
             faund=true;
             break;
             }
             }
             if(faund)
             break;
             }
             }*/
            for (PackageInfo packageInfo : allInstalledApps) {
                String pn = packageInfo.packageName;

                // הוספה למאגר רק אם נמצא ברשימה הלבנה
                if (installedAppsCheckList.contains(pn)) { 
                    StoreItem existingItem = itemsManager.findItemByPackageName(pn);

                    if (existingItem == null) {
                        // ... [creation and addition of newItem] ...
                        String currentVersion = itemsManager.getInstalledVersion(pn);
                        StoreItem newItem = new StoreItem(
                            pn, itemsManager.getTitle( pn),itemsManager.getIcon(pn), currentVersion, "N/A", "מערכת", 
                            StoreItem.ItemSourceType.INSTALLED_APP, "", 
                            false,false, "", "", 0, false
                        );
                        items.add(newItem); 
                    }else{
                        //is in check list but is already in the items - like custom link...
                        //if title eq pn replace to title
                        if(existingItem.title.equals(pn)){
                            items.remove(existingItem);
                            existingItem.title=itemsManager.getTitle(pn);
                            items.add(existingItem);
                        }

                    }
                }else{
                    //is old item remove now
                    StoreItem existingItem = itemsManager.findItemByPackageName(pn);
                    if(existingItem!=null&&existingItem.itemSourceType.equals(StoreItem.ItemSourceType.INSTALLED_APP)){
                        items.remove(existingItem);
                    }
                }
            }
            List<String> pns=new ArrayList<>();
            PackageInfo[] pis={};
            pis= allInstalledApps.toArray(pis); 
            for(PackageInfo pi:pis){
                pns.add(pi.packageName);
            }

            faund=true;
            while(faund){
                //isnt curect pn...
                faund=false;
                for(StoreItem sif:items){
                    if(sif.itemSourceType.equals(StoreItem.ItemSourceType.INSTALLED_APP)&&!pns.contains(sif.packageName)){
                        items.remove(sif);
                        faund=true;
                        break;
                    }
                    //is multiple...
                    for(StoreItem sis:items){
                        if(sis.equals(sif))continue;
                        if(sis.packageName.equals(sif.packageName)&&sis.itemSourceType.equals(StoreItem.ItemSourceType.INSTALLED_APP)&&sif.itemSourceType.equals(StoreItem.ItemSourceType.INSTALLED_APP)){
                            items.remove(sis);
                            faund=true;
                            break;
                            //do not break... continue removing all...
                            //break because modification list exception...
                        }
                    }
                    if(faund)
                        break;
                }

            }
            /*jsoncl = array.getJSONObject(1);
            installedAppsCheckList = new ArrayList<String>();
            installedAppsCheckListJson = jsoncl.optJSONArray("clm");
            if (installedAppsCheckListJson != null) {
                for (int i = 0; i < installedAppsCheckListJson.length(); i++) {
                    installedAppsCheckList.add(installedAppsCheckListJson.getString(i));
                }
            }
            jsoncl = array.getJSONObject(2);
            List<String> installedAppsCheckListti = new ArrayList<String>();
            installedAppsCheckListJson = jsoncl.optJSONArray("clmti");
            if (installedAppsCheckListJson != null) {
                for (int i = 0; i < installedAppsCheckListJson.length(); i++) {
                    installedAppsCheckListti.add(installedAppsCheckListJson.getString(i));
                }
            }
            for(int i=0;i<installedAppsCheckList.size();i++){
                    StoreItem newItem = new StoreItem(installedAppsCheckList.get(i), StoreItem.ItemSourceType.MANUAL, "", installedAppsCheckListti.get(i));
                items.add(newItem);
            }*/
            
            for (int i = 1; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);

                StoreItem.ItemSourceType itemSourceType = StoreItem.ItemSourceType.valueOf(json.getString("itemSourceType"));

                StoreItem item = new StoreItem(
                    itemSourceType,
                    json.getString("packageName"),
                    ItemsManager.getItemsManager().getTitle(json.getString("packageName")).equals("N/A")? json.getString("title"):ItemsManager.getItemsManager().getTitle(json.getString("packageName")),
                    json.optString("customLink", null),
                    json.optBoolean("isDrive", false)
                );
                items.add(item);
            }
            return items;
        }
    
// ... [existing imports] ...

//    public class JsonUtil {

        // --- Serialization (Java -> JSON String) ---

        public static String serializeStoreConfiguration(StoreConfiguration config) throws JSONException {
            JSONObject json = new JSONObject();
      //      json.put("checkInstalledAppsForUpdates", config.checkInstalledAppsForUpdates);
            //json.put("checkSystemApps", config.checkSystemApps);     // חדש
       //     json.put("checkUserApps", config.checkUserApps);         // חדש
            json.put("defaultSource", config.defaultSource.name());
            json.put("defaultSourceLink", config.defaultSourceLink != null ? config.defaultSourceLink : "");

            //JSONArray installedAppsCheckListJson = new JSONArray();
           // for (String pn : config.installedAppsCheckList) {
             //   installedAppsCheckListJson.put(pn);
            //}
           // json.put("installedAppsCheckList", installedAppsCheckListJson);

            return json.toString(2);
        }

        // --- Deserialization (JSON String -> Java Objects) ---

        public static StoreConfiguration deserializeStoreConfiguration(String jsonString) throws JSONException {
            JSONObject json = new JSONObject(jsonString);
            // שימוש ב-optBoolean עם ברירת מחדל (false) כדי לתמוך בגרסאות ישנות של קונפיגורציה
       //     boolean checkInstalledAppsForUpdates = json.optBoolean("checkInstalledAppsForUpdates", false); 
     //       boolean checkSystemApps = json.optBoolean("checkSystemApps", false);   
      //      boolean checkUserApps = json.optBoolean("checkUserApps", false);             
            String defaultSourceLink = json.getString("defaultSourceLink");

            // ... [installedAppsCheckList deserialization] ...
           /* List<String> installedAppsCheckList = new ArrayList<String>();
            JSONArray installedAppsCheckListJson = json.optJSONArray("installedAppsCheckList");
            if (installedAppsCheckListJson != null) {
                for (int i = 0; i < installedAppsCheckListJson.length(); i++) {
                    installedAppsCheckList.add(installedAppsCheckListJson.getString(i));
                }
            }*/

            StoreConfiguration.DefaultSourceType defaultSource = StoreConfiguration.DefaultSourceType.valueOf(json.getString("defaultSource"));

            return new StoreConfiguration(
                //checkInstalledAppsForUpdates, 
               // checkSystemApps,          // חדש
             //   checkUserApps,            // חדש
            //    installedAppsCheckList, 
                defaultSource, 
                defaultSourceLink
            );
        }

        // ... [rest of the file] ...
    
}
