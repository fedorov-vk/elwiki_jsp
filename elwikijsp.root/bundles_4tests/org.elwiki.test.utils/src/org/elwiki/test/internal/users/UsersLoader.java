package org.elwiki.test.internal.users;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.elwiki.authorize.user.DefaultUserProfile;
import org.elwiki.test.internal.bundle.TestUtilsActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.useradmin.UserAdmin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

@SuppressWarnings("unused")
@Deprecated //:FVK: -- old code (reading a JSON file from a bundle...)
public class UsersLoader {

	static final Logger log = Logger.getLogger(UsersLoader.class);

	private static final String UID = "uid";

	private static final String LOGIN_NAME = "loginName";

	private static final String FULL_NAME = "fullName";

	private static final String PASSWORD = "password";

	private static final String CREATED = "created";

	private static final String EMAIL = "email";

	private static final String LAST_MODIFIED = "lastModified";

	private static final String LOCK_EXPIRY = "lockExpiry";

	private static final String DATE_FORMAT = "yyyy.MM.dd 'at' HH:mm:ss:SSS z";

	private UserAdmin userAdmin;

	// == CODE ================================================================
	
	public UsersLoader() {
		super();
		BundleContext context = TestUtilsActivator.getContext();

		ServiceReference<?> ref;
		ref = context.getServiceReference(UserAdmin.class.getName());
		if (ref != null) {
			this.userAdmin = (UserAdmin) context.getService(ref);
		}

	}

	static final String filePath = "WS/data/users.json";

	public void test() {
		String response = "{\"name\":\"Ivan\",\"surname\":Petrov,\"age\":40}";

		response = "{\"name\":\"Dmitry\",\"age\":35}";

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Customer customer = gson.fromJson(response, Customer.class);

		System.out.println("Имя: " + customer.getName() + "\nФамилия: " + customer.getSurname() + "\nВозраст: "
				+ customer.getAge());

		Customer c = new Customer("Dmitry", "Petrov", 35);
		String json = gson.toJson(c);
		System.out.println(json);
	}

}