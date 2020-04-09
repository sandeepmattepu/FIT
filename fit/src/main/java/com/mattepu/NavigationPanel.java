package com.mattepu;

public class NavigationPanel 
{
	private String navigationPanelDisplay = "";
	private String menuSeperator = "/";
	
	public void setSeperator(String seperator)
	{
		if(seperator != null && !(seperator.equals("")))
		{
			menuSeperator = seperator;
		}
	}
	
	public void setRootMenu(String rootMenuName)
	{
		if(rootMenuName != null)
		{
			String[] menus = navigationPanelDisplay.split(menuSeperator);
			menus[0] = rootMenuName;
			navigationPanelDisplay = "";
			for(int i = 0; i < menus.length; i++)
			{
				navigationPanelDisplay += menus[i];
			}
		}
	}
	
	public void changeToSubMenu(String subMenuName)
	{
		if(subMenuName == null)
		{
			subMenuName = "";
		}
		navigationPanelDisplay += (menuSeperator + subMenuName);
	}
	
	public void goUpMenu()
	{
		if(navigationPanelDisplay.contains(menuSeperator))
		{
			int stripStringFromIndex = navigationPanelDisplay.lastIndexOf(menuSeperator);
			navigationPanelDisplay = navigationPanelDisplay.substring(0, stripStringFromIndex);
		}
	}
	
	public void goToRootMenu()
	{
		if(navigationPanelDisplay.contains(menuSeperator))
		{
			int stripStringFromIndex = navigationPanelDisplay.indexOf(menuSeperator);
			navigationPanelDisplay = navigationPanelDisplay.substring(0, stripStringFromIndex);
		}
	}
	
	@Override
	public String toString()
	{
		return navigationPanelDisplay;
	}
}
