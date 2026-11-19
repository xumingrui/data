package com.huaneng.data.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ReadFiles {

    // 常见的二进制文件扩展名
    private static final String[] BINARY_EXTENSIONS =
        {".exe", ".dll", ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".ico", ".pdf", ".doc", ".docx", ".xls", ".xlsx",
            ".ppt", ".pptx", ".zip", ".rar", ".7z", ".jar", ".class", ".mp3", ".mp4", ".avi", ".mov"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入目录路径: ");
        String directoryPath = scanner.nextLine().trim();

        System.out.print("请输入要搜索的文字: ");
        String searchText = scanner.nextLine().trim();

        if (directoryPath.isEmpty() || searchText.isEmpty()) {
            System.err.println("目录路径和搜索文字不能为空");
            return;
        }

        try {
            System.out.println("正在搜索...");
            SearchResults results = findFilesContainingText(directoryPath, searchText);

            if (results.matchingFiles.isEmpty()) {
                System.out.println("未找到包含 \"" + searchText + "\" 的文件");
            } else {
                System.out.println("找到 " + results.matchingFiles.size() + " 个包含 \"" + searchText + "\" 的文件:");
                results.matchingFiles.forEach(path -> System.out.println("  " + path.toAbsolutePath().toString()));
            }

            if (results.skippedFiles > 0) {
                System.out.println("\n提示: 已跳过 " + results.skippedFiles + " 个二进制文件或无法读取的文件");
            }

        } catch (IOException e) {
            System.err.println("搜索过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * 搜索结果封装类
     */
    static class SearchResults {
        List<Path> matchingFiles;
        int skippedFiles;

        SearchResults(List<Path> matchingFiles, int skippedFiles) {
            this.matchingFiles = matchingFiles;
            this.skippedFiles = skippedFiles;
        }
    }

    /**
     * 查找包含指定文字的文件
     * 
     * @param directoryPath
     *            目录路径
     * @param searchText
     *            要搜索的文字
     * @return 搜索结果
     * @throws IOException
     *             IO异常
     */
    public static SearchResults findFilesContainingText(String directoryPath, String searchText) throws IOException {
        Path startPath = Paths.get(directoryPath);

        if (!Files.exists(startPath)) {
            throw new IOException("目录不存在: " + directoryPath);
        }

        if (!Files.isDirectory(startPath)) {
            throw new IOException("指定路径不是目录: " + directoryPath);
        }

        final int[] skippedCount = {0};

        List<Path> matchingFiles = Files.walk(startPath).filter(Files::isRegularFile).filter(path -> {
            if (isBinaryFile(path)) {
                skippedCount[0]++;
                return false;
            }
            return true;
        }).filter(path -> safeContainsText(path, searchText, skippedCount)).collect(Collectors.toList());

        return new SearchResults(matchingFiles, skippedCount[0]);
    }

    /**
     * 判断是否为二进制文件
     * 
     * @param filePath
     *            文件路径
     * @return 如果是二进制文件返回true
     */
    private static boolean isBinaryFile(Path filePath) {
        String fileName = filePath.toString().toLowerCase();
        for (String ext : BINARY_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }

        // 对于未知类型的文件，检查文件头
        try {
            return isBinaryByContent(filePath);
        } catch (IOException e) {
            return false; // 出现异常时假设不是二进制文件
        }
    }

    /**
     * 通过文件内容判断是否为二进制文件
     * 
     * @param filePath
     *            文件路径
     * @return 如果是二进制文件返回true
     * @throws IOException
     *             IO异常
     */
    private static boolean isBinaryByContent(Path filePath) throws IOException {
        // 读取文件前几个字节进行判断
        byte[] buffer = new byte[1024];
        int bytesRead = Files.newInputStream(filePath).read(buffer);

        if (bytesRead <= 0) {
            return false;
        }

        // 检查是否包含null字节或其他控制字符
        for (int i = 0; i < bytesRead; i++) {
            if (buffer[i] == 0) { // null字节通常表示二进制文件
                return true;
            }
        }

        return false;
    }

    /**
     * 安全地检查文件内容是否包含指定文字
     * 
     * @param filePath
     *            文件路径
     * @param searchText
     *            要搜索的文字
     * @param skippedCount
     *            跳过的文件计数器
     * @return 如果包含返回true，否则返回false
     */
    private static boolean safeContainsText(Path filePath, String searchText, int[] skippedCount) {
        try {
            // 先尝试使用UTF-8编码读取
            return Files.lines(filePath, StandardCharsets.UTF_8).anyMatch(line -> line.contains(searchText));
        } catch (java.nio.charset.MalformedInputException e) {
            // 编码问题，尝试其他方式
            return handleEncodingIssue(filePath, searchText);
        } catch (Exception e) {
            // 其他异常，记录但不中断搜索
            System.err.println("跳过文件: " + filePath + " - " + e.getMessage());
            skippedCount[0]++;
            return false;
        }
    }

    /**
     * 处理编码问题
     * 
     * @param filePath
     *            文件路径
     * @param searchText
     *            要搜索的文字
     * @return 如果包含返回true，否则返回false
     */
    private static boolean handleEncodingIssue(Path filePath, String searchText) {
        try {
            // 尝试使用系统默认编码
            return Files.lines(filePath).anyMatch(line -> line.contains(searchText));
        } catch (Exception e1) {
            try {
                // 最后的尝试：使用ISO-8859-1编码
                return Files.lines(filePath, StandardCharsets.ISO_8859_1).anyMatch(line -> line.contains(searchText));
            } catch (Exception e2) {
                // 如果所有方式都失败，跳过该文件
                System.err.println("跳过文件: " + filePath + " - 无法以文本方式读取");
                return false;
            }
        }
    }
}
