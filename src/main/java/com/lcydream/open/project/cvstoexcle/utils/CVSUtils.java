package com.lcydream.open.project.cvstoexcle.utils;

import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by luochunyun on 2017/11/22.
 */
public class CVSUtils {

    /**
     * 导出为CVS文件
     *
     * @param exportData
     */
    public static File createCSVFile(List exportData, LinkedHashMap rowMapper, String outPutPath) {
        File csvFile = null;
        BufferedWriter csvFileOutputStream = null;
        try {
            csvFile = File.createTempFile("temp", ".csv", new File(outPutPath));
            // GB2312使正确读取分隔符","
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"),
                    1024);
            // 写入文件头部
            for (Iterator propertyIterator = rowMapper.entrySet().iterator(); propertyIterator.hasNext();) {
                java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator.next();
                csvFileOutputStream.write("\"" + propertyEntry.getValue().toString() + "\"");
                if (propertyIterator.hasNext()) {
                    csvFileOutputStream.write(",");
                }
            }
            csvFileOutputStream.newLine();
            // 写入文件内容
            for (Iterator iterator = exportData.iterator(); iterator.hasNext();) {
                Object row = (Object) iterator.next();
                for (Iterator propertyIterator = rowMapper.entrySet().iterator(); propertyIterator.hasNext();) {
                    java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator.next();
                    csvFileOutputStream.write("\""
                            + BeanUtils.getProperty(row, propertyEntry.getKey().toString()).toString() + "\"");
                    if (propertyIterator.hasNext()) {
                        csvFileOutputStream.write(",");
                    }
                }
                if (iterator.hasNext()) {
                    csvFileOutputStream.newLine();
                }
            }
            csvFileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     * 导出为CSV文件
     *
     * @param response
     * @param exportData
     * @param fileName
     * @param outputPath
     * @throws FileNotFoundException
     */
    public static void exportToCSVFile(HttpServletResponse response, List exportData, LinkedHashMap rowMapper,
                                       String fileName, String outputPath) throws FileNotFoundException {
        File csvFile = createCSVFile(exportData, rowMapper, outputPath);
        //FileUtils.downFile(response, fileName, new FileInputStream(csvFile));
        csvFile.delete();
    }

    public static void main(String[] args) {
        List exportData = new ArrayList<Map>();
        Map row1 = new LinkedHashMap<String, String>();
        row1.put("1", "11");
        row1.put("2", "12");
        row1.put("3", "13");
        row1.put("4", "14");
        exportData.add(row1);
        row1 = new LinkedHashMap<String, String>();
        row1.put("1", "21");
        row1.put("2", "22");
        row1.put("3", "23");
        row1.put("4", "24");
        exportData.add(row1);
        List propertyNames = new ArrayList();
        LinkedHashMap map = new LinkedHashMap();
        map.put("1", "第一列");
        map.put("2", "第二列");
        map.put("3", "第三列");
        map.put("4", "第四列");
        CVSUtils.createCSVFile(exportData, map, "c:/");
    }
}
