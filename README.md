# Rise & Fall

Rise & Fall est un jeu de stratégie dans un monde médiéval fantasy ou vous pouvez incarner une race pour dominer le
continent sur lequel vous vous trouvez grâce à des batiments et votre armée.

Pour accomplir vos objectifs, un choix primordial vous sera demandé : la race que vous représentez.
Celle-ci aura des conséquences sur la suite de votre partie, en particulier dans les bonus qu'elle confère ainsi que les
batiments et unités spécialisés. Elle sont au nombre de sept : Humain, Elfe, Nain, Mort-Vivant, Orc, Nerlk et
Primotaure.

Après avoir rejoint une partie, plusieurs options s'offriront à vous lors des différents tours : construire des
batiments, former des troupe, discuter avec d'autres joueurs, et attaquer d'autre joueurs. Ce cycle se répète jusqu'à ce
qu'il en reste plus qu'un (inspiration tiré de Koh Lanta), ou bien après le 50ème tour.

Seigneurs, Seigneuresses, puisse le sort vous être favorable !

## Architecture du projet

Le projet est divisé en plusieurs modules :

- **Front** : Contient le code du front-end du jeu, c'est-à-dire l'interface utilisateur et les interactions avec le
  serveur. Il utilise le framework JavaFX pour l'interface graphique.
- **Server** : Contient le code du back-end du jeu, c'est-à-dire la logique du jeu, la gestion des données et les
  interactions avec la base de données.
- **Common** : Contient le code commun aux deux modules, comme les modèles de données, les utilitaires et les paquets
  échangés entre le client et le serveur.

Le projet utilise Maven pour la gestion des dépendances et la construction du projet.

## Installation

Pour installer le projet, vous devez avoir Maven et Java 21 installés sur votre machine. Ensuite, vous pouvez cloner le
dépôt et exécuter les commandes suivantes :

```bash
git clone https://github.com/Kevin-OVI/RiseAndFall.git
cd RiseAndFall
mvn clean install
```

Afin de lancer le serveur, il faudra créer un fichier .env à la racine du projet contenant les variables d'environnement
nécessaires au bon fonctionnement du serveur. Un exemple de fichier .env est fourni dans le dépôt.

Pour lancer le serveur, vous pouvez utiliser la commande suivante :

```bash
mvn -pl Server exec:java -Dexec.mainClass="fr.butinfoalt.riseandfall.server.RiseAndFallServer"
```

Ou depuis un IDE, en lançant la classe `fr.butinfoalt.riseandfall.server.RiseAndFallServer`.

Le client ne peut être lancé que dans un IDE ou depuis un exécutable à cause de limitations de JavaFX.
Dans un IDE, l'application peut être lancée en exécutant la classe
`fr.butinfoalt.riseandfall.front.RiseAndFallApplication`.
Pour compiler en exécutable, il n'est pas possible d'utiliser la même classe que celle de l'application JavaFX. Il faut
donc utiliser la classe `fr.butinfoalt.riseandfall.front.AppRunner`.
