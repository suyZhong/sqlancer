package lama.mariadb.gen;

import lama.Randomly;

public class MariaDBCommon {

	public static void addWaitClause(StringBuilder sb) {
		if (Randomly.getBoolean()) {
			sb.append(" WAIT 1");
		} else if (Randomly.getBoolean()) {
			sb.append(" NOWAIT");
		}
	}

}