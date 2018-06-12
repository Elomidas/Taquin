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

**TODO**
