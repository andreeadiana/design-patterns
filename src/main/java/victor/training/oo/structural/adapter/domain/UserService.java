package victor.training.oo.structural.adapter.domain;

import lombok.extern.slf4j.Slf4j;
import victor.training.oo.structural.adapter.infra.LdapUser;
import victor.training.oo.structural.adapter.infra.LdapUserWebserviceClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j

// ZEN
public class UserService {
	private final LdapUserWebserviceClient wsClient;

	public UserService(LdapUserWebserviceClient wsClient) {
		this.wsClient = wsClient;
	}

	public void importUserFromLdap(String username) {
		List<User> list = searchByUsername(username);
		if (list.size() != 1) {
			throw new IllegalArgumentException("There is no single user matching username " + username);
		}
		User user = list.get(0);

		if (user.getWorkEmail() != null) {
			log.debug("Send welcome email to " + user.getWorkEmail());
		}
		log.debug("Insert user in my database");
	}

	public List<User> searchUserInLdap(String username) {
		return searchByUsername(username);
	}

	// o linie -----------------------------------------------

	private User transformUser(LdapUser ldapUser) {
		String fullName = getFullName(ldapUser);
		return new User(ldapUser.getuId(), fullName, ldapUser.getWorkEmail());
	}

	private String getFullName(LdapUser ldapUser) {
		return ldapUser.getfName() + " " + ldapUser.getlName().toUpperCase();
	}

	private List<User> searchByUsername(String username) {
		List<LdapUser> ldapUsers = wsClient.search(username.toUpperCase(), null, null);
		List<User> users = new ArrayList<>();
		for (LdapUser ldapUser : ldapUsers) {
			User user = transformUser(ldapUser);
			users.add(user);
		}
		return users;
	}

}
