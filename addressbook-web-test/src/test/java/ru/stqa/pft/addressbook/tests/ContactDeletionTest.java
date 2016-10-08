package ru.stqa.pft.addressbook.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.stqa.pft.addressbook.model.ContactData;

import java.util.Comparator;
import java.util.List;

/**
 * Created by owlowl on 24.09.16.
 */
public class ContactDeletionTest extends AddressBookTest{
	
		@Test(enabled = false)
	public void deleteContactFromTable(){
		app.getNavigation().openContacts();
			if (!app.getContactHelper().isAnyContactsThere()) {
			app.getContactHelper().createContact(new ContactData("FirstNameForTest", "LastNameForTest", "addr", "mobilephone", "email", "15.12.1992", "17.09.2001", "TestGroupName"));
			app.getNavigation().openContacts();
		}
		List<ContactData> before = app.getContactHelper().getContactList();
		int deleteIndex=0;
		app.getContactHelper().chooseContact(deleteIndex);
		app.getContactHelper().pressDeleteButton();
		app.getContactHelper().acceptDialog();
		app.getNavigation().openContacts();
		List<ContactData> after = app.getContactHelper().getContactList();
		Assert.assertEquals(after.size(), before.size()-1);
		before.remove(deleteIndex);
		after.sort(app.getContactHelper().getComparator());
		before.sort(app.getContactHelper().getComparator());
		Assert.assertEquals(after, before);
	}
}
