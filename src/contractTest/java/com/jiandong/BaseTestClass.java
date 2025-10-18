package com.jiandong;

import com.jiandong.core.apiversioning.AccountController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;

public class BaseTestClass {

	@BeforeEach
	public void setup() {
		RestAssuredMockMvc.standaloneSetup(new AccountController());
	}

}
