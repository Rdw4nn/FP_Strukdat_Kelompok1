package algorithm;

import model.Graph;
import model.Edge;
import model.Node;

import java.util.*;

public class BFS {

    // Class HasilBFS menyimpan hasil pencarian BFS, termasuk rute yang ditemukan, total waktu, total biaya, jumlah transit, dan apakah rute ditemukan atau tidak
    public static class HasilBFS {
        // urutan node dari asal ke tujuan (list ID node)
        public List<String> rute;      
        // akumulasi waktu dan biaya sepanjang rute (hanya dihitung untuk edge yang aktif)
        public int totalWaktu;
        // total biaya sepanjang rute (hanya dihitung untuk edge yang aktif)
        public int totalBiaya;
        // jumlah perpindahan antar node
        public int jumlahTransit;       
        // status apakah rute ditemukan atau tidak
        public boolean ditemukan;

        public HasilBFS(List<String> rute, int totalWaktu, int totalBiaya, boolean ditemukan) {
            this.rute = rute;
            this.totalWaktu = totalWaktu;
            this.totalBiaya = totalBiaya;
            // jumlah transit adalah jumlah node dalam rute dikurangi 1 (karena tidak dihitung node awal)
            this.jumlahTransit = ditemukan ? rute.size() - 1 : 0;
            this.ditemukan = ditemukan;
        }
    }

    public static HasilBFS cariMinimumTransit(Graph graph, String asalId, String tujuanId) {

        // Validasi node asal dan tujuan ada di graph
        if (!graph.containsNode(asalId) || !graph.containsNode(tujuanId)) {
            System.out.println("Node asal atau tujuan tidak ditemukan di graph.");
            return new HasilBFS(new ArrayList<>(), -1, -1, false);
        }

        // Jika asal dan tujuan sama, tidak perlu pencarian
        if (asalId.equals(tujuanId)) {
            List<String> rute = new ArrayList<>();
            rute.add(asalId);
            // Waktu, biaya, dan transit bernilai 0 karena tidak berpindah node
            return new HasilBFS(rute, 0, 0, true);
        }

        // Queue menyimpan path (list ID) yang sedang dijelajahi
        Queue<List<String>> queue = new LinkedList<>();

        // Set untuk melacak node yang sudah dikunjungi agar tidak terjadi loop
        Set<String> sudahDikunjungi = new HashSet<>();

        // Masukkan path awal berisi hanya node asal
        List<String> pathAwal = new ArrayList<>();
        pathAwal.add(asalId);

        // masukkan jalur awal ke queue dan tandai asal sebagai sudah dikunjungi
        queue.add(pathAwal);
        sudahDikunjungi.add(asalId);

        // loop selama masih ada jalur yang perlu dieksplorasi
        while (!queue.isEmpty()) {
            // Mengambil jalur terdepan dari queue
            List<String> pathSekarang = queue.poll();
            // Node terakhir dalam jalur dianggap sebagai posisi saat ini
            String nodeSekarang = pathSekarang.get(pathSekarang.size() - 1);

            // Cek semua tetangga dari node sekarang
            for (Edge edge : graph.getNeighbors(nodeSekarang)) {

                // Skip edge yang sedang nonaktif 
                if (!edge.isAktif()){ 
                    continue;
                }

                String idTetangga = edge.getTujuan().getId();

                // jika node tetangga nonaktif, skip 
                if (edge.getTujuan().getStatus().equalsIgnoreCase("Nonaktif")) continue;
                // Kalau sudah sampai tujuan, hitung total & kembalikan
                if (idTetangga.equals(tujuanId)) {
                    List<String> pathFinal = new ArrayList<>(pathSekarang);
                    pathFinal.add(idTetangga);

                    // Hitung total waktu dan biaya
                    int[] totalWaktuBiaya = hitungTotalWaktuBiaya(graph, pathFinal);
                    return new HasilBFS(pathFinal, totalWaktuBiaya[0], totalWaktuBiaya[1], true);
                }

                // Kalau node tetangga belum dikunjungi, tambahkan ke queue untuk dieksplorasi pada level berikutnya
                if (!sudahDikunjungi.contains(idTetangga)) {
                    sudahDikunjungi.add(idTetangga);
                    List<String> pathBaru = new ArrayList<>(pathSekarang);
                    pathBaru.add(idTetangga);
                    queue.add(pathBaru);
                }
            }
        }

        // Tidak ada rute ditemukan
        return new HasilBFS(new ArrayList<>(), -1, -1, false);
    }

    public static int[] hitungTotalWaktuBiaya(Graph graph, List<String> path) {
        int totalWaktu = 0;
        int totalBiaya = 0;

        // Menelusuri setiap pasangan node yang berurutan dalam jalur
        for (int i = 0; i < path.size() - 1; i++) {
            String dari = path.get(i);
            String ke = path.get(i + 1);

            // Mencari edge yang menghubungkan kedua node tersebut
            for (Edge edge : graph.getNeighbors(dari)) {
                if (edge.isAktif() && edge.getTujuan().getId().equals(ke)) {
                    totalWaktu += edge.getWaktuMenit();
                    totalBiaya += edge.getBiayaRupiah();
                    break;
                }
            }
        }

        return new int[]{totalWaktu, totalBiaya};
    }
}
