package victor.training.oo.structural.adapter.domain;

import lombok.extern.slf4j.Slf4j;
import victor.training.oo.structural.adapter.infra.LdapUser;
import victor.training.oo.structural.adapter.infra.LdapUserWebserviceClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserService {
	private final LdapUserWebserviceClient wsClient;

	public UserService(LdapUserWebserviceClient wsClient) {
		this.wsClient = wsClient;
	}

	public void importUserFromLdap(String username) {
		List<LdapUser> list = searchByUsername(username);
		if (list.size() != 1) {
			throw new IllegalArgumentException("There is no single user matching username " + username);
		}
		LdapUser ldapUser = list.get(0);
		User user = transformUser(ldapUser);

		if (user.getWorkEmail() != null) {
			log.debug("Send welcome email to " + user.getWorkEmail());
		}
		log.debug("Insert user in my database");
	}

	public List<User> searchUserInLdap(String username) {
		List<LdapUser> list = searchByUsername(username);
		List<User> results = new ArrayList<>();
		for (LdapUser ldapUser : list) {
			results.add(transformUser(ldapUser));
		}
		return results;
	}

	private User transformUser(LdapUser ldapUser) {
		String fullName = getFullName(ldapUser);
		return new User(ldapUser.getuId(), fullName, ldapUser.getWorkEmail());
	}

	private String getFullName(LdapUser ldapUser) {
		return ldapUser.getfName() + " " + ldapUser.getlName().toUpperCase();
	}

	private List<LdapUser> searchByUsername(String username) {
		return wsClient.search(username.toUpperCase(), null, null);
	}

}
