package stratefy;

import config.ResourceConf;

import java.io.Closeable;
import java.util.List;

public interface Strategy extends Closeable {

    /**
     * 初始化数据库连接等
     * 请在init中执行clear()
     * @param config xml配置类
     */
    void init(ResourceConf config);

    /**
     * 清空分页信息/es滚动ID等
     * 根据具体需求编写
     */
    void clear();

    /**
     * 批量滚动获取数据
     * @param resource 资源唯一标识
     * @return 数据json集合
     */
    List<String> batchGet(String resource);

    /**
     * 批量写入数据
     * @param resource 需要写入的资源地址唯一标识
     * @param jsonList 需要写入的数据json集合
     * @return 写入成功的条数
     */
    int batchSet(String resource, List<String> jsonList);

    /**
     * 获得所有资源唯一标识
     * @return 资源唯一标识集合
     */
    List<String> allResourceNames();

    /**
     * 资源是否存在
     * @param resource 资源唯一标识
     * @return true: 存在  false: 不存在
     */
    boolean resourceExists(String resource);

    /**
     * 创建新表 已存在则先删除 再创建
     * @param resource 资源唯一标识
     * @return true: 创建成功  false: 创建失败
     */
    boolean createNewResource(String resource);

    /**
     * 作为资源提供者 生成提供的资源的唯一标识
     * @return 提供方资源唯一标识 null: 未匹配
     */
    default String sourceResource(String resourceName, String resourceConf) {
        boolean match;
        if (resourceConf.equals("*")) {
            return resourceName;
        } else if (resourceConf.startsWith("^$")) {
            match = resourceName.startsWith(resourceConf.substring(2)) ||
                    resourceName.endsWith(resourceConf.substring(2));
        } else if (resourceConf.startsWith("^")) {
            match = resourceName.startsWith(resourceConf.substring(1));
        } else if (resourceConf.startsWith("$")) {
            match = resourceName.endsWith(resourceConf.substring(1));
        } else {
            match = resourceName.equals(resourceConf);
        }
        return match ? resourceName : null;
    }

    /**
     * 作为资源接收者 生成提供者提供的资源对应接收者的资源唯一标识
     * @param resourceName 提供者提供的资源唯一标识
     * @return 接收方资源唯一标识
     */
    default String targetResource(String resourceName, String resourceConf) {
        return resourceName;
    }

    /**
     * 关闭数据库连接
     */
    void close();
}
