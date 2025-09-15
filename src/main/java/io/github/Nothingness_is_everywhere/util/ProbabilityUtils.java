package io.github.Nothingness_is_everywhere.util;
import java.util.*;

public class ProbabilityUtils {
    private static final Random random = new Random();

    /**
     * 判断是否触发指定概率的事件
     * @param probability 概率值，范围[0, 100]表示百分比
     * @return 如果触发返回true，否则返回false
     */
    public static boolean isTriggered(double probability) {
        if (probability <= 0) {
            return false;
        }
        if (probability >= 100) {
            return true;
        }
        return random.nextDouble() * 100 < probability;
    }

    /**
     * 生成指定范围的随机整数 [min, max]
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return 随机整数
     * @throws IllegalArgumentException 如果min > max
     */
    public static int randomInRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min不能大于max");
        }
        return min + random.nextInt(max - min + 1);
    }

    /**
     * 从带权重的元素列表中随机选择一个元素
     * @param weightedItems 包含元素及其权重的列表，权重需为正数
     * @param <T> 元素类型
     * @return 随机选中的元素，如果列表为空则返回null
     * @throws IllegalArgumentException 如果权重为非正数
     */
    public static <T> T weightedRandomSelect(List<WeightedItem<T>> weightedItems) {
        if (weightedItems == null || weightedItems.isEmpty()) {
            return null;
        }

        // 计算权重总和
        double totalWeight = 0;
        for (WeightedItem<T> item : weightedItems) {
            if (item.weight() <= 0) {
                throw new IllegalArgumentException("权重必须为正数: " + item);
            }
            totalWeight += item.weight();
        }

        // 生成0到总权重之间的随机数
        double randomValue = random.nextDouble() * totalWeight;

        // 找出随机数落在哪个权重区间
        double cumulativeWeight = 0;
        for (WeightedItem<T> item : weightedItems) {
            cumulativeWeight += item.weight();
            if (randomValue < cumulativeWeight) {
                return item.item();
            }
        }

        // 理论上不会到达这里，除非出现计算误差
        return weightedItems.get(weightedItems.size() - 1).item();
    }

    /**
     * 带权重的元素封装类
     *
     * @param <T> 元素类型
     */
        public record WeightedItem<T>(T item, double weight) {

        @Override
            public String toString() {
                return "WeightedItem{item=" + item + ", weight=" + weight + "}";
            }
        }

    // 使用示例
    public static void main(String[] args) {
        // 测试暴击概率
        System.out.println("测试30%概率事件:");
        int count = 0;
        for (int i = 0; i < 10000; i++) {
            if (isTriggered(30)) {
                count++;
            }
        }
        System.out.println("触发次数: " + count + "，大约占" + (count / 100.0) + "%");

        // 测试随机选择
        List<String> fruits = Arrays.asList("苹果", "香蕉", "橙子", "葡萄");
        System.out.println("\n随机选择水果: " + fruits.get(randomInRange(0, fruits.size() - 1)));

        // 测试带权重的随机选择
        List<WeightedItem<String>> items = new ArrayList<>();
        items.add(new WeightedItem<>("普通物品", 70));
        items.add(new WeightedItem<>("稀有物品", 20));
        items.add(new WeightedItem<>("史诗物品", 9));
        items.add(new WeightedItem<>("传说物品", 1));

        System.out.println("\n测试带权重的随机选择（10000次）:");
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            String item = weightedRandomSelect(items);
            result.put(item, result.getOrDefault(item, 0) + 1);
        }
        result.forEach((k, v) -> System.out.println(k + ": " + v + "次，占" + (v / 100.0) + "%"));
    }
}

