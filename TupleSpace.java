import java.util.HashMap;
import java.util.Map;

public class TupleSpace {
    private final Map<String, String> tupleMap = new HashMap<>();

    // 读取操作
    public String read(String key) {
        return tupleMap.get(key);
    }

    // 获取并删除操作
    public String get(String key) {
        return tupleMap.remove(key);
    }

    // 插入操作
    public int put(String key, String value) {
        if (tupleMap.containsKey(key)) {
            return 1;
        }
        tupleMap.put(key, value);
        return 0;
    }
}