package model;
import algorithm.Djikstra;

public class TesProgram {
    public static void main(String[] args) {
        Graph g = new Graph();

        g.addNode(new Node("A", "Halte A", "bus", "Surabaya Pusat", "Tempat duduk, atap", "aktif"));
        g.addNode(new Node("B", "Halte B", "bus", "Surabaya Pusat", "Tempat duduk", "aktif"));
        g.addNode(new Node("C", "Halte C", "kereta", "Surabaya Timur", "Tempat duduk, toilet", "aktif"));
        g.addNode(new Node("D", "Halte D", "bus", "Surabaya Timur", "Atap", "aktif"));

        g.addEdge("E1", "A", "B", 10, 3000, "bus");
        g.addEdge("E2", "B", "C", 5, 2000, "kereta");
        g.addEdge("E3", "A", "C", 20, 4000, "bus");
        g.addEdge("E4", "C", "D", 8, 2500, "bus");
        g.addEdge("E5", "B", "D", 15, 1000, "bus");

        System.out.println("Jumlah node: " + g.jumlahNode());
        System.out.println("Jumlah edge: " + g.jumlahEdge());
        System.out.println();

        Djikstra.bandingkanRute(g, "A", "D");

        System.out.println();
        System.out.println("Simulasi: edge B -> D dihapus");
        g.removeEdge("B", "D");
        Djikstra.bandingkanRute(g, "A", "D");
    }
}