package stratefy;

import config.CommonConf;
import stratefy.impl.EsStrategy;
import stratefy.impl.FileStrategy;
import stratefy.impl.MongoStrategy;
import util.log.Log;

public class StrategyFactory {
    public static Strategy getSourceStrategy() {
        Strategy strategy = getStrategy(CommonConf.SOURCE);
        if (strategy == null) {
            Log.error("资源提供者配置错误，请检查common.xml/source参数");
            System.exit(-1);
        }
        return strategy;
    }

    public static Strategy getTargetStrategy() {
        Strategy strategy = getStrategy(CommonConf.TARGET);
        if (strategy == null) {
            Log.error("资源接收者配置错误，请检查common.xml/target参数");
            System.exit(-1);
        }
        return strategy;
    }

    private static Strategy getStrategy(String strategy) {
        switch (strategy) {
            case "es": return new EsStrategy();
            case "mongo": return new MongoStrategy();
            case "file": return new FileStrategy();
            default: return null;
        }
    }
}
