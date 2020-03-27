package fr.leomelki.loupgarou.classes;

import fr.leomelki.loupgarou.roles.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class IndexedRole {
	@Getter
	private final Role role;
	@Setter
	@Getter
	private int number = 1;
}
