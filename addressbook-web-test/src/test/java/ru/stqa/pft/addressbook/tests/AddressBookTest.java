package ru.stqa.pft.addressbook.tests;

import org.openqa.selenium.remote.BrowserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import ru.stqa.pft.addressbook.appamanager.ApplicationManager;
import ru.stqa.pft.addressbook.model.ContactData;
import ru.stqa.pft.addressbook.model.GroupData;
import ru.stqa.pft.addressbook.model.Groups;
import ru.stqa.pft.addressbook.tests.groups.GroupCreationTests;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Created by owlowl on 19.09.16.
 */
public class AddressBookTest {
	Logger logger = LoggerFactory.getLogger(GroupCreationTests.class);
	protected static final ApplicationManager app =
			new ApplicationManager(System.getProperty("browser", BrowserType.CHROME));
	
	@BeforeSuite
	public void setUp() throws Exception {
		app.init();
	}
	
	@BeforeMethod()
	public void logTestStart(Method method, Object[] p) {
		logger.info("Start test " + method.getName());
		logger.debug("test parametrs are " + Arrays.asList(p));
	}
	
	@BeforeMethod(dependsOnMethods = "logTestStart")
	public void ensureGroupPreconditions() {
		if (app.db().groups().size() == 0) {
			app.moveTo().groupsPage();
			app.groups().create(new GroupData().withId("TestGroupName"));
		}
	}
	@BeforeMethod(dependsOnMethods = "logTestStart")
	public void ensureContactPreconditions() {
			app.moveTo().contactsPage();
		if (app.contacts().getSet().size() == 0) {
			app.contacts().create(new ContactData().withFirstName("FirstNameForTest").withLastName("LastNameForTest")
					.withAddress("addr").withMobilePhone("mobilephone").withEmail("email").withBirth("15.12.1992")
					.withAnniversary("17.09.2001"));//, "TestGroupName"));
			app.moveTo().contactsPage();
		}
	}
	
	@AfterMethod(alwaysRun = true)
	public void logTestFinish(Method method) {
		logger.info("Stop test " + method.getName());
	}
	
	@AfterSuite(alwaysRun = true)
	public void tearDown() {
		app.stop();
	}
	
	public void verifyGroupListinUI() {
		if(app.configuration().isUseUIChecks()){
			Groups fromDB = app.db().groups();
			Groups fromGUI = app.groups().getSet();
			Assert.assertEquals(fromGUI,fromDB.stream()
					.map((g)->new GroupData().withId(g.getId()).withName(g.getName())).collect(Collectors.toSet()));
		}
		
	}
}
