package com.mattepu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.mattepu.exception.InvalidIndexException;

public class OptionManagerTest 
{
	private OptionsManager opm;
	private IOption op1;
	private IOption op2;
	
	public OptionManagerTest()
	{
		// Two dummy options
		op1 = new IOption() {
			
			@Override
			public OptionActionResult optionSelected() {
				return null;
			}
		};
		op2 = new IOption() {
			
			@Override
			public OptionActionResult optionSelected() {
				return null;
			}
		};
	}
	
	@Before
	public void beforeEachTest()
	{
		opm = new OptionsManager();
	}
	
	@Test
	public void addOption_NullOptionName_toStringReturnsEmptyOptionName() 
	{
		opm.addOption(null, null);
		assertEquals("1 : ", opm.toString());
		opm.addOption(null, null);
		assertEquals("1 : \n2 : ", opm.toString());
	}
	
	@Test
	public void addOption_ValidOptionNames_toStringReturnsOptionNames()
	{
		opm.addOption("Wifi", null);
		assertEquals("1 : Wifi", opm.toString());
		opm.addOption("Display", null);
		assertEquals("1 : Wifi\n2 : Display", opm.toString());
	}
	
	@Test
	public void addOption_InvalidIndex_toStringReturnsOptionNameAtLastOfAllOptions()
	{
		opm.addOption("Wifi", null);
		opm.addOption("Display", null, -1);
		assertEquals("1 : Wifi\n2 : Display", opm.toString());
		opm.addOption("Apps", null, 1000);
		assertEquals("1 : Wifi\n2 : Display\n3 : Apps", opm.toString());
	}
	
	@Test
	public void addOption_InValidIndex_ReturnIndexOfLastOptionPlusOne()
	{
		opm.addOption("Wifi", null);
		opm.addOption("Display", null);
		int index = opm.addOption("Apps", null, -1);
		assertEquals(2, index);
	}
	
	@Test
	public void addOption_ValidIndex_ReturnValidIndex()
	{
		opm.addOption("Wifi", null);
		opm.addOption("Display", null);
		int index = opm.addOption("Apps", null, 1);
		assertEquals(1, index);
	}
	
	@Test
	public void addOption_ValidIndex_toStringReturnOptionAtSpecifiedIndex()
	{
		opm.addOption("Wifi", null);
		opm.addOption("Apps", null);
		assertEquals("1 : Wifi\n2 : Apps", opm.toString());
		opm.addOption("Display", null, 1);
		assertEquals("1 : Wifi\n2 : Display\n3 : Apps", opm.toString());
	}
	
	@Test
	public void removeOption_InvalidIndex_ThrowInvalidIndexException()
	{
		try
		{
			opm.removeOption(-1);
			fail("InvalidIndexException not thrown");
		}
		catch(Exception e)
		{
			assertEquals(InvalidIndexException.class, e.getClass());
		}
		try
		{
			opm.removeOption(1000);
			fail("InvalidIndexException not thrown");
		}
		catch(Exception e)
		{
			assertEquals(InvalidIndexException.class, e.getClass());
		}
	}
	
	@Test
	public void removeOption_ValidIndex_toStringReturnsWithoutOptionThatIsRemoved()
	{
		opm.addOption("Wifi", null);
		opm.addOption("Display", null);
		opm.addOption("Apps", null);
		assertEquals("1 : Wifi\n2 : Display\n3 : Apps", opm.toString());
		opm.removeOption(1);
		assertEquals("1 : Wifi\n2 : Apps", opm.toString());
		opm.removeOption(0);
		assertEquals("1 : Apps", opm.toString());
	}
	
	@Test
	public void getOptionAt_InvalidIndex_ThrowInvalidIndexException()
	{
		try
		{
			opm.getOptionAt(-1);
			fail("InvalidIndexException not thrown");
		}
		catch(Exception e)
		{
			assertEquals(InvalidIndexException.class, e.getClass());
		}
		try
		{
			opm.getOptionAt(1000);
			fail("InvalidIndexException not thrown");
		}
		catch(Exception e)
		{
			assertEquals(InvalidIndexException.class, e.getClass());
		}
	}
	
	@Test
	public void getOptionAt_ValidIndex_ReturnIOptionValue()
	{
		opm.addOption("Wifi", op1);
		assertSame(op1, opm.getOptionAt(0));
		opm.addOption("Display", op2, 0);
		assertSame(op2, opm.getOptionAt(0));
	}
	
	@Test
	public void toString_NoOptions_ReturnEmptyString()
	{
		assertEquals("", opm.toString());
	}
	
	@Test
	public void toString_HasTwoOptions_ReturnsStringWithTwoOptions()
	{
		opm.addOption("Wifi", null);
		opm.addOption("Apps", null);
		assertEquals("1 : Wifi\n2 : Apps", opm.toString());
	}
}
