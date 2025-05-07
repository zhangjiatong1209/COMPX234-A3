import java.util.Objects;

public class Tuple {
      // The key of the tuple. It is declared as final, meaning it cannot be changed after initialization.
    private final String key;
    private String value;

// Constructor to initialize a new tuple with a given key and value.
    public Tuple(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // Getter method to retrieve the key of the tuple.
    public String getKey() {
        return key;
    }

     // Getter method to retrieve the value of the tuple.
    public String getValue() {
        return value;
    }

    // Setter method to update the value of the tuple
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return Objects.equals(key, tuple.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}