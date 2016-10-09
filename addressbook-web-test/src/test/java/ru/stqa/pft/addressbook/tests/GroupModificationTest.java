package ru.stqa.pft.addressbook.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.stqa.pft.addressbook.model.GroupData;

import java.util.List;

/**
 * Created by owlowl on 23.09.16.
 */
public class GroupModificationTest extends AddressBookTest {
	@BeforeMethod
	public void ensurePreconditions(){
		app.moveTo().groupsPage();
		if (app.groups().getList().size()==0) {
			app.groups().create(new GroupData("TestGroupName", null, null));
		}
	}
	 
	@Test
	public void testGroupModification() {
		int modifiedIndex;
		GroupData modified;
	
		
		List<GroupData> before = app.groups().getList();
		modifiedIndex=before.size()-1;
		
		modified=new GroupData(before.get(modifiedIndex).getId(), "t1", "t2", "t3");
		
		app.groups().modify(modifiedIndex, modified);
		
		app.groups().returnToGroupPage();
		
		List<GroupData> after = app.groups().getList();
		Assert.assertEquals(after.size(), before.size());
		before.remove(modifiedIndex);
		before.add(modified);
		before.sort(app.groups().getComparator());
		after.sort(app.groups().getComparator());
		Assert.assertEquals(after, before);
	}
	

}
