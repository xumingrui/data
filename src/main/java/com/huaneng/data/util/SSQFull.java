package com.huaneng.data.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SSQFull {
    public static void main(String[] args) throws IOException {
        int minSum = 95, maxSum = 98;

        // 按和值统计红球组合数
        int[] sumCount = new int[maxSum + 1];
        int redTotal = 0;

        // 导出文件（红球 + 全部蓝球 1-16，生成完整投注）
        BufferedWriter writer = new BufferedWriter(new FileWriter("ssq_full.txt"));

        for (int a = 1; a <= 28; a++) {
            for (int b = a + 1; b <= 29; b++) {
                for (int c = b + 1; c <= 30; c++) {
                    for (int d = c + 1; d <= 31; d++) {
                        for (int e = d + 1; e <= 32; e++) {
                            for (int f = e + 1; f <= 33; f++) {
                                int sum = a + b + c + d + e + f;
                                if (sum < minSum || sum > maxSum)
                                    continue;

                                sumCount[sum]++;
                                redTotal++;
                                writer.write(
                                    String.format("%02d %02d %02d %02d %02d %02d  (和=%d)%n", a, b, c, d, e, f, sum));
                            }
                        }
                    }
                }
            }
        }
        writer.close();

        // 打印分组统计
        System.out.println("===== 红球按和值分组统计 =====");
        for (int s = minSum; s <= maxSum; s++) {
            System.out.printf("和值 %d: %d 注%n", s, sumCount[s]);
        }
        System.out.println("红球组合总数: " + redTotal);
        System.out.println("完整投注(含蓝球)总数: " + (redTotal * 16L));
    }
}