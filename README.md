# Taquin

## Informations

### Étudiants
* JACOUD Bastien
* REMOND Victor

### Le projet
* Langage : Java
* Bibliothèque graphique : Java FX
* [Projet Git](https://github.com/Elomidas/Taquin)

## Introduction
Le but de ce projet était de mettre en place un système multi-agents permettant de résoudre un [jeu de taquin](https://fr.wikipedia.org/wiki/Taquin).

## I - Agents

Nos agents sont tous des classes filles de la classe `Thread` et chaque pièce du jeu est un agent.

Pour savoir dans quelle direction un agent doit se déplacer, nous avons décidé d'appliquer un [algorithme A*](https://fr.wikipedia.org/wiki/Algorithme_A*) (cf. notre classe `model.path.Graph`).

Afin de pouvoir gérer un plateau relativement rempli, nous avons donné à chaque agent une priorité dépendant de la position de son but sur le plateau. En effet, nous avons cherché "la plus grosse concentration de cases vides attendues", c'est à dire la zone sur laquelle, une fois le taquin terminée, il y aurait le plus de cases vides adjacentes. On essaye ainsi de garder cette zone pour la fin, car c'est celle dans laquelle il sera le plus facile de déplacer les derniers pions.

Afin d'éviter les erreurs de synchronisation avec l'interface graphique comme on avait au début, nous avons ajouté un système de token :
* Par défaut, le plateau a un *token*.
* Lorsqu'un agent veut bouger, il demande le token au plateau.
* On a alors deux possibilités :
  * L'agent a réussi à bouger, le thread UI redonne le *token* au plateau après avoir actualisé l'affichage.
  * L'agent n'a pas pu bougé, le *token* est rendu au plateau, aucun update de l'affichage est nécessaire.

On commence par placer les pions les plus prioritaires (sur les bords), les moins prioritaires sont à l'écoute des messages qui leur sont envoyés, afin de faciliter le déplacement des pions prioritaires.

Afin d'éviter un blocage général du système, on vérifie qu'un agent n'est pas déjà occupé avant de lui envoyer une requête de déplacement.

## II - Communication

Pour gérer les communications entre les agents, nous avons créé une classe `model.communication.Messages`, regroupant la totalité des messages (représentés individuellement par la classe `model.communication.Message`). Les messages sont triés par expéditeur (id de l'agent l'ayant envoyé) dans des `HashMap<Integer, HashMap<Message.performs, Queue<Message>>>`, par type (grâce à l'énumération `Message.performs`) dans des `HashMap<Message.performs, Queue<Message>>`, puis par priorité (grâce à la priorité de l'agent expéditeur) dans la `Queue<Message>`.

Lorsqu'un agent demande à récupérer un message (en précisant s'il veut un message de type *request* ou *response*), on récupère celui qui se trouve en première position dans la queue correspondante et on le retourne.

## III - Plateau

## IV - Interface Graphique

Afin de mieux visualiser notre plateau du Taquin ainsi que les différentes pièces qui le compose, nous avons décidé de mettre en place une interface graphique. Cette dernière a été réalisée à l'aide de la librairie graphique Javafx, ainsi qu'à l'aide de vues fxml.

Dans un premier temps, nous avons créé nos vues fxml qui vont composer l'application, c'est à dire : **root.fxml**, qui va être le support de notre application, **settings.fxml**, qui va être notre première vue appelée, nous permettant de sélectionner tous les paramètres avant de lancer le jeu, et enfin **game.fxml** qui va être notre composant principal contenant notre grille de taquin.

Nous avons alors créé deux controlleurs pour pouvoir gérer les deux vues principales : **SettingsController** et **GameController**. Nos deux controlleurs héritent d'une classe commune possédant une référence vers le **Main** ainsi qu'une référence vers notre classe **Board**, contenant tous les objets nécessaires pour notre taquin. Chaque classe redéfinit également la fonction ```setMain(Board board, Main main)``` permettant de définir le main à utiliser ainsi que le plateau de jeu qui va être utilisé.

En pratique, l'application se lance sur la fenêtre de settings, et lors d'un appui sur le bouton ```launch```, l'objet **Board** est alors créé et la partie se lance. Grâce à notre main, nous changeons de fenêtre, pour visualiser notre fenêre de jeu. En fonction des paramètres de notre plateau, la grille est automatiquement créée et centrée dans la fenêtre de jeu. 

Afin de mettre à jour la fenêtre graphique en temps réel, nous avons fait le choix d'utiliser le pattern Observable/Observer. Pour cela, notre objet **GameController** implements l'interface **Observer** de *java.util* et notre objet **Board** hérite de la classe **Observable** de *java.util*. Au lancement de notre application, dans la fonction ```setMain()```, nous ajoutons à notre plateau l'observer de la manière suivante : ```super.board.addObserver(this);```. 

Nous avons alors redéfinit la méthode ```public void update(Observable observable, Object o)``` dans notre controlleur afin qu'elle mette à jour la grille lors du mouvement d'un agent. Pour cela, nous passons en paramètre un tableau de **Position** composé de l'ancienne et de la nouvelle position de l'objet qui vient de se déplacer. Dans notre classe **Agent**, lorsque l'agent en question effectue un mouvement, nous appelons deux fonctions : ```_board.setChanged();``` pour indiquer qu'il y a eu un changement sur le plateau, et ```_board.notifyObservers(new Position[]{oldPos, new Position(position)});``` pour appeler la fonction *Update()* de notre controlleur, avec l'ancienne et la nouvelle position.

De cette manière, notre grille est mise à jour en temps réel lorsqu'un agent bouge sur le plateau, c'est à dire lorsque notre **Board** est modifié.

