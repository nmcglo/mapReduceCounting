package mapreduce;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

public class PairWriter {

	PrintWriter pw = null;

	public PairWriter(String filename) {
		try {
			pw = new PrintWriter(new FileWriter(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writePair(Pair p) {
		pw.print(p.key);
		Vector vlist = (Vector) p.value;
		for (int i = 0; i < vlist.size(); ++i) {
			pw.print("\t" + vlist.get(i));
		}
		pw.println();
	}

	public void close() {
		pw.close();
	}
}
