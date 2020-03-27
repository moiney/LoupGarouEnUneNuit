package fr.leomelki.loupgarou.classes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LGWinType {
	VILLAGEOIS("§6§l§oLa partie a été gagnée par le §2§lVillage§6§l§o !"),
	TANNEUR("§6§l§oLa partie a été gagnée par le §c§Tanneur§6§l§o !"),
	LOUPGAROU("§6§l§oLa partie a été gagnée par les §c§lLoups-Garous§6§l§o !");

	@Getter
	private final String message;
}
