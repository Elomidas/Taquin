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
