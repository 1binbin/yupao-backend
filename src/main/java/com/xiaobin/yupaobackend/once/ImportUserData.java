package com.xiaobin.yupaobackend.once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 判断表格有多少重复昵称
 *
 * @Author: hongxiaobin
 * @Time: 2023/3/3 15:17
 */
public class ImportUserData {

    public static void main(String[] args) {
        String fileName = "E:\\Project\\yupao\\yupao_project\\yupao-backend\\excel\\userData.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserDataInfo> userInfoList =
                EasyExcel.read(fileName).head(UserDataInfo.class).sheet().doReadSync();

        System.out.println("总数 = " + userInfoList.size());
        // 将数据过滤并且按照昵称分组
        Map<String, List<UserDataInfo>> listMap =
                userInfoList.stream()
                        .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUsername()))
                        .collect(Collectors.groupingBy(UserDataInfo::getUsername));

        for (Map.Entry<String, List<UserDataInfo>> stringListEntry : listMap.entrySet()) {
            if (stringListEntry.getValue().size() > 1) {
                System.out.println("username = " + stringListEntry.getKey());
                System.out.println("1");
            }
        }
        System.out.println("不重复昵称数 = " + listMap.keySet().size());
    }
}
