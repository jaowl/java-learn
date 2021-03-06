package ru.stqa.pft.addressbook.appamanager;

import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.asserts.Assertion;
import ru.stqa.pft.addressbook.model.ContactData;
import ru.stqa.pft.addressbook.model.Contacts;
import ru.stqa.pft.addressbook.model.GroupData;
import ru.stqa.pft.addressbook.model.Groups;

import java.io.*;
import java.util.*;

import static ru.stqa.pft.addressbook.appamanager.HandyFunctions.STRING_SEPARATOR;

/**
 * Created by owlowl on 22.09.16.
 */
public class ContactHelper extends BaseHelper {
	
	private Contacts contactsCash;
	private static final Comparator<ContactData> byId = (c1, c2) -> Integer.compare(c1.getId(), c2.getId());
	
	public ContactHelper(RemoteWebDriver wd) {
		super(wd);
	}
	public Comparator<ContactData> getComparator() {
		return byId;
	}
	public void fillContactForm(ContactData contactData, boolean creation) {
		HandyFunctions.setFieldValue("firstname", contactData.getFirstName(), wd);
		HandyFunctions.setFieldValue("lastname", contactData.getLastName(), wd);
		HandyFunctions.setFieldValue("address", contactData.getAddress(), wd);
		HandyFunctions.setFieldValue("mobile", contactData.getMobilePhone(), wd);
		HandyFunctions.setFieldValue("home", contactData.getHomePhone(), wd);
		HandyFunctions.setFieldValue("work", contactData.getWorkPhone(), wd);
		HandyFunctions.setFieldValue("email", contactData.getEmail(), wd);
		putFile("photo", contactData.getPhoto());
		
		if (null != contactData.getBirth()) {
			HandyFunctions.chooseInSelector(1, contactData.getBirth().getSelectorDay(), wd);
			HandyFunctions.chooseInSelector(2, contactData.getBirth().getSelectorMonth(), wd);
			HandyFunctions.setFieldValue("byear", contactData.getBirth().getYear(), wd);
		}
		if (null != contactData.getAnniversary()) {
			HandyFunctions.chooseInSelector(3, contactData.getAnniversary().getSelectorDay(), wd);
			HandyFunctions.chooseInSelector(4, contactData.getAnniversary().getSelectorMonth(), wd);
			HandyFunctions.setFieldValue("ayear", contactData.getAnniversary().getYear(), wd);
		}
		//selectContactGroup(2);
		if (creation) {
			if (contactData.getGroups().size()>0) {
				Assert.assertTrue(contactData.getGroups().size()==1);
				selectContactGroup(contactData.getGroups().iterator().next());
			}
		}
	}
	
	private void selectContactGroup(int groupNumber) {
		HandyFunctions.chooseInSelector(5, groupNumber, wd);
	}
	
	private void selectContactGroup(GroupData group) {
		if(!(null==group)) {
			HandyFunctions.chooseInSelector(wd, "new_group", group.getName());
		}
	}
	
	public void initContact() {
		wd.findElement(By.linkText("add new")).click();
	}
	
	public void editContact(int n) {
		this.pressButtonByXPath("//table[@id='maintable']/tbody/tr[" + String.valueOf(n+2) + "]/td[8]/a/img");
	}
	
	public void saveContact() {
		this.pressButton("update");
	}
	
	public void choose() {
		selectElement();
	}
	
	public void choose(int i) {
		selectElement(i);
	}
	
	public void pressDeleteButton() {
		pressButtonByXPath("//div[@id='content']/form[2]/div[2]/input");
	}
	
	
	public void create(ContactData contactData) {
		initContact();
		fillContactForm(contactData, true);
		this.contactsCash=null;
		pressButtonByXPath("//div[@id='content']/form/input[21]");
		
	}
	public Contacts getSet(){
		return getSet(false);
	}
	public Contacts getSet(Boolean regetCache){
		final int NAME_COLUMN_NUMBER=2;
		final int LAST_NAME_COLUMN_NUMBER=1;
		final int ADDRESS_COLUMN_NUMBER = 3;
		final int EMAIL_COLUMN_NUMBER = 4;
		final int NUMBERS_COLUMN_NUMBER = 5;
		
		if (!(regetCache||null==contactsCash)) {
			return contactsCash;
		}
		contactsCash = new Contacts();
		List<WebElement> pageElements=	wd.findElements(By.name("entry" ));
		for(WebElement row:pageElements){
			List <WebElement> cells =row.findElements(By.tagName("td"));
			String name=cells.get(NAME_COLUMN_NUMBER).getText();
			String lastName=cells.get(LAST_NAME_COLUMN_NUMBER).getText();
			String address=cells.get(ADDRESS_COLUMN_NUMBER).getText();
			String emails=cells.get(EMAIL_COLUMN_NUMBER).getText();
			String[] emailArr = emails.split(STRING_SEPARATOR);
			String allphones=cells.get(NUMBERS_COLUMN_NUMBER).getText();
			String[] phoneArr = allphones.split(STRING_SEPARATOR);
			
			String id =row.findElement(By.className("center")).findElement(By.name("selected[]")).getAttribute("id");
			ContactData contact= new ContactData().withFirstName(name).withLastName(lastName).withId(id)
					.withAddress(address);
			if (phoneArr.length>=3) {
				contact.withHomePhone(phoneArr[0]).withMobilePhone(phoneArr[1]).withWorkPhone(phoneArr[2]);
			}else{
				contact.withAllPhones(allphones);
			}
			
			if (emailArr.length>=3) {
				contact.withEmail(emailArr[0]).withEmail2(emailArr[1]).withEmail3(emailArr[2]);
			}else{
				contact.withAllEmails(emails);
			}
			
			contactsCash.add(contact);
		}
		return contactsCash;
	}
	public int getCount() {
		return wd.findElements(By.name("selected[]")).size();
	}
	
	@Deprecated
	public List<ContactData> getList() {
		final int NAME_COLUMN_NUMBER=2;
		final int LAST_NAME_COLUMN_NUMBER=1;
		List<ContactData> contacts=new ArrayList<ContactData>();
		List<WebElement> pageElements=	wd.findElements(By.name("entry" ));
		for(WebElement we:pageElements){
			String name=we.findElements(By.tagName("td")).get(NAME_COLUMN_NUMBER).getText();
			String lastName=we.findElements(By.tagName("td")).get(LAST_NAME_COLUMN_NUMBER).getText();
			String id =we.findElement(By.className("center")).findElement(By.name("selected[]")).getAttribute("id");
			Assert.assertNotNull(id);
			Assert.assertNotEquals(id,"","Пустое поле id");
			ContactData contact= new ContactData(name,lastName, null,null,null,null,null);
			contact.withId(id);
			contacts.add(contact);
		}
		return contacts;
	}
	
	public void delete(ContactData toDelete) {
		selectById(toDelete.getId());
		pressDeleteButton();
		this.contactsCash=null;
		acceptDialog();
	}
	
	private void selectById(int id) {
		wd.findElement(By.cssSelector("input[id='"+id+"']")).click();
	}
	
	public void modify(ContactData modified) {
		editContactById(modified.getId());
		fillContactForm(modified, false);
		this.contactsCash=null;
		saveContact();
	}
	public ContactData infoFromDetails(ContactData contact) {
		showContactDetailsById(contact.getId());
		String fullInfo=wd.findElement(By.id("content")).getText();
		fullInfo=fullInfo.replaceAll(" \\(www.+\\)","");
		ContactData retval = new ContactData().withId(contact.getId()).withFullInfo(fullInfo);
		wd.navigate().back();
		return retval;
	}
	
	private void showContactDetailsById(int id) {
		wd.findElement(By.xpath("//a[contains(@href,'view.php?id="+id+"')]")).click();
	}
	private void chooseContact(int id){
		wd.findElement(By.xpath("//input[@id="+id+"]")).click();
	}
	private void chooseGroupForContact(String id) {
		String expression = ".//*[@id='content']/form[2]/div[4]/select[@name='to_group']//option[@value=" + id + "]";
		if (!wd.findElement(By.xpath(expression)).isSelected()) {
			wd.findElement(By.xpath(expression)).click();
		}
	}
	
	private void editContactById(int id) {
		wd.findElement(By.xpath("//a[contains(@href,'edit.php?id="+id+"')]")).click();
	}
	
	public ContactData infoFromEditForm(ContactData contact) {
		editContactById(contact.getId());
		ContactData retval = new ContactData().withId(contact.getId()).withFirstName(getFieldValue("firstname")).withLastName(getFieldValue("lastname"))
				.withHomePhone(getFieldValue("home")).withMobilePhone(getFieldValue("mobile")).withWorkPhone(getFieldValue("work"))
				.withEmail(getFieldValue("email")).withEmail2(getFieldValue("email2")).withEmail3(getFieldValue("email3"))
				.withAddress(getFieldValue("address"));
		wd.navigate().back();
		return retval;
	}
	
	
	public Contacts loadFromDefaultJSON() throws IOException {
		File source = new File("src/test/resources/contacts.json");
		if (!source.exists()) {
			throw new FileNotFoundException("Не найден файл " + source.getAbsolutePath());
		}
		return loadJSON(source);
	}
	
	private Contacts loadJSON(File source) throws IOException {
		Contacts contacts;
		String json = "";
		Gson gson = new Gson();
		
		try(BufferedReader reader = new BufferedReader(new FileReader(source))) {
			String line = reader.readLine();
			while (null != line) {
				json += line;
				line = reader.readLine();
			}
			contacts = gson.fromJson(json, Contacts.class);
			return contacts;
		}
	}
	
	public void setGroup(ContactData toModify, GroupData toAdd) {
		chooseContact(toModify.getId());
		chooseGroupForContact(toAdd.getId());
		addContactsToGroup();
	}
	
	private void addContactsToGroup() {
		pressButtonByXPath(".//*[@id='content']/form[2]/div[4]/input[@name='add']");
	}
	
	public void deleteGroup(ContactData toModify, GroupData toDelete) {
		System.out.println("contact: "+toModify);
		System.out.println("group: "+toDelete);
		chooseGroupInSelector(toDelete.getId());
		chooseContact(toModify.getId());
		pressButton("remove");
	}
	
	private void chooseGroupInSelector(String id) {
		String expression = ".//*[@id='right']/select[@name='group']//option[@value=" + id + "]";
		if (!wd.findElement(By.xpath(expression)).isSelected()) {
			wd.findElement(By.xpath(expression)).click();
		}
	}
}