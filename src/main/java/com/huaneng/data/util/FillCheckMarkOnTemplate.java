package com.huaneng.data.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import com.huaneng.data.entity.SsqHistory;

public class FillCheckMarkOnTemplate {
    @Value("${excel.output-dir:./output}")
    private String outputDir;

    public static void fillData(List<SsqHistory> dataList, String type, OutputStream bos) throws Exception {
        ClassPathResource resource = new ClassPathResource("excel/template.xlsx");
        InputStream is = resource.getInputStream();
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);

        Map<Short, CellStyle> styleCache = new HashMap<>();

        // 遍历 List：第 n 条（0基）-> 第 n+1 列（0基索引 n+1）
        for (int n = 0; n < dataList.size(); n++) {
            int colIndex = n + 1; // 第 n+1 列：n=0 -> 列索引1(B列)
            sheet.setColumnWidth(colIndex, 6 * 256);
            SsqHistory ssqHistory = dataList.get(n);// 值 m
            Row headRow = sheet.getRow(1);

            Cell headCell = headRow.getCell(colIndex);
            CellStyle oldStyle = headCell.getCellStyle();
            CellStyle newStyle = workbook.createCellStyle();
            newStyle.cloneStyleFrom(oldStyle);
            newStyle.setWrapText(true);
            headCell.setCellValue(ssqHistory.getNlyear() + type);
            headCell.setCellStyle(newStyle);

            Integer red1 = Integer.parseInt(ssqHistory.getRed1());
            Integer red2 = Integer.parseInt(ssqHistory.getRed2());
            Integer red3 = Integer.parseInt(ssqHistory.getRed3());
            Integer red4 = Integer.parseInt(ssqHistory.getRed4());
            Integer red5 = Integer.parseInt(ssqHistory.getRed5());
            Integer red6 = Integer.parseInt(ssqHistory.getRed6());
            List<Integer> list = Arrays.asList(red1, red2, red3, red4, red5, red6);
            for (Integer num : list) {
                Row row = sheet.getRow(num + 1);
                Cell cell = row.getCell(colIndex);
                cell.setCellValue(num);

            }
        }
        // 写出
        workbook.write(bos);
        bos.flush();
    }

    public static void main(String[] args) throws Exception {
        String templatePath = "template.xlsx";
        String outPath = "result.xlsx";

        // 模拟 List 数据：第 n 条 -> 第 n+1 列，值 m -> 第 m+2 行
        // 这里用 List<Integer> 表示每条数据的值 m
        java.util.List<Integer> dataList = java.util.Arrays.asList(1, // 第1条 -> B列，值1 -> 第3行
            5, // 第2条 -> C列，值5 -> 第7行
            33, // 第3条 -> D列，值33 -> 第35行
            20 // 第4条 -> E列，值20 -> 第22行
        );

        try (InputStream is = new FileInputStream(templatePath); Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // 对号样式（居中、字体）
            CellStyle checkStyle = workbook.createCellStyle();
            checkStyle.setAlignment(HorizontalAlignment.CENTER);
            checkStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short)14);
            checkStyle.setFont(font);

            // 遍历 List：第 n 条（0基）-> 第 n+1 列（0基索引 n+1）
            for (int n = 0; n < dataList.size(); n++) {
                int colIndex = n + 1; // 第 n+1 列：n=0 -> 列索引1(B列)
                int m = dataList.get(n); // 值 m
                int rowIndex = m + 1; // 第 m+2 行：0基索引 m+1

                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    row = sheet.createRow(rowIndex);

                Cell cell = row.getCell(colIndex);
                if (cell == null)
                    cell = row.createCell(colIndex);

                cell.setCellValue("✓");
                cell.setCellStyle(checkStyle);
            }

            // 写出
            try (FileOutputStream fos = new FileOutputStream(outPath)) {
                workbook.write(fos);
            }
            System.out.println("生成成功：" + outPath);
        }
    }
}