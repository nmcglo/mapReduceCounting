package mapreduce;

import java.io.Serializable;

public class Pair implements Serializable {
  public Object key;
  public Object value;
  public Pair(Object key, Object value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public boolean equals(Object object)
  {
    boolean result = false;
    Pair otherPair = (Pair) object;

    if((String) this.key == (String) otherPair.key)
    {
      if((int) this.value == (int) otherPair.value)
      {
        result = true;
      }
    }

    return result;
  }

  @Override
  public int hashCode()
  {
    String newString = this.key.toString() + this.value.toString();
    return newString.hashCode();
  }

  public String toString()
  {
  	return String.format("(" + this.key + ", " + this.value + ")");
  }
}

