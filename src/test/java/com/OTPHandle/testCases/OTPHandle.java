package com.OTPHandle.testCases;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;

import io.github.bonigarcia.wdm.WebDriverManager;
public class OTPHandle {
	public static final String Account_SID="AC80d1fe4795c2bdefd33d38b0a654419b";
	public static final String Auth_token="df3ba1f9a1b48a5d246bf3ed510cb18f";
	public static void main(String[] args) {
		WebDriverManager.chromedriver().setup();
		
		WebDriver driver=new ChromeDriver();
		driver.get("https://www.amazon.in/ap/register?_encoding=UTF8&openid.assoc_handle=inflex&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.mode=checkid_setup&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.ns.pape=http%3A%2F%2Fspecs.openid.net%2Fextensions%2Fpape%2F1.0&openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.in%2Fgp%2Fyourstore%2Fhome%3Fie%3DUTF8%26ref_%3Dnav_newcust");
		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
		
	//	driver.findElement(By.xpath("//*[@id=\"nav-link-accountList\"]/span[1]")).click();
		//driver.findElement(By.xpath("//*[@id=\"nav-flyout-ya-newCust\"]/a")).click();
		driver.findElement(By.id("ap_customer_name")).sendKeys("Rohit Soni");
		driver.findElement(By.id("auth-country-picker-container")).click();

		driver.findElement(By.xpath("//ul[@role='application']//li/a[contains(text(),'United States +1')]")).click();
		driver.findElement(By.id("ap_phone_number")).sendKeys("2512379882");
		driver.findElement(By.id("ap_password")).sendKeys("Rohit@123");
		driver.findElement(By.id("continue")).click();
		
		// get the OTP using Twilio APIs:
				Twilio.init(Account_SID, Auth_token);
				String smsBody = getMessage();
				System.out.println(smsBody);
				String OTPNumber = smsBody.replaceAll("[^-?0-9]+", " ");
				System.out.println(OTPNumber);
				
				driver.findElement(By.id("auth-pv-enter-code")).sendKeys(OTPNumber);
		
		}
	public static String getMessage() {
		return getMessages().filter(m -> m.getDirection().compareTo(Message.Direction.INBOUND) == 0)
				.filter(m -> m.getTo().equals("+12512379882")).map(Message::getBody).findFirst()
				.orElseThrow(IllegalStateException::new);
	}

	private static Stream<Message> getMessages() {
		ResourceSet<Message> messages = Message.reader(Account_SID).read();
		return StreamSupport.stream(messages.spliterator(), false);
	}

}
