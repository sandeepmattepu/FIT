package com.mattepu;

import java.util.Scanner;

public class InputOutputDevice implements IODevice
{
	private Scanner scanner;
	private String osName;
	
	public InputOutputDevice() 
	{
		scanner = new Scanner(System.in);
		osName = System.getProperty("os.name");
	}

	@Override
	public void display(String content) 
	{
		String[] lines = content.split("\n");
		for(int i = 0; i < lines.length; i++)
		{
			if(i == (lines.length - 1))
			{
				System.out.print(lines[i]);
			}
			else
			{
				System.out.println(lines[i]);
			}
		}
	}

	@Override
	public String acceptInput() 
	{
		String result = scanner.nextLine();
		return result;
	}

	@Override
	public void clearDisplay() 
	{
		try
		{
	        if (osName.contains("Windows"))
	        {
	            Runtime.getRuntime().exec("cls");
	        }
	        else
	        {
	            Runtime.getRuntime().exec("clear");
	        }
		}
		catch(Exception e)
		{}
	}

}
