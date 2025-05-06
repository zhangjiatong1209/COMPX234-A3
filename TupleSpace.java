import java.util.HashMap;
import java.util.Map;

public class TupleSpace {
    private final Map<String, String> tupleMap = new HashMap<>();
    public String read(String key) {
        return tupleMap.get(key);
    }

    public String get(String key) {
        return tupleMap.remove(key);
    }

    public int put(String key, String value) {
        if (tupleMap.containsKey(key)) {
            return 1;
        }
        tupleMap.put(key, value);
        return 0;
    }
}