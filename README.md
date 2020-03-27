

  

## Table des matières

- [À propos](#à-propos)
- [Jouer facilement](#jouer-facilement)
- [Installation](#installation)
  - [Dépendances requises](#dépendances-requises)
  - [Installation classique](#installation-classique)
  - [Installation avec docker (Alternative)](#installation-avec-docker-alternative)
- [Commandes](#commandes)
- [Crédits](#crédits)
- [Aide](#aide)
  - [Questions fréquentes](#questions-fréquentes)

## Remerciements

Je tiens à remercier [Leomelki](https://twitter.com/leomelki) et [Shytoos](https://twitter.com/shytoos_) pour leur travail sur le mode original. J'ai repris une grande partie de leur base de code en forkant leur projet, n'hésitez pas à les remercier et à essayer leur mode de jeu.

## À propos

Le mode Loup-Garou en une Nuit est un mode inspiré du jeu de société [Loup Garou pour une Nuit](https://www.ravensburger.fr/produits/jeux-de-soci%C3%A9t%C3%A9/jeux-d-ambiance/loup-garou-pour-une-nuit-26681/index.html) reprenant son fonctionnement ainsi que sa manière d'être joué, à la seule différence qu'aucun maître du jeu n'est requis, le déroulement de chaque partie étant entièrement automatisé :

- Déroulement de la partie automatisé
- Rôles du jeu de base, et nouveaux rôles
- Utilisable sur n'importe quelle map

Ce mode est repris sur celui fait par [Leomelki et Shytoos](https://github.com/leomelki/LoupGarou), en le modifiant pour l'adapter aux règles du Loup-Garou pour une nuit. Les règles de cette variante sont disponibles [ici](https://www.iello.fr/regles/266814_regles.pdf) et [ici](https://regle.jeuxsoc.fr/lgpun_rg.pdf).

## Installation

### Dépendances requises
- [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

### Installation classique
**Minecraft 1.15.1 est requis.**  
Pour installer le plug-in, merci de suivre les étapes suivantes:
  - Téléchargez Spigot 1.15.1 et lancez une fois le serveur
  - Dans le dossier `plugins`, insérez [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) et [LoupGarou.jar](https://github.com/leomelki/LoupGarou/releases)
  - Redémarrez votre serveur puis donnez vous les permissions administrateur (/op <votre_pseudo> dans la console)
  - Allez sur la map et ajoutez les points de spawn sur chaque dalle `/lg addSpawn`
  - Allez sur la map et ajoutez les points de spawn sur chaque dalle `/lg addCenter` (il faut exactement 3 rôles au centre)
  - Connectez-vous au serveur et choisissez les rôles à utiliser avec `/lg roles set <ROLE> <MONTANT>`
	  - ⚠️ Il faut qu'il y ait autant de places dans les rôles que de joueur pour lancer une partie
  - Vous pouvez démarrer la partie avec `/lg start <pseudo>` 
	  - ⚠️ N'oubliez pas de mettre votre pseudo. Exemple : `/lg start leomelki` 

Lien des releases : [Cliquez ici](https://github.com/moiney/LoupGarouEnUneNuit/releases)


### Installation avec docker (Alternative)

Vous devez avoir installé `docker` et `docker-compose` sur votre machine

#### Installation du serveur
```sh
docker-compose up -d --build
```

#### Démarage du serveur

Vous devez exécuter la commande suivante à chaque redémarage de votre machine avant de pouvoir continuer

```sh
docker-compose up -d
```

Ainsi, vous pouvez lancer le serveur en utilisant la commande suivante :

```sh
docker-compose exec loup-garou java -jar spigot.jar
```

Les fichiers relatifs à minecraft se situeront dans le dossier `minecraft_data` 
> **Tip :** Il faut accepter les conditions d'utilisations de Mojang en modifiant le fichier `minecraft_data/eula.txt`

## Commandes

`/lg roles` : Retourne la liste des rôles dans la partie  
`/lg roles set <ID> <MONTANT>` : Définit le nombre de joueurs pour un certain rôle  
`/lg addSpawn` : Ajoute un point de spawn (emplacement de joueur)  
`/lg addCenter` : Ajoute un point pour les rôles du centre (emplacement de rôle du centre)  
`/lg start <PSEUDO>` : Lance la partie  
`/lg end <PSEUDO>` : Arrête une partie  
`/lg reloadConfig` : Recharge la configuration  
`/lg joinAll` : À utiliser après avoir changé les rôles  

## Crédits

- Chef de Projet : [Shytoos](https://twitter.com/shytoos_)
- Développement : [Leomelki](https://twitter.com/leomelki)
- Mapping : [Cosii](https://www.youtube.com/channel/UCwyOcA41QSk590fl9L0ys8A)
- Adaptation pour le mode en une nuit : Signoube

### Questions fréquentes

- Que faire en cas de problème d'affichage (votes bloqués au dessus des têtes, etc...) ?  

Cela arrive après avoir `reload` au cours d'une partie, tous les joueurs qui ont ce problème doivent se déconnecter et se reconnecter.

- Pourquoi la partie ne se lance pas ?  

Il faut taper la commande `/lg start <PSEUDO>` en mettant le pseudo d'un des joueurs qui sera présent dans la partie. Si cela ne fonctionne toujours pas, c'est parce qu'il n'y a pas suffisamment de rôles pour le nombre de joueurs, il doit y avoir le même nombre de rôles qu'il y aura de joueurs dans la partie. N'oubliez pas de taper `/lg joinAll` après avoir modifié la liste des rôles.

- J'ai mal placés mes spawns/centres ou je veux utiliser une nouvelle map, comment faire ?  

Il suffit d'ouvrir le fichier `plugins\LoupGarou\config.yml` et de supprimer les points de spawn.

- Puis-je mettre plusieurs fois le même rôle dans une seule partie ?

Cela est possible pour les rôles `Loup-Garou`, `Villageois` et `Franc-Maçon`. Évitez pour tous les autres rôles.

### Publier un rôle

Si vous arrivez à créer un rôle, je vous invite à faire une demande de publication dans cette repo afin de les faire partager à l'ensemble de la communauté !
