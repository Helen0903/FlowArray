package source;

public class FlowArray {
	public Class<?> type = null; public Class<?>[] types = null; boolean mul;
	Object[] array = new Object[0];
	public FlowArray(Class<? extends Object> type) {
		this.type = type;
		mul = false;
	}
	
	public FlowArray() {}
	
	public FlowArray(Class<? extends Object>[] type) {
		this.types = type;
		mul = true;
	}

	public boolean addType(Class<?> type) {
		if(mul) {
			Class<?>[] temp = new Class<?>[] {type};
			types = typeMerge(types, temp);
		} else {
			if(this.type==null) {
				this.type = type;
				return true;
			}
			mul = true;
			types = new Class<?>[] {this.type, type};
			this.type = null;
		}
		return true;
	}
	
	public boolean setType(Class<?> type) throws MultipleTypeException {
		if(mul) { throw new MultipleTypeException(); }
		if(type.equals(this.type)) { return true; }
		this.type = type;
		return true;
	}
	
	public <T extends Object> boolean remove(T element) throws InvalidTypeException, NoMatchException {
		if(!isValid(element)) { throw new InvalidTypeException(); }
		if(!contains(element)) { throw new NoMatchException(); }
		Object[] total = new Object[size()];
		int count = 0;
		for(Object x : array) {
			if(x.equals(element)) { continue; }
			total[count++] = x;
		}
		array = merge(true, total);
		return true;
	}
	
	public boolean removeByIndex(int index) throws InvalidTypeException {
		if(!isValid(get(index))) { throw new InvalidTypeException(); }
		array[index] = null;
		array = merge(true, array);
		return true;
	}
	
	public boolean removeType(Class<?> type) throws SingleTypeException, InvalidTypeException {
		if(!mul) { throw new SingleTypeException(); }
		if(!contain(types, type)) { throw new InvalidTypeException(); }
		Class<?>[][] total = new Class<?>[types.length%2!=1 ? types.length/2 : (types.length+1)/2][types.length-1];
		int count1 = 0; int count2 = 0;
		for(Class<?> x : types) {
			if(x == type) {
				count1++; count2 = 0;
				continue;
			}
			total[count1][count2++] = x;
		}
		Class<?>[] returner = new Class<?>[] {};
		for(Class<?>[] x : total) {
			returner = typeMerge(returner, x);
		}
		if(returner.length == 1) {
			this.type = returner[0];
			types = null;
			mul = false;
			return true;
		}
		types = returner;
		return true;
	}
	
	public boolean hasType(Class<?> type) {
		return mul ? contain(types, type) : this.type == type;
	}
	
	private <T> T[] merge(@SuppressWarnings("unchecked") T[]... arrays) {
		return merge(false, arrays);
	}
	
	public boolean purge() {
		array = merge(true, array);
		return true;
	}

	public <T extends Object> int index(T element) throws InvalidTypeException, NoMatchException {
		if(!contain(types, element)) { throw new InvalidTypeException(); }
		if(!contains(element)) { throw new NoMatchException(); }
		for(int x=0;x<array.length;x++) {
			if(array[x].equals(element)) {
				return x;
			}
		}
		return -1;
	}
	
	@Override
	public String toString() {
		String returner = "[";
		for(int x=0;x<array.length;x++) {
			String temp = array[x].toString();
			if(array[x].getClass() == String.class) { temp = "\"" + array[x].toString() + "\""; }
			if(x==array.length-1) { returner += temp + "];"; continue; }
			returner += temp + ", ";
		}
		return returner;
	}
	
	public <T extends Object> boolean push(T element) throws InvalidTypeException {
		return append(element);
	}
	
	public <T extends Object> T pop() throws InvalidTypeException {
		T po = get(size()-1);
		removeByIndex(size()-1);
		return po;
	}
	
	public <T extends Object> boolean insert(int index, T element) {
		Object[] split1 = new Object[index+1]; Object[] split2 = new Object[array.length-(index-1)]; 
		for(int x=0;x<index;x++) {
			split1[x] = array[x];
		}
		for(int x=index;x<array.length;x++) {
			split2[x] = array[x];
		}
		array = merge(true, split1, new Object[] {element}, split2);
		return true;
	}
	
	private <T> T[] merge(boolean nullrem, @SuppressWarnings("unchecked") T[]... arrays) {
		int length = 0;
		for(T[] x : arrays) {
			length += x.length;
			if(nullrem) {
				for(T y : x) {
					if(y==null) { length--; }
				}
			}
		}
		@SuppressWarnings("unchecked")
		T[] returner = (T[]) new Object[length];
		int cur = 0;
		for(T[] x : arrays) {
			for(T y : x) {
				if(nullrem && y==null) { continue; }
				returner[cur++] = y;
			}
		}
		return returner;
	}
	
	public int size() {
		return array.length;
	}
	
	private Class<?>[] typeMerge(Class<?>[]... arrays) {
		int length = 0;
		for(Class<?>[] x : arrays) {
			length += x.length;
			for(Class<?> y : x) {
				if(y==null) { length--; }
			}
		}
		Class<?>[] returner = new Class<?>[length];
		int cur = 0;
		for(Class<?>[] x : arrays) {
			for(Class<?> y : x) {
				if(y == null) { continue; }
				returner[cur++] = y;
			}
		}
		return returner;
	}
	
	private <T extends Object> boolean contain(T[] array, T content) {
		for(T x : array) {
			if(x==content) {
				return true;
			}
		}
		return false;
	}
	
	public <T extends Object> boolean contains(T content) {
		return contain(array, content);
	}
	
	private <T extends Object> boolean isValid(T value) {
		return (value.getClass() == (this.type) && !mul || contain(this.types, value.getClass()) && mul);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(int index) {
		if(isValid(array[index])) {
			return (T) array[index];
		}
		return null;		
	}
	
	public <T extends Object> boolean append(T value) throws InvalidTypeException {
		if(isValid(value)) {
			Object[] temp = new Object[1];
			temp[0] = value;
			array = merge(array, temp);
			return true;
		}
		throw new InvalidTypeException();
	}
	
	public <T extends Object> boolean set(int index, T value, boolean extend) {
		if(isValid(value)) {
			if(index < array.length) {
				array[index] = value;
			} else if(extend) {
				array = new Object[index+1];
				array[index] = value;
			}
			return true;
		}
		return false;
	}
	
	public <T extends Object> boolean set(int index, T value) {
		return set(index, value, true);
	}
	
	public class MultipleTypeException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public class NoMatchException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public class InvalidTypeException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public class SingleTypeException extends Exception {
		private static final long serialVersionUID = 1L;
	}
} 
