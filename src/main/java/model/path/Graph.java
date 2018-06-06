package model.path;

import model.Position;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Graph {
    static private Node nodes[][];
    int agentId;
    Position target;
    public enum direction{none, up, down, left, right}

    public static synchronized void setFree(Position pos, boolean free) {
        nodes[pos.getX()][pos.getY()].setFree(free);
    }

    public Graph(int id, Position t) {
        agentId = id;
    }

    public void setTarget(Position t) {
        target = new Position(t.getX(), t.getY());
        for(int i = 0; i < nodes.length; i++) {
            for(int j = 0; j < nodes[i].length; i++) {
                nodes[i][j].setHeuristics(target, agentId);
            }
        }
    }

    private int compare(Node n1, Node n2) {
        return Integer.compare(n2.getHeuristic(agentId), n1.getHeuristic(agentId));
    }

    public void AStar(Position src) {
        /*
        closedList = File()
        openList = FilePrioritaire(comparateur=compare2Noeuds)
        openList.ajouter(depart)
        tant que openList n'est pas vide
        u = openList.depiler()
        si u.x == objectif.x et u.y == objectif.y
        reconstituerChemin(u)
        terminer le programme
        pour chaque voisin v de u dans g
        si v existe dans closedList avec un cout inférieur ou si v existe dans openList avec un cout inférieur
        neRienFaire()
        sinon
        v.cout = u.cout +1
        v.heuristique = v.cout + distance([v.x, v.y], [objectif.x, objectif.y])
        openList.ajouter(v)
        closedList.ajouter(u)
        terminer le programme (avec erreur)
        */
    }
}
