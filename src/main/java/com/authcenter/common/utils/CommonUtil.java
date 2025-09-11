/* This project is licensed under the Mulan PSL v2.
 You can use this software according to the terms and conditions of the Mulan PSL v2.
 You may obtain a copy of Mulan PSL v2 at:
     http://license.coscl.org.cn/MulanPSL2
 THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 PURPOSE.
 See the Mulan PSL v2 for more details.
 Create: 2025
*/

package com.authcenter.common.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class CommonUtil {
    private CommonUtil() {
        throw new AssertionError("Utility class. Not intended for instantiation.");
    }

    /**
     * 随机字符串生成源.
     */
    private static final String DATA_FOR_RANDOM_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";

    private static final int DOWNLOAD_FILE_MAX_SIZE = 1024 * 1024 * 5;

    /**
     * 删除文件.
     *
     * @param path 文件路径
     * @return 如果文件删除成功则返回 true，否则返回 false
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    /**
     * 获取调用接口路径（防uri鉴权绕过）.
     *
     * @param request 请求体
     * @return uri
     */
    public static String getSafeRequestUri(HttpServletRequest request) {
        return request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
    }

    /**
     * 随机生成字符串.
     *
     * @param strLength 字符串长度
     * @return 随机字符串
     * @throws NoSuchAlgorithmException 当算法不存在时抛出异常
     */
    public static String randomStrBuilder(int strLength) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("DRBG",
                DrbgParameters.instantiation(256, DrbgParameters.Capability.RESEED_ONLY, null));
        if (strLength < 1) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder(strLength);
        for (int i = 0; i < strLength; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }

    /**
     * 下载文件.
     *
     * @param fileUrl 文件url
     * @return 文件内容
     * @throws IOException
     */
    public static String downloadFile(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 检查文件大小
        long fileSize = connection.getContentLengthLong();
        if (fileSize > DOWNLOAD_FILE_MAX_SIZE) {
            throw new IOException("File size too large: " + fileSize + " bytes. Maximum allowed: " +
                    DOWNLOAD_FILE_MAX_SIZE + " bytes.");
        }

        // 读取文件内容
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        connection.disconnect();
        return content.toString();
    }
}
