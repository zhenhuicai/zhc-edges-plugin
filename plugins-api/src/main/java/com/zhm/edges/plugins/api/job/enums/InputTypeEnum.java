package com.zhm.edges.plugins.api.job.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum InputTypeEnum {
  NA(-1, "Default Placeholder Should NEVER be used", "默认占位枚举请勿使用"),

  TEXT(0, "text", "文本"),

  IMAGE(1, "image", "图片"),

  EXCEL(2, "excel", "excel");

  public static final List<String> VALUES;

  public static final List<InputTypeEnum> ENUMS;

  public static final List<Integer> CODES;

  public static final Map<String, InputTypeEnum> NAME_2_ENUM_MAP;

  public static final Map<Integer, InputTypeEnum> CODE_2_ENUM_MAP;

  static {
    List<String> _VALUES = new ArrayList<>();
    List<InputTypeEnum> _ENUMS = new ArrayList<>();
    List<Integer> _CODES = new ArrayList<>();
    Map<String, InputTypeEnum> _NAME_2_ENUM_MAP = new LinkedHashMap<>();
    Map<Integer, InputTypeEnum> _CODE_2_ENUM_MAP = new LinkedHashMap<>();
    for (InputTypeEnum each : InputTypeEnum.values()) {
      _VALUES.add(each.title());
      _ENUMS.add(each);
      _CODES.add(each.code());
      _NAME_2_ENUM_MAP.put(each.title(), each);
      _CODE_2_ENUM_MAP.put(each.code(), each);
    }
    VALUES = Collections.unmodifiableList(_VALUES);
    ENUMS = Collections.unmodifiableList(_ENUMS);
    CODES = Collections.unmodifiableList(_CODES);
    NAME_2_ENUM_MAP = Collections.unmodifiableMap(_NAME_2_ENUM_MAP);
    CODE_2_ENUM_MAP = Collections.unmodifiableMap(_CODE_2_ENUM_MAP);
  }

  public final int code;

  public final String description;

  public final String description2;

  public final boolean deprecated;

  InputTypeEnum(int code, String description, String description2, boolean deprecated) {
    this.code = code;
    this.description = description;
    this.description2 = description2;
    this.deprecated = deprecated;
  }

  InputTypeEnum(int code, String description, String description2) {
    this(code, description, description2, false);
  }

  public String title() {
    return name();
  }

  public int code() {
    return code;
  }

  public String description() {
    return description;
  }

  public String description2() {
    return description2 != null ? description2 : description();
  }

  public boolean deprecated() {
    return deprecated;
  }

  public List<InputTypeEnum> supportEnumerations() {
    return ENUMS;
  }

  public List<Integer> supportCodes() {
    return CODES;
  }

  public List<String> supportValues() {
    return VALUES;
  }

  public InputTypeEnum mapFromCode(int code) {
    if (this.code == code) {
      return this;
    }

    return CODE_2_ENUM_MAP.getOrDefault(code, NA);
  }

  public InputTypeEnum mapFromName(String name) {
    if (name == null || name.isEmpty()) {
      return NA;
    }

    return NAME_2_ENUM_MAP.getOrDefault(name, NA);
  }
}
