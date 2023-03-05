package com.xiaobin.yupaobackend.model.enums;

import org.apache.xmlbeans.impl.regex.REUtil;

/**
 * 队伍状态常量
 *
 * @Author hongxiaobin
 * @Time 2023/3/5-20:01
 */
public enum TeamStatusEnum {
    /**
     * 公开状态
     */
    PUBLIC(0, "公开"),
    /**
     * 私有状态
     */
    PRIVATE(1, "私有"),
    /**
     * 私密状态
     */
    SECRET(2, "私密");

    private int value;
    private String text;

    /**
     * 根据标签查找枚举值
     * @param value 队伍状态
     * @return 枚举
     */
    public static TeamStatusEnum getTeamStatusByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] teamStatusEnums = TeamStatusEnum.values();
        for (TeamStatusEnum statusEnum : teamStatusEnums) {
            if (statusEnum.getValue() == value) {
                return statusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
