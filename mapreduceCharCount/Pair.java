package mapreduce;

import java.io.Serializable;

public class Pair implements Serializable {
  public Object key;
  public Object value;
  public Pair(Object key, Object value) {
    this.key = key;
    this.value = value;
  }

  public String toString()
  {
  	return String.format("(" + this.key + ", " + this.value + ")");
  }
}

