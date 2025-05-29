package com.example.demo.helper;

import java.util.UUID;

public class SecurityCodeHelper {

	public static String gennerate() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
