package com.mattepu;

public interface IODevice 
{
	void display(String content);
	
	public String acceptInput();
	
	public void clearDisplay();
}
