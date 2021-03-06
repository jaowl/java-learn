package ru.stqa.pft.addressbook.tests.groups;

import org.hamcrest.MatcherAssert;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.stqa.pft.addressbook.model.GroupData;
import ru.stqa.pft.addressbook.model.Groups;
import ru.stqa.pft.addressbook.tests.AddressBookTest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * Created by owlowl on 23.09.16.
 */
public class GroupDeletionTest extends AddressBookTest {
	
	@Test
	public void deleteGroup()
	{
		Groups before = app.db().groups();
		GroupData toDelete=before.iterator().next();
		app.moveTo().groupsPage();
		app.groups().delete(toDelete);
		Groups after = app.db().groups();
		assertEquals(after.getCount(),before.getCount()-1);
			
		assertThat(after,equalTo(before.without(toDelete)));
	}
	

}
