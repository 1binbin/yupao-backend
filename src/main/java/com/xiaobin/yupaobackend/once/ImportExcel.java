package com.xiaobin.yupaobackend.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 测试导入Excel
 *
 * @Author: hongxiaobin
 * @Time: 2023/3/3 15:03
 */
public class ImportExcel {

    /**
     * 读取数据
     */
    public static void main(String[] args) {
        // 写法1：JDK8+
        // since: 3.0.0-beta1
        // Excel绝对路径
        String fileName = "E:\\Project\\yupao\\yupao_project\\yupao-backend\\excel\\userData.xlsx";
        synchronousRead(fileName);
    }

    /**
     * 监听器读取
     *
     * @param fileName 文件名
     */
    public static void readByListener(String fileName) {
        EasyExcel.read(fileName, UserDataInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步读
     *
     * @param fileName 文件名
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserDataInfo> totalDataList =
                EasyExcel.read(fileName).head(UserDataInfo.class).sheet().doReadSync();
        for (UserDataInfo userDataInfo : totalDataList) {
            System.out.println(userDataInfo);
        }
    }

}
