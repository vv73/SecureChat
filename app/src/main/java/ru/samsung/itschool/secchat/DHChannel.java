package ru.samsung.itschool.secchat;

public class DHChannel {

	String n1, n2, key;
	String name1;
	String name2;

	public DHChannel(String name1, String name2, String n1, String n2,
			String key) {
		this.name1 = name1;
		this.name2 = name2;
		this.n1 = n1;
		this.n2 = n2;
		this.key = key;

	}
}
