package mapreduce;

import java.util.Vector;

public class CharCountMapReduce implements MapReduce {

	private int nMappers;
	private int nShufflers;
	private int nReducers;
	
	public CharCountMapReduce(int m, int s, int r) {
		nMappers = m;
		nShufflers = s;
		nReducers = r;
	}
	
	public int nMappers() {
		return nMappers;
	}

	public int nReducers() {
		return nReducers;
	}

	public int nShufflers() {
		return nShufflers;
	}

	public Vector map(Pair p) {
		Vector ret = new Vector();
		String s = (String)p.value;
		for (int i = 0; i < s.length(); ++i)
		{
			ret.add(new Pair(s.charAt(i), 1));
		}
		return ret;
	}

	// public Pair shuffle(Vector v, Shuffle[] shufflers)
	// {
	// 	// join
	// 	// {
	// 	// 	for(int i = 0; i < shufflers.length; i++)
	// 	// 	{
	// 	// 		shufflers[i] <- get
	// 	// 	}
	// 	// }
	// 	return null;

	// }

	public Pair reduce(Pair p) {
		Vector v = (Vector)p.value;
		int sum = 0;
		for (int i = 0; i < v.size(); ++i) sum += (Integer)v.get(i);
		Vector ret = new Vector();
		ret.add(sum);
		return new Pair(p.key, ret);
	}

}

