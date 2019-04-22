package stratefy;

import config.CommonConf;
import config.ResourceConf;
import util.log.Log;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private Strategy source;
    private Strategy target;

    public Context(Strategy source, Strategy target) {
        this.source = source;
        this.target = target;
    }

    public void execute() {
        try {
            // 初始化资源处理对象
            source.init(ResourceConf.getSourceConfig());
            target.init(ResourceConf.getTargetConfig());
            // 待处理资源集合
            List<String> sourceResList = new ArrayList<>();
            List<String> targetResList = new ArrayList<>();
            // 匹配获得待处理资源
            boolean matchAll = "*".equals(CommonConf.RESOURCES[0]);
            for (String resName : source.allResourceNames()) {
                if (matchAll) {
                    String sourceRes = source.sourceResource(resName, "*");
                    if (sourceRes != null) {
                        String targetRes = target.targetResource(resName, "*");
                        sourceResList.add(sourceRes);
                        targetResList.add(targetRes);
                    }
                } else {
                    for (String resConf : CommonConf.RESOURCES) {
                        String sourceRes = source.sourceResource(resName, resConf);
                        if (sourceRes != null) {
                            String targetRes = target.targetResource(resName, resConf);
                            sourceResList.add(sourceRes);
                            targetResList.add(targetRes);
                        }
                    }
                }

            }
            // 进行资源数据导出导入
            for (int i = 0; i < sourceResList.size(); i++) {
                String sourceRes = sourceResList.get(i);
                String targetRes = targetResList.get(i);
                Log.info("[" + (i + 1) + "/" + sourceResList.size() + "] " +
                        "source = " + sourceRes + ", target = " + targetRes);
                try {
                    int total = executeOne(sourceRes, targetRes);
                    Log.info("movement success, total = " + total);
                } catch (Exception e) {
                    Log.info("movement failure, " + e.toString());
                }
            }
        } catch (Exception e) {
            Log.error("策略执行上下文执行失败, " + e.toString());
            e.printStackTrace();
        } finally {
            source.close();
            target.close();
        }
    }

    /**
     * 执行单个资源的导出导入
     * @param sourceRes 提供者资源唯一标识
     * @param targetRes 接收者资源唯一标识
     * @return 成功导出导入资源数据数量
     */
    private int executeOne(String sourceRes, String targetRes) {
        source.clear();
        target.clear();
        // 创建或跳过资源
        if (target.resourceExists(targetRes)) {
            if ("cover".equals(CommonConf.ALREADY_EXISTS)) {
                Log.warn("资源" + targetRes + "已存在，开始覆盖，若需跳过/追加请修改common.xml/already_exists参数");
                if (!target.createNewResource(targetRes)) {
                    Log.error("资源" + targetRes + "创建失败");
                    return 0;
                }
            } else if ("append".equals(CommonConf.ALREADY_EXISTS)) {
                Log.warn("资源" + targetRes + "已存在，开始追加，若需覆盖/跳过请修改common.xml/already_exists参数");
            } else {
                Log.warn("资源" + targetRes + "已存在，已跳过，若需覆盖/追加请修改common.xml/already_exists参数");
                return 0;
            }
        } else {
            if (!target.createNewResource(targetRes)) {
                Log.error("资源" + targetRes + "创建失败");
                return 0;
            }
        }
        int total = 0;
        int size;
        do {
            List<String> list = source.batchGet(sourceRes);
            if (list.size() == 0)
                break;
            size = target.batchSet(targetRes, list);
            total += size;
            if (total % (CommonConf.BATCH_NUM * 5) == 0)
                Log.debug("total = " + total);
        } while (size >= CommonConf.BATCH_NUM);
        return total;
    }
}
