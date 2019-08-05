package com.testrails;

import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.BeforeTest;

import java.util.List;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.CaseField;
import com.codepine.api.testrail.model.Project;
import com.codepine.api.testrail.model.Result;
import com.codepine.api.testrail.model.ResultField;
import com.codepine.api.testrail.model.Run;
import com.codepine.api.testrail.model.Section;
import com.codepine.api.testrail.model.Suite;

public class test_execution {
	
	WebDriver driver;
	
	public String internet_explorer_path = "C:\\Users\\Benjamin\\Downloads\\IEDriverServer_Win32_3.14.0\\IEDriverServer.exe";
	public String websiteurl = "http://www.google.com";
	
	TestRail testrail;
	Project project;
	Suite suite;
	Section section;
	Case testcase;
	Run run;

	@BeforeTest
	public void config() {
		
		System.setProperty("webdriver.ie.driver", internet_explorer_path);
		driver = new InternetExplorerDriver();
			
	}
	
	@Test
	public void launch_search() {
		
		driver.get(websiteurl);

	}
	
	@Test
	public void testrail_report() {
		
		testrail = TestRail.builder("https://some.testrail.net", "username", "password").build();
		
		//Create a new project
		project = testrail.projects().add(new Project().setName("Playground Project")).execute();
		
		//Add a new test suite
		suite = testrail.suites().add(project.getId(), new Suite().setName("Functional Tests")).execute();
		
		//Add a new section
		section = testrail.sections().add(project.getId(), new Section().setSuiteId(suite.getId()).setName("Weekly Regression")).execute();
		
		//Add a new test case
		List<CaseField> customCaseFields = testrail.caseFields().list().execute();
		testcase = testrail.cases().add(section.getId(), new Case().setTitle("Be able to play in the playground"), customCaseFields).execute();
		
		//Add a new test run
		run = testrail.runs().add(project.getId(), new Run().setSuiteId(suite.getId()).setName("Weekly Regression")).execute();
		
		//Add test result
		List<ResultField> customResultFields = testrail.resultFields().list().execute();
		testrail.results().addForCase(run.getId(), testcase.getId(), new Result().setStatusId(1), customResultFields).execute();
		
		//Close the run
		testrail.runs().close(run.getId()).execute();
		
		//Complete the project - supports partial updates
		testrail.projects().update(project.setCompleted(true)).execute();
	
		
	}
}
