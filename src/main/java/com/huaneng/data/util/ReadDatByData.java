package com.huaneng.data.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReadDatByData {
    public static void main(String[] args) {
        // try (BufferedReader br = new BufferedReader(
        // new FileReader("C:\\Users\\mingr\\Desktop\\1-3阶段\\PACK-WORKFLOW-bak\\55a1829da469f92f~\\service.dat"))) {
        // String line;
        // while ((line = br.readLine()) != null) {
        // System.out.println(line);
        // }
        //
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
        //

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream("C:\\Users\\mingr\\Desktop\\1-3阶段\\PACK-WORKFLOW-bak\\55a1829da469f92f~\\service.dat"),
            StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
