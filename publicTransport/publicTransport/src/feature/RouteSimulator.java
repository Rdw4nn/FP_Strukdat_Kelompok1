package feature;

import algorithm.BFS;
import model.Edge;
import model.Graph;
import model.Node;

import java.util.List;
import java.util.Scanner;

public class RouteSimulator {

    private final Graph graph;

    public RouteSimulator(Graph graph) {
        this.graph = graph;
    }

    /**
     * Fitur: Simulasikan rute tidak tersedia.
     * User bisa nonaktifkan edge tertentu, lalu lihat efeknya ke pencarian rute.
     * Edge tidak dihapus dari graph, hanya diset aktif = false sementara.
     */
    public void runFeature(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("   SIMULASI RUTE TIDAK TERSEDIA");
        System.out.println("========================================");

        boolean lanjut = true;
        while (lanjut) {
            System.out.println("\nPilih aksi:");
            System.out.println("  1. Nonaktifkan sebuah rute");
            System.out.println("  2. Aktifkan kembali sebuah rute");
            System.out.println("  3. Lihat semua rute yang nonaktif");
            System.out.println("  4. Cari rute minimum transit setelah simulasi");
            System.out.println("  0. Kembali ke menu utama");
            System.out.print("Pilih: ");

            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    nonaktifkanRute(scanner);
                    break;
                case "2":
                    aktifkanKembali(scanner);
                    break;
                case "3":
                    tampilkanRuteNonaktif();
                    break;
                case "4":
                    cariRuteSetelahSimulasi(scanner);
                    break;
                case "0":
                    // Sebelum keluar, aktifkan semua rute kembali
                    aktifkanSemuaRute();
                    lanjut = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Nonaktifkan edge berdasarkan ID edge (contoh: "E01")
    private void nonaktifkanRute(Scanner scanner) {
        System.out.print("Masukkan ID edge yang ingin dinonaktifkan (contoh: E01): ");
        String idEdge = scanner.nextLine().trim().toUpperCase();

        boolean ditemukan = false;
        for (String nodeId : graph.getAllNodeIds()) {
            for (Edge edge : graph.getNeighbors(nodeId)) {
                if (edge.getIdEdge().equalsIgnoreCase(idEdge)) {
                    if (!edge.isAktif()) {
                        System.out.println("Rute " + idEdge + " sudah dalam kondisi nonaktif.");
                    } else {
                        edge.setAktif(false);
                        System.out.println("Rute " + idEdge + " berhasil dinonaktifkan.");
                        System.out.println("  (" + nodeId + " -> " + edge.getTujuan().getId()
                                + " | " + edge.getJenisTransportasi() + ")");
                    }
                    ditemukan = true;
                    break;
                }
            }
            if (ditemukan) break;
        }

        if (!ditemukan) {
            System.out.println("ERROR: Edge dengan ID '" + idEdge + "' tidak ditemukan.");
            System.out.println("Tip: ID edge ada di dataset, contoh: E01, E02, dst.");
        }
    }

    // Aktifkan kembali edge yang sudah dinonaktifkan
    private void aktifkanKembali(Scanner scanner) {
        System.out.print("Masukkan ID edge yang ingin diaktifkan kembali (contoh: E01): ");
        String idEdge = scanner.nextLine().trim().toUpperCase();

        boolean ditemukan = false;
        for (String nodeId : graph.getAllNodeIds()) {
            for (Edge edge : graph.getNeighbors(nodeId)) {
                if (edge.getIdEdge().equalsIgnoreCase(idEdge)) {
                    if (edge.isAktif()) {
                        System.out.println("Rute " + idEdge + " sudah aktif, tidak perlu diaktifkan lagi.");
                    } else {
                        edge.setAktif(true);
                        System.out.println("Rute " + idEdge + " berhasil diaktifkan kembali.");
                    }
                    ditemukan = true;
                    break;
                }
            }
            if (ditemukan) break;
        }

        if (!ditemukan) {
            System.out.println("ERROR: Edge dengan ID '" + idEdge + "' tidak ditemukan.");
        }
    }

    // Tampilkan semua rute yang sedang nonaktif
    private void tampilkanRuteNonaktif() {
        System.out.println("\n--- Rute yang Sedang Nonaktif ---");
        boolean adaYangNonaktif = false;

        for (String nodeId : graph.getAllNodeIds()) {
            for (Edge edge : graph.getNeighbors(nodeId)) {
                if (!edge.isAktif()) {
                    Node dari   = graph.getNode(nodeId);
                    Node tujuan = edge.getTujuan();
                    System.out.println("  [" + edge.getIdEdge() + "] "
                            + dari.getNama() + " -> " + tujuan.getNama()
                            + " (" + edge.getJenisTransportasi() + ")"
                            + " | " + edge.getWaktuMenit() + " menit"
                            + " | Rp " + String.format("%,d", edge.getBiayaRupiah()));
                    adaYangNonaktif = true;
                }
            }
        }

        if (!adaYangNonaktif) {
            System.out.println("  Semua rute sedang aktif.");
        }
        System.out.println("---------------------------------");
    }

    // Cari rute minimum transit setelah ada simulasi nonaktif
    private void cariRuteSetelahSimulasi(Scanner scanner) {
        System.out.println("\n[Mencari rute dengan kondisi simulasi saat ini]");
        tampilkanRuteNonaktif();

        System.out.print("Masukkan ID asal   (contoh: S01): ");
        String asalId = scanner.nextLine().trim().toUpperCase();

        System.out.print("Masukkan ID tujuan (contoh: B01): ");
        String tujuanId = scanner.nextLine().trim().toUpperCase();

        if (!graph.containsNode(asalId) || !graph.containsNode(tujuanId)) {
            System.out.println("ERROR: ID node tidak ditemukan.");
            return;
        }

        BFS.HasilBFS hasil = BFS.cariMinimumTransit(graph, asalId, tujuanId);

        if (!hasil.ditemukan) {
            System.out.println("\nTidak ada rute yang tersedia dengan kondisi simulasi saat ini.");
            System.out.println("Coba aktifkan kembali beberapa rute yang nonaktif.");
        } else {
            System.out.println("\nRute ditemukan dengan " + hasil.jumlahTransit + " transit:");
            List<String> rute = hasil.rute;
            for (int i = 0; i < rute.size(); i++) {
                Node node = graph.getNode(rute.get(i));
                System.out.println("  " + (i + 1) + ". [" + node.getId() + "] " + node.getNama());
            }
            System.out.println("  Total Waktu : " + hasil.totalWaktu + " menit");
            System.out.println("  Total Biaya : Rp " + String.format("%,d", hasil.totalBiaya));
        }
    }

    // Reset: aktifkan semua rute kembali saat keluar dari simulasi
    private void aktifkanSemuaRute() {
        int jumlah = 0;
        for (String nodeId : graph.getAllNodeIds()) {
            for (Edge edge : graph.getNeighbors(nodeId)) {
                if (!edge.isAktif()) {
                    edge.setAktif(true);
                    jumlah++;
                }
            }
        }
        if (jumlah > 0) {
            System.out.println("\n[OK] Simulasi selesai. " + jumlah + " rute telah diaktifkan kembali.");
        } else {
            System.out.println("\nKembali ke menu utama.");
        }
    }
}