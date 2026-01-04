package com.mdm.activities;


import java.util.List;
import java.util.ArrayList;

/**
 * מודל הגדרות המערכת.
 */
public class StoreConfiguration {
   /* public boolean checkInstalledAppsForUpdates;
    public final List<String> installedAppsCheckList;
    public final DefaultSourceType defaultSource;
    public final String defaultSourceLink;

    public enum DefaultSourceType {
        LOCAL, LINK
        }*/
/*
    public StoreConfiguration(boolean checkInstalledAppsForUpdates, List<String> installedAppsCheckList, DefaultSourceType defaultSource, String defaultSourceLink) {
        this.checkInstalledAppsForUpdates = checkInstalledAppsForUpdates;
        this.installedAppsCheckList = installedAppsCheckList != null ? installedAppsCheckList : new ArrayList<String>();
        this.defaultSource = defaultSource;
        this.defaultSourceLink = defaultSourceLink;
    }*/
/*
    // בנאי ברירת מחדל
    public StoreConfiguration() {
        this(true, new ArrayList<String>(), DefaultSourceType.LOCAL, null);
    }*/
    

    /**
     * מודל הגדרות המערכת.
     */

     //   public boolean checkInstalledAppsForUpdates; // דגל ישן (נשמר לתאימות)
       // public boolean checkSystemApps;           // חדש: בדיקת אפליקציות מערכת
      //  public boolean checkUserApps;             // חדש: בדיקת אפליקציות משתמש
     /*   public final List<String> installedAppsCheckList;
        public final DefaultSourceType defaultSource;
        public final String defaultSourceLink;

        public enum DefaultSourceType {
            LOCAL, LINK
            }
*/
        public StoreConfiguration( DefaultSourceType defaultSource, String defaultSourceLink) {
            //this.checkInstalledAppsForUpdates = checkInstalledAppsForUpdates;
            //this.checkSystemApps = checkSystemApps;
            //this.checkUserApps = checkUserApps;
          //  this.installedAppsCheckList = installedAppsCheckList != null ? installedAppsCheckList : new ArrayList<String>();
            this.defaultSource = defaultSource;
            this.defaultSourceLink = defaultSourceLink;
        }
/*
        // בנאי ברירת מחדל: כל הבדיקות הגלובליות כבויות (false)
        public StoreConfiguration() {
            this(false, false, false, new ArrayList<String>(), DefaultSourceType.LOCAL, null); 
        }*/
    
        //public final boolean checkInstalledAppsForUpdates; // דגל זה מציין האם לבדוק אפליקציות מותקנות כלל (אם הרשימה אינה ריקה)
       // public List<String> installedAppsCheckList; // הרשימה הספציפית של חבילות לבדיקה
        public final DefaultSourceType defaultSource;
        public final String defaultSourceLink;

        public enum DefaultSourceType {
            LOCAL, LINK
            }

        // בנאי מעודכן - הדגלים checkSystemApps ו-checkUserApps הוסרו
        public StoreConfiguration(boolean checkInstalledAppsForUpdates, List<String> installedAppsCheckList, DefaultSourceType defaultSource, String defaultSourceLink) {
            //this.checkInstalledAppsForUpdates = checkInstalledAppsForUpdates;
           // this.installedAppsCheckList = installedAppsCheckList != null ? installedAppsCheckList : new ArrayList<String>();
            this.defaultSource = defaultSource;
            this.defaultSourceLink = defaultSourceLink;
        }

        // בנאי ברירת מחדל
        public StoreConfiguration() {
            this(false, new ArrayList<String>(), DefaultSourceType.LOCAL, null); 
        }
    
}
