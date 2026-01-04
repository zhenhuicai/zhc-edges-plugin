package com.zhm.edges.plugins.api.utils;

import java.math.BigDecimal;

public class SimilarityUtils {

  public static BigDecimal calculateSimilarity(String input, String materialNo, String model) {
    String normalizedInput = input.replaceAll("\\s+", "").toLowerCase();
    String normalizedModel = model != null ? model.replaceAll("\\s+", "").toLowerCase() : "";
    String normalizedMaterialNo = materialNo != null ? materialNo.replaceAll("\\s+", "").toLowerCase() : "";

    // 检查是否完全匹配
    if (normalizedInput.equals(normalizedModel) || normalizedInput.equals(normalizedMaterialNo)) {
      return BigDecimal.ONE;
    }
    // 计算与型号的相似百分比
    double modelMatchRate = calculateSimilarity(normalizedInput, normalizedModel);
    // 计算与订货号的相似百分比
    double materialNoMatchRate = calculateSimilarity(normalizedInput, normalizedMaterialNo);
    return BigDecimal.valueOf(Math.max(modelMatchRate, materialNoMatchRate));
  }

  /**
   * 计算两个字符串的相似度（简单的字符匹配百分比）
   *
   * @param s1 第一个字符串
   * @param s2 第二个字符串
   * @return 相似度（0-1之间）
   */
  private static double calculateSimilarity(String s1, String s2) {
    if (s1.isEmpty() || s2.isEmpty()) {
      return 0.0;
    }

    // 找出较长的字符串作为基准
    String longer = s1;
    String shorter = s2;
    if (s1.length() < s2.length()) {
      longer = s2;
      shorter = s1;
    }

    int longerLength = longer.length();

    // 计算编辑距离（Levenshtein距离）
    int editDistance = calculateEditDistance(shorter, longer);

    // 计算相似度（1 - 编辑距离/较长字符串长度）
    return 1.0 - (double) editDistance / longerLength;
  }

  /**
   * 计算两个字符串之间的编辑距离（Levenshtein距离）
   *
   * @param s1 第一个字符串
   * @param s2 第二个字符串
   * @return 编辑距离
   */
  private static int calculateEditDistance(String s1, String s2) {
    int[] costs = new int[s2.length() + 1];
    for (int i = 0; i <= s1.length(); i++) {
      int lastValue = i;
      for (int j = 0; j <= s2.length(); j++) {
        if (i == 0) {
          costs[j] = j;
        } else {
          if (j > 0) {
            int newValue = costs[j - 1];
            if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
              newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
            }
            costs[j - 1] = lastValue;
            lastValue = newValue;
          }
        }
      }
      if (i > 0) {
        costs[s2.length()] = lastValue;
      }
    }
    return costs[s2.length()];
  }
}
