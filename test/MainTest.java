package test;

import source.FlowArray;
import source.FlowArray.InvalidTypeException;
import source.FlowArray.SingleTypeException;

public class Main {

	public static void main(String[] args) throws InvalidTypeException, SingleTypeException {
        FlowArray arr = new FlowArray(Integer.class);
        arr.addType(String.class);
        arr.addType(Boolean.class);
        arr.append(true);
        arr.append(3);
        arr.insert(1, "13");
        arr.removeType(String.class);
        for(Class<?> x : arr.types) {
        	System.out.println(x);
        }
        System.out.println(arr);
    }

}
