package com.mdm.activities;

import java.util.List;
import java.util.ArrayList;

public class ExportData {
    public final String version = "1.0";
    public final long exportTimestamp = System.currentTimeMillis();

    // ההגדרות הנוכחיות
    public final StoreConfiguration storeConfiguration;

    // רשימת הפריטים שנוספו ידנית או מלינק
    // (פריטי INSTALLED_APP לא נשמרים כאן, אלא רק כללי הבדיקה שלהם בהגדרות)
    public final List<StoreItem> manualAndCustomItems; 

    public ExportData(StoreConfiguration config, List<StoreItem> items) {
        this.storeConfiguration = config;
        this.manualAndCustomItems = new ArrayList<StoreItem>();

        // סינון רק של פריטים שנוספו ידנית/מיובאו
        for (StoreItem item : items) {
            if (item.itemSourceType != StoreItem.ItemSourceType.INSTALLED_APP) {
                manualAndCustomItems.add(item);
            }
        }
    }
}
