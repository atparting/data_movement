import stratefy.Context;
import stratefy.Strategy;
import stratefy.StrategyFactory;

public class Main {
    public static void main(String[] args) {
        Strategy source = StrategyFactory.getSourceStrategy();
        Strategy target = StrategyFactory.getTargetStrategy();
        Context context = new Context(source, target);
        context.execute();
    }
}
