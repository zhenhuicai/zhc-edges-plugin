package com.zhm.edges.plugins.api.utils;

public class TimeUtils {

  /**
   * Returns a human-readable string representing the time difference between the given timestamp
   * (ms) and now. Example: "5 seconds ago", "3 minutes ago", "2 hours ago", "1 day ago"
   *
   * @param pastMillis the past timestamp in milliseconds
   * @return human-readable time difference
   */
  public static String getTimeAgo(long pastMillis) {
    long now = System.currentTimeMillis();
    long diff = now - pastMillis;
    if (diff < 0) return "in the future";
    long seconds = diff / 1000;
    if (seconds < 60) {
      return seconds + (seconds == 1 ? " second ago" : " seconds ago");
    }
    long minutes = seconds / 60;
    if (minutes < 60) {
      return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
    }
    long hours = minutes / 60;
    if (hours < 24) {
      return hours + (hours == 1 ? " hour ago" : " hours ago");
    }
    long days = hours / 24;
    return days + (days == 1 ? " day ago" : " days ago");
  }
}
