package com.mattepu;

public class Menu
{
	public Menu(String name, boolean isRoot)
	{
		
	}
	
	public Menu(String name)
	{
		
	}
	
	public void setHeader(String header)
	{
		
	}
	
	public int addOption(String optionName, IOption option, int atIndex)
	{
		return -1;
	}
	
	public int addOption(String optionName, IOption option)
	{
		return -1;
	}
	
	public int addSubMenu(String menuName, Menu subMenu, int atIndex)
	{
		return -1;
	}
	
	public int addSubMenu(Menu subMenu)
	{
		return -1;
	}
	
	public int addSubMenu(Menu subMenu, int atIndex)
	{
		return -1;
	}
	
	public Menu getSubMenu(int atIndex)
	{
		return null;
	}
	
	public boolean removeOptionOrSubMenu(int at)
	{
		return false;
	}
	
	public void setFooter(String footer)
	{
		
	}
	
	public void setIODevice(IODevice ioDevice)
	{
		
	}
	
	public void start()
	{
		
	}
	
	public boolean isRoot()
	{
		return false;
	}
}
