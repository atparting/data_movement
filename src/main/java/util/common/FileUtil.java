package util.common;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import util.Path;
import util.log.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * 递归获得指定路径下的所有文件
     * @param path 文件/文件夹路径
     * @return 路径下的所有文件
     */
    public static List<File> getAllFile(String path) {
        File file = new File(path);
        List<File> fileList = new ArrayList<>();
        if (file.isDirectory())
            return getAllFile(file, fileList);
        else {
            fileList.add(file);
            return fileList;
        }
    }

    private static List<File> getAllFile(File directory, List<File> fileList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File fileChild : files) {
                if (fileChild.isDirectory()) {
                    getAllFile(fileChild, fileList);
                } else {
                    fileList.add(fileChild);
                }
            }
        }
        return fileList;
    }

    /**
     * 创建文件 若存在则覆盖
     * @param path 文件路径
     * @return true: 创建成功 false: 创建失败
     */
    public static boolean createNewFile(String path) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (file.exists() && !file.delete()) {
            return false;
        }
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获得xml配置文件
     * @param fileName 文件名
     * @return xml配置文件信息
     */
    public static XMLConfiguration getXmlConfig(String fileName)  {
        URL resource = FileUtil.class.getClassLoader().getResource(fileName);
        XMLConfiguration configuration = null;
        try {
            configuration = new XMLConfiguration(resource);
        } catch (ConfigurationException e) {
            if (resource == null) {
                Log.error("load configuration fail. resource \"" + fileName + "\" not exists");
            } else {
                Log.error("load configuration fail. please check \"" + resource.getPath() + "\" is exists");
            }
            System.exit(1);
        }
        return configuration;
    }
}
