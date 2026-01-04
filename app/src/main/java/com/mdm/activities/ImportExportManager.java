package com.mdm.activities;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import com.emanuelef.remote_capture.activities.LogUtil;
import java.io.FileInputStream;

public class ImportExportManager {
    private final Context context;
    private final ItemsManager itemsManager;
    private static final String EXPORT_FILE_NAME = "store_items_export.json";

    public ImportExportManager(Context context, ItemsManager itemsManager) {
        this.context = context;
        this.itemsManager = itemsManager;
    }

    // --- Export Logic ---
    
    /**
     * מייצא את הפריטים הנוכחיים לקובץ JSON בתיקיית Downloads.
     * נדרשת הרשאת WRITE_EXTERNAL_STORAGE במערכות ישנות.
     * @return הנתיב המוחלט לקובץ, או null במקרה של כשל.
     */
    public String exportItemsToJson() {
        try {
            //only if manuall or custom link.. else export to array installed system & user
            //List<StoreItem> itemstoexport=new ArrayList<>();
            
            
            List<StoreItem> items = itemsManager.getAllItems();
            /*
            for(StoreItem si:items){
                if(!si.itemSourceType.equals(StoreItem.ItemSourceType.INSTALLED_APP)){
                    itemstoexport.add(si);
                }
            }*/
            
            String jsonString = JsonUtil.serializeStoreItems(items,itemsManager);
           
            
            // שימוש ב-Environment.DIRECTORY_DOWNLOADS (דורש טיפול בהרשאות)
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            File exportFile = new File(downloadsDir, EXPORT_FILE_NAME);
            
            FileOutputStream fos = new FileOutputStream(exportFile);
            fos.write(jsonString.getBytes("UTF-8"));
            fos.close();
            
            return exportFile.getAbsolutePath();
            
        } catch (final Exception e) {
            LogUtil.logToFile("ImportExportManager"+"Error exporting items: " + e.getMessage());
            // ריצה על ה-UI Thread במקרה של שגיאה לדיווח
            if (context instanceof Activity) {
                 ((Activity) context).runOnUiThread(new Runnable() {
                     public void run() {
                         Toast.makeText(context, "שגיאה בייצוא: " + e.getMessage(), Toast.LENGTH_LONG).show();
                     }
                 });
            }
            return null;
        }
    }
    public String exportItemsToJsonFile(String path) {
        try {
            //only if manuall or custom link.. else export to array installed system & user
            //List<StoreItem> itemstoexport=new ArrayList<>();


            List<StoreItem> items = itemsManager.getAllItems();
            /*
             for(StoreItem si:items){
             if(!si.itemSourceType.equals(StoreItem.ItemSourceType.INSTALLED_APP)){
             itemstoexport.add(si);
             }
             }*/

            String jsonString = JsonUtil.serializeStoreItems(items,itemsManager);


            
            File exportFile = new File(path, EXPORT_FILE_NAME);

            FileOutputStream fos = new FileOutputStream(exportFile);
            fos.write(jsonString.getBytes("UTF-8"));
            fos.close();

            return exportFile.getAbsolutePath();

        } catch (final Exception e) {
            LogUtil.logToFile("ImportExportManager"+"Error exporting items: " + e.getMessage());
            // ריצה על ה-UI Thread במקרה של שגיאה לדיווח
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "שגיאה בייצוא: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            }
            return null;
        }
    }
    // --- Import Logic ---

    /**
     * קורא נתוני JSON מ-Uri (מבוחר קבצים) ומייבא את הפריטים.
     * @param uri ה-URI של הקובץ לייבוא.
     * @param mode מצב הייבוא (RESET_AND_ADD או ONLY_ADD).
     * @return true בהצלחה, false בכישלון.
     */
    public boolean importItemsFromJsonUri(Uri uri, Dialogs.ImportMode mode) {
        try {
            String jsonString = readTextFromUri(uri);
            if (jsonString.isEmpty()) {
                Log.e("ImportExportManager", "Import file is empty.");
                return false;
            }
            if (mode == Dialogs.ImportMode.RESET_AND_ADD) {
                itemsManager.clearAllItems(false);
            }
            
            List<StoreItem> importedItems = JsonUtil.deserializeStoreItems(jsonString,itemsManager);
            
            
            
            // הוספת פריטים חדשים, תוך התעלמות מאלו שכבר קיימים ברשימה (ALWAYS_IGNORE_EXISTING = true)
            itemsManager.addItems(importedItems, true); 
            
            return true;
            
        } catch (Exception e) {
            Log.e("ImportExportManager", "Error importing items: " + e.getMessage());
            return false;
        }
    }
    public boolean importItemsFromJsonFile(String uri, Dialogs.ImportMode mode) {
        try {
            String jsonString = readTextFromFile(uri);
            if (jsonString.isEmpty()) {
                Log.e("ImportExportManager", "Import file is empty.");
                return false;
            }
            if (mode == Dialogs.ImportMode.RESET_AND_ADD) {
                itemsManager.clearAllItems(false);
            }

            List<StoreItem> importedItems = JsonUtil.deserializeStoreItems(jsonString,itemsManager);



            // הוספת פריטים חדשים, תוך התעלמות מאלו שכבר קיימים ברשימה (ALWAYS_IGNORE_EXISTING = true)
            itemsManager.addItems(importedItems, true); 

            return true;

        } catch (Exception e) {
            Log.e("ImportExportManager", "Error importing items: " + e.getMessage());
            return false;
        }
    }
    private String readTextFromUri(Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) throw new Exception("Failed to open input stream.");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        
        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
    private String readTextFromFile(String uri) throws Exception {
        InputStream inputStream = new FileInputStream(uri);
        if (inputStream == null) throw new Exception("Failed to open input stream.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
}
