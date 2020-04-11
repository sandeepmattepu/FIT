package com.mattepu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.mattepu.exception.ActionOnlyForRootMenuException;
import com.mattepu.exception.IODeviceNotSetException;
import com.mattepu.exception.InvalidIndexException;
import com.mattepu.exception.RootMenuAsSubMenuException;
import com.mattepu.exception.SubMenuDoesNotExistException;

@RunWith(MockitoJUnitRunner.class)
public class MenuTest 
{
	@Mock
	private IODevice ioDevice;
	private IOption exitOption;
	private IOption backOption;
	private IOption homeOption;
	
	public MenuTest()
	{
		exitOption = new IOption() {
			@Override
			public OptionActionResult optionSelected() 
			{
				return OptionActionResult.EXIT_MENU;
			}
		};
		
		backOption = new IOption() {
			@Override
			public OptionActionResult optionSelected() 
			{
				return OptionActionResult.GO_UP;
			}
		};
		
		homeOption = new IOption() {
			@Override
			public OptionActionResult optionSelected() {
				return OptionActionResult.RETURN_HOME;
			}
		};
	}
	
	@Before
	public void beforeEachTest()
	{
		reset(ioDevice);
	}
	
	@Test
	public void Menu_NullName_DisplayEmptyStringInNavigationBar()
	{
		Menu menu = new Menu(null, true);
		menu.setIODevice(ioDevice);
		menu.start();
		verify(ioDevice).display("");
		
		reset(ioDevice);
		
		// When moved to sub menu navigation panel should still show empty if menu name is not passed
		menu = new Menu(null, true);
		Menu menu2 = new Menu("Wifi");
		menu2.addOption("Exit", exitOption);
		menu.addSubMenu(menu2);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("1", "1");
		menu.start();
		ArgumentCaptor<String> displayCaptor = ArgumentCaptor.forClass(String.class);
		verify(ioDevice, times(2)).display(displayCaptor.capture());
		List<String> stringsToDisplay = displayCaptor.getAllValues();
		assertEquals("\n1 : Wifi\nEnter your choice : ", stringsToDisplay.get(0));
		assertEquals("/Wifi\n1 : Exit\nEnter your choice : ", stringsToDisplay.get(1));
	}
	
	@Test
	public void Menu_CorrectName_DisplayNameInNavigationBar()
	{
		Menu menu = new Menu("Home", true);
		menu.setIODevice(ioDevice);
		menu.start();
		verify(ioDevice).display("Home");
		
		reset(ioDevice);
		
		menu = new Menu("Home", true);
		Menu menu2 = new Menu("Wifi");
		menu2.addOption("Exit", exitOption);
		menu.addSubMenu(menu2);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("1", "1");
		menu.start();
		ArgumentCaptor<String> displayCaptor = ArgumentCaptor.forClass(String.class);
		verify(ioDevice, times(2)).display(displayCaptor.capture());
		List<String> stringsDisplayed = displayCaptor.getAllValues();
		assertEquals("Home\n1 : Wifi\nEnter your choice : ", stringsDisplayed.get(0));
		assertEquals("Home/Wifi\n1 : Exit\nEnter your choice : ", stringsDisplayed.get(1));
	}
	
	@Test
	public void Menu_IsRoot_SubMenuReturnControlToItWhenReturnHomeSelected()
	{
		Menu menu = new Menu("Home", true);
		menu.setIODevice(ioDevice);
		Menu wifiMenu = new Menu("Wifi");
		menu.addSubMenu(wifiMenu);
		menu.addOption("Exit", exitOption);
		Menu forgetWifiMenu = new Menu("Forget network");
		wifiMenu.addSubMenu(forgetWifiMenu);
		forgetWifiMenu.addOption("Home", homeOption);
		when(ioDevice.acceptInput()).thenReturn("1", "1", "1", "2");
		menu.start();
		ArgumentCaptor<String> displayCaptor = ArgumentCaptor.forClass(String.class);
		verify(ioDevice, times(4)).display(displayCaptor.capture());
		List<String> stringsDisplayed = displayCaptor.getAllValues();
		assertEquals("Home/Wifi/Forget network\n1 : Home\nEnter your choice : ", stringsDisplayed.get(2));
		assertEquals("Home\n1 : Wifi\n2 : Exit\nEnter your choice : ", stringsDisplayed.get(3));
	}
	
	@Test
	public void Menu_IsRoot_OnlyRootCanUseStartMethod()
	{
		Menu rootMenu = new Menu("Home", true);
		rootMenu.setIODevice(ioDevice);
		rootMenu.start();
		assertTrue(true);
		
		Menu subMenu = new Menu("SubMenu1", false);
		try
		{
			subMenu.start();
			fail("It should throw ActionOnlyForRootException");
		}
		catch(ActionOnlyForRootMenuException e)
		{
			assertTrue(true);
		}
		
		subMenu = new Menu("SubMenu2");
		try
		{
			subMenu.start();
			fail("It should throw ActionOnlyForRootException");
		}
		catch(ActionOnlyForRootMenuException e)
		{
			assertTrue(true);
		}
	}
	
	@Test
	public void Menu_IsRoot_CannotAddOtherRootAsSubMenu()
	{
		Menu root1 = new Menu("Root1", true);
		Menu root2 = new Menu("Root2", true);
		try
		{
			root1.addSubMenu(root2);
			fail("It should throw DuplicateRootMenuException");
		}
		catch(RootMenuAsSubMenuException e)
		{
			assertTrue(true);
		}
	}
	
	@Test
	public void Menu_NotRoot_CannotAddOtherRootAsSubMenu()
	{
		Menu subMenu = new Menu("SubMenu");
		Menu rootMenu = new Menu("Home", true);
		
		try
		{
			subMenu.addSubMenu(rootMenu);
			fail("It should throw RootMenuAsSubMenuException");
		}
		catch(RootMenuAsSubMenuException e)
		{
			assertTrue(true);
		}
	}
	
	@Test
	public void setHeader_emptyOrNullValue_SkipDisplayingHeaderInMenu()
	{
		Menu menu = new Menu("Home", true);
		menu.setHeader(null);
		menu.addOption("Exit", exitOption);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
		
		reset(ioDevice);
		
		menu = new Menu("Home", true);
		menu.setHeader("");
		menu.addOption("Exit", exitOption);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
	}
	
	@Test
	public void setHeader_ValidString_DisplayHeaderInBetweenNavigationAndOptions()
	{
		Menu menu = new Menu("Home", true);
		menu.setHeader("This is header");
		menu.addOption("Exit", exitOption);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(ioDevice).display("Home\nThis is header\n1 : Exit\nEnter your choice : ");
	}
	
	@Test
	public void addOption_EmpthOfNullName_DisplayEmptyStringInOptionsAndSelectable()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption(null, exitOption);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(ioDevice).display("Home\n1 : \nEnter your choice : ");
		
		reset(ioDevice);
		
		menu = new Menu("Home", true);
		menu.addOption("", exitOption);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(ioDevice).display("Home\n1 : \nEnter your choice : ");
	}
	
	@Test
	public void addOption_ValidName_DisplayOptionsBetweenHeaderAndFooter()
	{
		Menu menu = new Menu("Home", true);
		menu.setHeader("This is header");
		menu.addOption("Option1", null);
		menu.addOption("Exit", exitOption);
		menu.setFooter("This is footer");
		when(ioDevice.acceptInput()).thenReturn("2");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(ioDevice).display("Home\nThis is header\n1 : Option1\n2 : Exit\nThis is footer\nEnter your choice : ");
	}
	
	@Test
	public void addOption_NullIOptionValue_OptionWhenSelectedShouldNotThrowException()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Option1", null);
		menu.addOption("Exit", exitOption);
		when(ioDevice.acceptInput()).thenReturn("1", "2");
		menu.setIODevice(ioDevice);
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Option1\n2 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Option1\n2 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
	}
	
	@Test
	public void addOption_ValidIOptionValue_OptionWhenSelectedShouldCallIOption()
	{
		Menu menu = new Menu("Home", true);
		IOption option = mock(IOption.class);
		when(option.optionSelected()).thenReturn(OptionActionResult.RETURN_HOME);
		menu.addOption("Option1", option);
		menu.addOption("Exit", exitOption);
		when(ioDevice.acceptInput()).thenReturn("1", "2");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(option).optionSelected();
	}
	
	@Test
	public void addOption_InvalidIndex_OptionAddedAtEndOfAllOptions()
	{
		Menu menu = new Menu("Home", true);
		Menu wifiMenu = new Menu("Wifi");
		Menu displayMenu = new Menu("Display");
		menu.addSubMenu(wifiMenu);
		menu.addSubMenu(displayMenu);
		menu.addOption("Exit", exitOption);
		IOption option1 = mock(IOption.class);
		when(option1.optionSelected()).thenReturn(OptionActionResult.RETURN_HOME);
		menu.addOption("Option1", option1, -1);
		when(ioDevice.acceptInput()).thenReturn("4", "3");
		menu.setIODevice(ioDevice);
		menu.start();
		ArgumentCaptor<String> displayStringArg = ArgumentCaptor.forClass(String.class);
		verify(option1).optionSelected();
		verify(ioDevice, times(2)).display(displayStringArg.capture());
		List<String> stringArgs = displayStringArg.getAllValues();
		assertEquals("Home\n1 : Wifi\n2 : Display\n3 : Exit\n4 : Option1\nEnter your choice : ", stringArgs.get(0));
		assertEquals("Home\n1 : Wifi\n2 : Display\n3 : Exit\n4 : Option1\nEnter your choice : ", stringArgs.get(1));
		
		menu = new Menu("Home", true);
		wifiMenu = new Menu("Wifi");
		menu.addSubMenu(wifiMenu);
		menu.addOption("Exit", exitOption);
		reset(option1);
		reset(ioDevice);
		when(option1.optionSelected()).thenReturn(OptionActionResult.RETURN_HOME);
		menu.addOption("Option1", option1, 1000);
		when(ioDevice.acceptInput()).thenReturn("3", "2");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(option1).optionSelected();
		displayStringArg = ArgumentCaptor.forClass(String.class);
		verify(option1).optionSelected();
		verify(ioDevice, times(2)).display(displayStringArg.capture());
		stringArgs = displayStringArg.getAllValues();
		assertEquals("Home\n1 : Wifi\n2 : Exit\n3 : Option1\nEnter your choice : ", stringArgs.get(0));
		assertEquals("Home\n1 : Wifi\n2 : Exit\n3 : Option1\nEnter your choice : ", stringArgs.get(1));
	}
	
	@Test
	public void addOption_ValidIndex_OptionAddedAtSpecifiedIndex()
	{
		Menu menu = new Menu("Home", true);
		Menu wifiMenu = new Menu("Wifi");
		Menu displayMenu = new Menu("Display");
		menu.addSubMenu(wifiMenu);
		IOption option1 = mock(IOption.class);
		menu.addOption("Option1", option1, 1);
		menu.addSubMenu(displayMenu);
		menu.addOption("Exit", exitOption);
		when(option1.optionSelected()).thenReturn(OptionActionResult.RETURN_HOME);
		when(ioDevice.acceptInput()).thenReturn("2", "4");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(option1).optionSelected();
		ArgumentCaptor<String> argCap = ArgumentCaptor.forClass(String.class);
		verify(ioDevice, times(2)).display(argCap.capture());
		List<String> args = argCap.getAllValues();
		assertEquals("Home\n1 : Wifi\n2 : Option1\n3 : Display\n4 : Exit\nEnter your choice : ", args.get(0));
		assertEquals("Home\n1 : Wifi\n2 : Option1\n3 : Display\n4 : Exit\nEnter your choice : ", args.get(1));
	}
	
	@Test
	public void addSubMenu_NullName_DisplayEmptyStringOption()
	{
		Menu menu = new Menu("Home", true);
		Menu wifiMenu = new Menu("Wifi");
		menu.addSubMenu(null, wifiMenu, 0);
		menu.addOption("Exit", exitOption);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("2");
		menu.start();
		verify(ioDevice).display("Home\n1 : \n2 : Exit\nEnter your choice : ");
	}
	
	@Test
	public void addSubMenu_ValidName_DisplayNameAsOneOfOption()
	{
		Menu menu = new Menu("Home", true);
		Menu wifiMenu = new Menu("Wifi");
		menu.addSubMenu("Network", wifiMenu, 0);
		menu.addOption("Exit", exitOption);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("2");
		menu.start();
		verify(ioDevice).display("Home\n1 : Network\n2 : Exit\nEnter your choice : ");
	}
	
	@Test
	public void addSubMenu_NullSubMenu_DontThrowErrorWhenOptionSelected()
	{
		Menu menu = new Menu("Home", true);
		menu.addSubMenu("Network", null, 0);
		menu.addOption("Exit", exitOption);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("1", "2");
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Network\n2 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Network\n2 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
	}
	
	@Test
	public void addSubMenu_ValidSubMenu_DisplaySubMenu()
	{
		Menu menu = new Menu("Home", true);
		Menu wifi = new Menu("Wifi");
		menu.addSubMenu(wifi);
		menu.addOption("Exit", exitOption);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("2");
		menu.start();
		verify(ioDevice).display("Home\n1 : Wifi\n2 : Exit\nEnter your choice : ");
	}
	
	@Test
	public void addSubMenu_InValidIndex_AddSubMenuAtBottomOfAllOptions()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		Menu wifi = new Menu("Wifi");
		menu.addSubMenu(wifi, -1);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
		
		reset(ioDevice);
		
		menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		wifi = new Menu("Wifi");
		menu.addSubMenu(wifi, 1000);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
	}
	
	@Test
	public void addSubMenu_ValidIndex_AddSubMenuAtSpecifiedIndex()
	{
		Menu menu = new Menu("Home", true);
		menu.setIODevice(ioDevice);
		Menu wifi = new Menu("Wifi");
		menu.addSubMenu(wifi);
		menu.addOption("Exit", exitOption);
		Menu display = new Menu("Display");
		menu.addSubMenu(display, 1);
		when(ioDevice.acceptInput()).thenReturn("3");
		menu.start();
		verify(ioDevice).display("Home\n1 : Wifi\n2 : Display\n3 : Exit\nEnter your choice : ");
	}
	
	@Test
	public void getSubMenu_InValidIndex_InvalidIndexException()
	{
		Menu menu = new Menu("Home");
		Menu wifi = new Menu("Wifi");
		menu.addSubMenu(wifi);
		try
		{
			menu.getSubMenu(-1);
			fail("It should throw InvalidIndexException");
		}
		catch(InvalidIndexException e)
		{
			assertTrue(true);
		}
		
		try
		{
			menu.getSubMenu(1000);
			fail("It should throw InvalidIndexException");
		}
		catch(InvalidIndexException e)
		{
			assertTrue(true);
		}
	}
	
	@Test
	public void getSubMenu_ValidButSubMenuDonotExistThere_SubMenuDoesNotExistException()
	{
		Menu menu = new Menu("Home");
		menu.addOption("Exit", exitOption);
		menu.addOption("Action1", null);
		try
		{
			menu.getSubMenu(0);
			fail("It should throw SubMenuDoesNotExistException");
		}
		catch(SubMenuDoesNotExistException e)
		{
			assertTrue(true);
		}
	}
	
	@Test
	public void getSubMenu_ValidAndSubMenuExistThere_ReturnSubMenu()
	{
		Menu menu = new Menu("Home");
		menu.addOption("Exit", exitOption);
		Menu wifi = new Menu("Wifi");
		menu.addSubMenu(wifi);
		Menu display = new Menu("Display");
		menu.addSubMenu(display);
		assertSame(wifi, menu.getSubMenu(1));
	}
	
	@Test
	public void removeOptionsOrSubMenu_InValidInput_ReturnFalse()
	{
		Menu menu = new Menu("Home");
		menu.addOption("Exit", exitOption);
		menu.addOption("Back", backOption);
		assertFalse(menu.removeOptionOrSubMenu(-1));
		assertFalse(menu.removeOptionOrSubMenu(1000));
	}
	
	@Test
	public void removeOptionsOrSubMenu_ValidInput_ReturnTrue()
	{
		Menu menu = new Menu("Home");
		menu.addOption("Exit", exitOption);
		menu.addOption("Back", backOption);
		assertTrue(menu.removeOptionOrSubMenu(1));
		assertTrue(menu.removeOptionOrSubMenu(0));
	}
	
	@Test
	public void removeOptionsOrSubMenu_ValidInput_RemovedOptionOrSubMenuNotDisplayed()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		menu.addOption("Back", backOption);
		Menu wifi = new Menu("Wifi");
		menu.addSubMenu(wifi);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\n2 : Back\n3 : Wifi\nEnter your choice : ");
		
		menu.removeOptionOrSubMenu(1);
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
		
		menu.removeOptionOrSubMenu(1);
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
	}
	
	@Test
	public void setFooter_NullOrEmptyString_SkipDisplayingFooter()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		menu.addOption("Back", backOption);
		menu.setFooter(null);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\n2 : Back\nEnter your choice : ");
		
		reset(ioDevice);
		
		menu.setFooter("");
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\n2 : Back\nEnter your choice : ");
	}
	
	@Test
	public void setFooter_ValidString_DisplayFooter()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		menu.addOption("Back", backOption);
		menu.setFooter("Some footer message.");
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("1");
		menu.start();
		verify(ioDevice).display("Home\n1 : Exit\n2 : Back\nSome footer message.\nEnter your choice : ");
	}
	
	@Test
	public void setIODevice_NullValue_ThrowErrorWhenStartMethodCalled()
	{
		Menu menu = new Menu("Home", true);
		try
		{
			menu.start();
			fail("It should throw IODeviceNotSetException");
		}
		catch(IODeviceNotSetException e)
		{
			assertTrue(true);
		}
	}
	
	@Test
	public void setIODevice_NotNull_InteractWithThisInstanceInsideStartMethod()
	{
		Menu menu = new Menu("Home", true);
		menu.setIODevice(ioDevice);
		menu.addOption("Exit", exitOption);
		menu.addOption("Option1", null);
		when(ioDevice.acceptInput()).thenReturn("2", "1");
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Option1\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Option1\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
	}
	
	@Test
	public void start_InValidInputFromIODevice_ClearDisplayAndShowOptionsAgain()
	{
		Menu menu = new Menu("Home", true);
		menu.setIODevice(ioDevice);
		menu.addOption("Exit", exitOption);
		when(ioDevice.acceptInput()).thenReturn(null, "1");
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();										// null entered here
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		
		reset(ioDevice);
		
		when(ioDevice.acceptInput()).thenReturn("abc", "1");
		menu.start();
		order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();										// abc entered here
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		
		reset(ioDevice);
		
		when(ioDevice.acceptInput()).thenReturn("-1", "1");
		menu.start();
		order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();										// -1 entered here
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		
		reset(ioDevice);
		
		when(ioDevice.acceptInput()).thenReturn("1000", "1");
		menu.start();
		order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();										// 1000 entered here
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
	}
	
	@Test
	public void start_OptionSelected_IOptionIsExecuted()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		IOption option1 = mock(IOption.class);
		menu.addOption("Option1", option1);
		when(option1.optionSelected()).thenReturn(OptionActionResult.SUCCESS);
		when(ioDevice.acceptInput()).thenReturn("2", "1");
		menu.setIODevice(ioDevice);
		menu.start();
		verify(option1).optionSelected();
	}
	
	@Test
	public void start_GO_UP_IsReturnedWhenOptionIsSelectedWhenMenuIsRoot_DoNothing()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		IOption option1 = mock(IOption.class);
		menu.addOption("Option1", option1);
		when(option1.optionSelected()).thenReturn(OptionActionResult.GO_UP);
		when(ioDevice.acceptInput()).thenReturn("2", "1");
		menu.setIODevice(ioDevice);
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Option1\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Option1\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
	}
	
	@Test
	public void start_GO_UP_IsReturnedWhenOptionIsSelectedWhenMenuNotRoot_ClearDisplayAndDisplayUpMenu()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		Menu subMenu = new Menu("Wifi");
		menu.addSubMenu(subMenu);
		subMenu.addOption("Back", backOption);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("2", "1", "1");
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home/Wifi\n1 : Back\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
	}
	
	@Test
	public void start_RETURN_HOME_IsReturnedWhenOptionIsSelectedWhenMenuIsRoot_DoNothing()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		IOption option1 = mock(IOption.class);
		menu.addOption("Option1", option1);
		when(option1.optionSelected()).thenReturn(OptionActionResult.RETURN_HOME);
		when(ioDevice.acceptInput()).thenReturn("2", "1");
		menu.setIODevice(ioDevice);
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Option1\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Option1\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
	}
	
	@Test
	public void start_RETURN_HOME_IsReturnedWhenOptionIsSelectedWhenMenuIsNotRoot_ClearDisplayAndDisplayRootMenu()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		Menu subMenu = new Menu("Wifi");
		Menu subMenu2 = new Menu("Home_WLAN");
		IOption homeOpt = mock(IOption.class);
		when(homeOpt.optionSelected()).thenReturn(OptionActionResult.RETURN_HOME);
		subMenu2.addOption("Home", homeOpt);
		subMenu.addSubMenu(subMenu2);
		menu.addSubMenu(subMenu);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("2", "1", "1", "1");
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home/Wifi\n1 : Home_WLAN\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home/Wifi/Home_WLAN\n1 : Home\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
	}
	
	@Test(timeout = 500)
	public void start_EXIT_MENU_IsReturnedWhenOptionIsSelected_ClearDisplayAndReturnControlFromStart()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		Menu subMenu = new Menu("Wifi");
		Menu subMenu2 = new Menu("Home_WLAN");
		IOption homeOpt = mock(IOption.class);
		when(homeOpt.optionSelected()).thenReturn(OptionActionResult.EXIT_MENU);
		subMenu2.addOption("Exit", homeOpt);
		subMenu.addSubMenu(subMenu2);
		menu.addSubMenu(subMenu);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("2", "1", "1");
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home/Wifi\n1 : Home_WLAN\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home/Wifi/Home_WLAN\n1 : Exit\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
	}
	
	@Test
	public void start_SubMenuSelected_ClearDisplayAndShowSubMenuOptions()
	{
		Menu menu = new Menu("Home", true);
		menu.addOption("Exit", exitOption);
		Menu wifiMenu = new Menu("Wifi");
		wifiMenu.addOption("Back", backOption);
		wifiMenu.addOption("Option1", null);
		wifiMenu.addOption("Option2", null);
		menu.addSubMenu(wifiMenu);
		menu.setIODevice(ioDevice);
		when(ioDevice.acceptInput()).thenReturn("2", "1", "1");
		menu.start();
		InOrder order = inOrder(ioDevice);
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home/Wifi\n1 : Back\n2 : Option1\n3 : Option2\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
		order.verify(ioDevice).display("Home\n1 : Exit\n2 : Wifi\nEnter your choice : ");
		order.verify(ioDevice).acceptInput();
		order.verify(ioDevice).clearDisplay();
	}
}
