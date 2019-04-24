package stratefy.impl;

import config.CommonConf;
import config.ResourceConf;
import stratefy.Strategy;
import util.common.FileUtil;
import util.log.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStrategy implements Strategy {

    private ResourceConf conf;
    private BufferedReader in;
    private BufferedWriter out;
    private Map<String, String> absolutePath;

    @Override
    public void init(ResourceConf conf) {
        this.conf = conf;
        absolutePath = new HashMap<>();
        clear();
    }

    @Override
    public void clear() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                Log.error("文件输入流关闭失败");
            }
            in = null;
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                Log.error("文件输出流关闭失败");
            }
            out = null;
        }
    }

    @Override
    public List<String> batchGet(String filePath) {
        List<String> jsonList = new ArrayList<>();
        try {
            if (in == null)
                in = new BufferedReader(new FileReader(filePath));
            String json;
            int size = 0;
            while ((json = in.readLine()) != null) {
                // 添加到批量中
                jsonList.add(json);
                size++;
                if (size == CommonConf.BATCH_NUM)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonList;
    }

    @Override
    public int batchSet(String filePath, List<String> jsonList) {
        int size = 0;
        try {
            out = new BufferedWriter(new FileWriter(filePath, true));
            for (String json : jsonList) {
                out.write(json + "\r\n");
                size++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    @Override
    public List<String> allResourceNames() {
        List<File> allFile = FileUtil.getAllFile(conf.getFilePath());
        List<String> fileNameList = new ArrayList<>(allFile.size());
        for (File file : allFile) {
            fileNameList.add(file.getName());
            absolutePath.put(file.getName(), file.getAbsolutePath());
        }
        return fileNameList;
    }

    @Override
    public boolean resourceExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    @Override
    public boolean createNewResource(String filePath) {
        return FileUtil.createNewFile(filePath);
    }

    @Override
    public String sourceResource(String resourceName, String resourceConf) {
        return absolutePath.get(resourceName);
    }

    @Override
    public String targetResource(String resourceName, String resourceConf) {
        if (resourceConf.equals("*")) {
            return conf.getFilePath() + resourceName;
        }else if (resourceConf.startsWith("^$")) {
            boolean match = resourceName.startsWith(resourceConf.substring(2)) ||
                    resourceName.endsWith(resourceConf.substring(2));
            return match ? conf.getFilePath() + resourceConf.substring(2) + "/" + resourceName : null;
        } else if (resourceConf.startsWith("^")) {
            boolean match = resourceName.startsWith(resourceConf.substring(1));
            return match ? conf.getFilePath() + resourceConf.substring(1) + "/" + resourceName : null;
        } else if (resourceConf.startsWith("$")) {
            boolean match = resourceName.endsWith(resourceConf.substring(1));
            return match ? conf.getFilePath() + resourceConf.substring(1) + "/" + resourceName : null;
        } else {
            boolean match = resourceName.equals(resourceConf);
            return match ? conf.getFilePath() + resourceName : null;
        }
    }

    @Override
    public void close() {
        clear();
    }
}
