package koda.handlers;

public class IDManager {

	private int[] id_table;
	
	public IDManager(int size) {
		id_table = new int[size + 1];
	}
	
	public int allocateId() {
		for (int i = 1; i < id_table.length; i++) {
			if (!isIdAllocated(i)) {
				id_table[i] = 1;
				return i;
			}
		}
		return 0;
	}
	
	public void releaseId(int i) {
		id_table[i] = 0;
	}
	
	public boolean isIdAllocated(int i) {
		return id_table[i] == 1;
	}
}
