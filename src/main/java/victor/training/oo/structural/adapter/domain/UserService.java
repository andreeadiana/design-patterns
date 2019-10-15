package victor.training.oo.structural.adapter.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service // ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN ZEN
public class UserService {
//	@EJB
	private final ILdapServiceAdapter ldapService;

	public UserService(ILdapServiceAdapter ldapService) {
		this.ldapService = ldapService;
	}

	public void importUserFromLdap(String username) {
		List<User> list = ldapService.searchByUsername(username);
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
		return ldapService.searchByUsername(username);
	}

}
