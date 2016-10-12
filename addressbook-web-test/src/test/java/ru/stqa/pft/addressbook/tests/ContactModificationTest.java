package ru.stqa.pft.addressbook.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.stqa.pft.addressbook.model.ContactData;

import java.util.List;
import java.util.Set;

/**
 * Created by owlowl on 24.09.16.
 */
public class ContactModificationTest extends AddressBookTest{
	@Test
	public void modifyContact(){
		ensurePreconditions();
		Set<ContactData> before = app.contacts().getSet();
		ContactData toModify=before.iterator().next();
		ContactData modified=new ContactData().withFirstName("First Name").withLastName("Last Name").withAddress("address").withId(toModify.getId()).withBirth("10.03.2016").withAnniversary("27.05.1985");
		
		app.contacts().modify(modified);
		
		app.moveTo().contactsPage();
		Set<ContactData> after = app.contacts().getSet();
		Assert.assertEquals(after.size(), before.size());
	
		before.remove(toModify);
		before.add(modified);
		
		Assert.assertEquals(after, before);
	}
	

	
	@BeforeMethod
	public void ensurePreconditions() {
		app.moveTo().contactsPage();
		if(app.contacts().getList().size()==0){
			app.contacts().create(new ContactData("FirstNameForTest", "LastNameForTest", "addr", "mobilephone", "email", "15.12.1992", "17.09.2001","TestGroupName"));
			app.moveTo().contactsPage();
		}
	}
}
