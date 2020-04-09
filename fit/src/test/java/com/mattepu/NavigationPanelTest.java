package com.mattepu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class NavigationPanelTest 
{
	private NavigationPanel nav;
	
	@Before
	public void beforeEachTest()
	{
		nav = new NavigationPanel();
	}
	
	@Test
	public void setSeperator_NullOrEmptyString_MenuAreSeperatedWithDefaultSeperator()
	{
		String rootMenuName = "Home";
		String seperator = "/";					// Default separator
		String subMenuName = "Settings";
		nav.setSeperator(null);
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		assertEquals((rootMenuName + seperator + subMenuName), nav.toString());
		
		nav = new NavigationPanel();
		nav.setSeperator("");
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		assertEquals((rootMenuName + seperator + subMenuName), nav.toString());
	}
	
	@Test
	public void setSeperator_ValidString_MenusAreSeperatedWithGivenString()
	{
		String rootMenuName = "Home";
		String seperator = "->";
		String subMenuName = "Settings";
		nav.setSeperator(null);
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		assertEquals((rootMenuName + seperator + subMenuName), nav.toString());
	}
	
	@Test
	public void setRootMenu_NullOrEmptyName_toStringReturnStringWithoutRootMenuName()
	{
		String rootMenuName = null;
		String seperator = "/";
		String subMenuName = "Settings";
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		assertEquals((seperator + subMenuName), nav.toString());
		
		nav = new NavigationPanel();
		rootMenuName = "";
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		assertEquals((seperator + subMenuName), nav.toString());
		
		nav = new NavigationPanel();
		nav.setRootMenu(rootMenuName);
		assertEquals("", nav.toString());
	}
	
	@Test
	public void setRootMenu_ValidValue_toStringReturnsStringWithRootMenuName()
	{
		String rootMenuName = "Home";
		nav.setRootMenu(rootMenuName);
		assertEquals(rootMenuName, nav.toString());
		
		String seperator = "/";
		String subMenuName = "Settings";
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		assertEquals((rootMenuName + seperator + subMenuName), nav.toString());
	}
	
	@Test
	public void changeToSubMenu_NullOrEmptyString_toStringReturnsStringWithoutSubMenuName()
	{
		String rootMenuName = "Home";
		String seperator = "/";
		String subMenuName = null;
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		assertEquals((rootMenuName + seperator), nav.toString());
		
		nav = new NavigationPanel();
		subMenuName = "";
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		assertEquals((rootMenuName + seperator), nav.toString());
	}
	
	@Test
	public void changeToSubMenu_ValidValue_toStringReturnsStringWithSubMenuValue()
	{
		String rootMenuName = "Home";
		String seperator = "/";
		String subMenuName = "Settings";
		String subMenuName2 = "Display";
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		nav.changeToSubMenu(subMenuName2);
		assertEquals((rootMenuName + seperator + subMenuName + seperator + subMenuName), nav.toString());
	}
	
	@Test
	public void goUpMenu_NavigationAtRootMenu_toStringReturnsStringWithRootMenu()
	{
		String rootMenuName = "Home";
		nav.setRootMenu(rootMenuName);
		assertEquals(rootMenuName , nav.toString());
		nav.goUpMenu();
		assertEquals(rootMenuName, nav.toString());
	}
	
	@Test
	public void goUpMenu_NavigationAtSomeSubMenu_toStringReturnsStringWithoutSubMenuAtEnd()
	{
		String rootMenuName = "Home";
		String seperator = "/";
		String subMenuName = "Settings";
		String subMenuName2 = "Display";
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		nav.changeToSubMenu(subMenuName2);
		assertEquals((rootMenuName + seperator + subMenuName + seperator + subMenuName), nav.toString());
		nav.goUpMenu();
		assertEquals((rootMenuName + seperator + subMenuName), nav.toString());
		nav.goUpMenu();
		assertEquals(rootMenuName, nav.toString());
	}
	
	@Test
	public void goToRootMenu_NavigationAtRootMenu_toStringReturnsOnlyRootMenuName()
	{
		String rootMenuName = "Home";
		nav.setRootMenu(rootMenuName);
		assertEquals(rootMenuName , nav.toString());
		nav.goToRootMenu();
		assertEquals(rootMenuName, nav.toString());
	}
	
	@Test
	public void goToRootMenu_NavigationAtSubMenu_toStringReturnOnlyRootMenuName()
	{
		String rootMenuName = "Home";
		String seperator = "/";
		String subMenuName = "Settings";
		String subMenuName2 = "Display";
		nav.setRootMenu(rootMenuName);
		nav.changeToSubMenu(subMenuName);
		nav.changeToSubMenu(subMenuName2);
		assertEquals((rootMenuName + seperator + subMenuName + seperator + subMenuName), nav.toString());
		nav.goToRootMenu();
		assertEquals(rootMenuName, nav.toString());
	}
}
