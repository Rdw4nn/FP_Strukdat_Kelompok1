import model.*;
import algorithm.*;
import feature.*;

import java.io.*;
import java.util.*;

public class App {

    // =====================================================
    // LOAD DATA DARI CSV
    // =====================================================
    static Graph loadGraph() throws Exception {
        Graph graph = new Graph();

        // Load nodes
        BufferedReader nodeReader = new BufferedReader(new FileReader("../../../data/nodes.csv"));
        String line = nodeReader.readLine(); // skip header
        while ((line = nodeReader.readLine()) != null) {
            String[] col = line.split(",", 6);
            // id, nama, jenis, area, fasilitas, status
            graph.addNode(new Node(col[0].trim(), col[1].trim(), col[2].trim(),
                                   col[3].trim(), col[4].trim(), col[5].trim()));
        }
        nodeReader.close();

        // Load edges
        BufferedReader edgeReader = new BufferedReader(new FileReader("../../../data/edges.csv"));
        edgeReader.readLine(); // skip header
        while ((line = edgeReader.readLine()) != null) {
            String[] col = line.split(",");
            // id_edge, dari, ke, waktu_menit, biaya_rupiah, jenis_transportasi
            graph.addEdge(col[0].trim(), col[1].trim(), col[2].trim(),
                          Integer.parseInt(col[3].trim()),
                          Integer.parseInt(col[4].trim()),
                          col[5].trim());
        }
        edgeReader.close();
        return graph;
    }

    static Trie loadTrie(Graph graph) {
        Trie trie = new Trie();
        for (String id : graph.getAllNodeIds()) {
            Node node = graph.getNode(id);
            trie.insert(node.getNama(), node);
        }
        return trie;
    }

    // =====================================================
    // FITUR INTEGRASI TREE + GRAPH
    // User cari halte pakai Trie, lalu BFS jalan
    // =====================================================
    static void fiturIntegrasiTrieBFS(Graph graph, Trie trie, Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("  CARI HALTE + RUTE MINIMUM TRANSIT");
        System.out.println("  (Integrasi Trie + BFS)");
        System.out.println("========================================");

        // Langkah 1: Cari halte ASAL pakai Trie
        System.out.print("Ketik awalan nama halte ASAL: ");
        String prefixAsal = scanner.nextLine().trim();
        List<Node> hasilAsal = trie.searchByPrefix(prefixAsal);

        if (hasilAsal.isEmpty()) {
            System.out.println("Halte tidak ditemukan.");
            return;
        }

        System.out.println("Pilih halte ASAL:");
        for (int i = 0; i < hasilAsal.size(); i++) {
            System.out.println("  " + (i+1) + ". [" + hasilAsal.get(i).getId() + "] " + hasilAsal.get(i).getNama());
        }
        System.out.print("Nomor: ");
        int pilihanAsal = Integer.parseInt(scanner.nextLine().trim()) - 1;
        String asalId = hasilAsal.get(pilihanAsal).getId();

        // Langkah 2: Cari halte TUJUAN pakai Trie
        System.out.print("Ketik awalan nama halte TUJUAN: ");
        String prefixTujuan = scanner.nextLine().trim();
        List<Node> hasilTujuan = trie.searchByPrefix(prefixTujuan);

        if (hasilTujuan.isEmpty()) {
            System.out.println("Halte tidak ditemukan.");
            return;
        }

        System.out.println("Pilih halte TUJUAN:");
        for (int i = 0; i < hasilTujuan.size(); i++) {
            System.out.println("  " + (i+1) + ". [" + hasilTujuan.get(i).getId() + "] " + hasilTujuan.get(i).getNama());
        }
        System.out.print("Nomor: ");
        int pilihanTujuan = Integer.parseInt(scanner.nextLine().trim()) - 1;
        String tujuanId = hasilTujuan.get(pilihanTujuan).getId();

        // Langkah 3: Jalankan BFS
        BFS.HasilBFS hasil = BFS.cariMinimumTransit(graph, asalId, tujuanId);

        if (!hasil.ditemukan) {
            System.out.println("Tidak ada rute yang tersedia.");
            return;
        }

        System.out.println("\nRute minimum transit ditemukan (" + hasil.jumlahTransit + " transit):");
        for (int i = 0; i < hasil.rute.size(); i++) {
            Node n = graph.getNode(hasil.rute.get(i));
            System.out.println("  " + (i+1) + ". [" + n.getId() + "] " + n.getNama() + " (" + n.getJenis() + ")");
        }
        System.out.println("Total Waktu : " + hasil.totalWaktu + " menit");
        System.out.println("Total Biaya : Rp " + String.format("%,d", hasil.totalBiaya));
    }

    // =====================================================
    // MAIN MENU
    // =====================================================
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.setErr(new PrintStream(System.err, true, "UTF-8"));

        Scanner scanner = new Scanner(System.in);

        System.out.println("Memuat data...");
        Graph graph = loadGraph();
        Trie trie   = loadTrie(graph);

        System.out.println("Data berhasil dimuat!");
        System.out.println("Node: " + graph.jumlahNode() + " | Edge: " + graph.jumlahEdge());

        // Inisialisasi fitur
        PrefixSearcher    prefixSearcher    = new PrefixSearcher(trie);
        MinTransitFinder  minTransitFinder  = new MinTransitFinder(graph);
        RouteSimulator    routeSimulator    = new RouteSimulator(graph);

        boolean jalan = true;
        while (jalan) {
            System.out.println("\n========================================");
            System.out.println("   SISTEM TRANSPORTASI UMUM SURABAYA");
            System.out.println("========================================");
            System.out.println("1. Cari halte/stasiun berdasarkan nama");
            System.out.println("2. Cari rute TERCEPAT (Dijkstra)");
            System.out.println("3. Cari rute TERMURAH (Dijkstra)");
            System.out.println("4. Cari rute MINIMUM TRANSIT (BFS)");
            System.out.println("5. Bandingkan dua kriteria rute (Dijkstra)");
            System.out.println("6. Simulasi rute tidak tersedia");
            System.out.println("7. Cari halte + rute sekaligus [INTEGRASI]");
            System.out.println("0. Keluar");
            System.out.println("========================================");
            System.out.print("Pilih menu: ");

            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    // Fitur Orang 2
                    prefixSearcher.runFeature(scanner);
                    break;

                case "2":
                    // Fitur Orang 1 - Dijkstra tercepat
                    System.out.print("ID Asal  : "); String a2 = scanner.nextLine().trim().toUpperCase();
                    System.out.print("ID Tujuan: "); String t2 = scanner.nextLine().trim().toUpperCase();
                    Djikstra.HasilRute r2 = Djikstra.cari(graph, a2, t2, "waktu");
                    if (r2.ditemukan) {
                        System.out.println("Rute: " + r2.rute);
                        System.out.println("Total waktu: " + r2.totalBobot + " menit");
                    } else System.out.println("Rute tidak ditemukan.");
                    break;

                case "3":
                    // Fitur Orang 1 - Dijkstra termurah
                    System.out.print("ID Asal  : "); String a3 = scanner.nextLine().trim().toUpperCase();
                    System.out.print("ID Tujuan: "); String t3 = scanner.nextLine().trim().toUpperCase();
                    Djikstra.HasilRute r3 = Djikstra.cari(graph, a3, t3, "biaya");
                    if (r3.ditemukan) {
                        System.out.println("Rute: " + r3.rute);
                        System.out.println("Total biaya: Rp " + r3.totalBobot);
                    } else System.out.println("Rute tidak ditemukan.");
                    break;

                case "4":
                    // Fitur Orang 3 (KAMU) - BFS
                    minTransitFinder.runFeature(scanner);
                    break;

                case "5":
                    // Fitur Orang 1 - Bandingkan dua kriteria
                    System.out.print("ID Asal  : "); String a5 = scanner.nextLine().trim().toUpperCase();
                    System.out.print("ID Tujuan: "); String t5 = scanner.nextLine().trim().toUpperCase();
                    Djikstra.bandingkanRute(graph, a5, t5);
                    break;

                case "6":
                    // Fitur Orang 3 (KAMU) - Simulasi
                    routeSimulator.runFeature(scanner);
                    break;

                case "7":
                    // Integrasi Trie + BFS (Orang 2 + Orang 3)
                    fiturIntegrasiTrieBFS(graph, trie, scanner);
                    break;

                case "0":
                    System.out.println("Program selesai. Terima kasih!");
                    jalan = false;
                    break;

                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
        scanner.close();
    }
}