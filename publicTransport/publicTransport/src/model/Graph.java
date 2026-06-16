package model;

import java.util.*;

public class Graph {
    private Map<String, Node> nodes = new HashMap<>();      // key = id node
    private Map<String, List<Edge>> adjList = new HashMap<>(); // key = id node asal

    public void addNode(Node node) {
        nodes.putIfAbsent(node.getId(), node);
        adjList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(String idEdge, String dariId, String tujuanId, int waktuMenit, int biayaRupiah, String jenisTransportasi) {
        Node tujuanNode = nodes.get(tujuanId);
        Node dariNode   = nodes.get(dariId);
        if (tujuanNode == null) {
            throw new IllegalArgumentException("Node tujuan " + tujuanId + " belum ditambahkan");
        }
        if (dariNode == null) {
            throw new IllegalArgumentException("Node asal " + dariId + " belum ditambahkan");
        }

        if (dariNode.getStatus().equalsIgnoreCase("Nonaktif") || tujuanNode.getStatus().equalsIgnoreCase("Nonaktif")) {
        System.out.println("Edge " + idEdge + " dilewati node yang nonaktif.");
        return;
        }

        adjList.get(dariId).add(new Edge(idEdge, tujuanNode, waktuMenit, biayaRupiah, jenisTransportasi));
    }

    // dipakai buat fitur "simulasikan rute tidak tersedia" (cara 1: hapus dari list)
    public void removeEdge(String dariId, String tujuanId) {
        List<Edge> edges = adjList.get(dariId);
        if (edges != null) {
            edges.removeIf(e -> e.getTujuan().getId().equals(tujuanId));
        }
    }

    public void updateBobot(String dariId, String tujuanId, int waktuBaru, int biayaBaru) {
    for (Edge e : adjList.get(dariId)) {
        if (e.getTujuan().getId().equals(tujuanId)) {
            e.setWaktuMenit(waktuBaru);
            e.setBiayaRupiah(biayaBaru);
            System.out.println("Bobot edge " + dariId + "->" + tujuanId + " berhasil diubah.");
            return;
        }
    }
    }

    public List<Edge> getNeighbors(String nodeId) {
        return adjList.getOrDefault(nodeId, new ArrayList<>());
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public boolean containsNode(String id) {
        return nodes.containsKey(id);
    }

    public Set<String> getAllNodeIds() {
        return nodes.keySet();
    }

    public int jumlahNode() {
        return nodes.size();
    }

    public int jumlahEdge() {
        int total = 0;
        for (List<Edge> e : adjList.values()) {
            total += e.size();
        }
        return total;
    }
}