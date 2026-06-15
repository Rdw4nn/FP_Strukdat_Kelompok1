package algorithm;
import java.util.*;
import model.Graph;
import model.Edge;

public class Djikstra {
    public static class HasilRute {
        public List<String> rute;
        public int totalBobot;
        public boolean ditemukan;

        public HasilRute(List<String> rute, int totalBobot, boolean ditemukan) {
            this.rute = rute;
            this.totalBobot = totalBobot;
            this.ditemukan = ditemukan;
        }
    }

    // mode: "waktu" atau "biaya"
    public static HasilRute cari(Graph graph, String asalId, String tujuanId, String mode) {
        if (!graph.containsNode(asalId) || !graph.containsNode(tujuanId)) {
            return new HasilRute(new ArrayList<>(), -1, false);
        }

        Map<String, Integer> jarak = new HashMap<>();
        Map<String, String> sebelum = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String id : graph.getAllNodeIds()) {
            jarak.put(id, Integer.MAX_VALUE);
        }
        jarak.put(asalId, 0);

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(jarak::get));
        pq.add(asalId);

        while (!pq.isEmpty()) {
            String sekarang = pq.poll();
            if (visited.contains(sekarang)) continue;
            visited.add(sekarang);

            if (sekarang.equals(tujuanId)) break;

            for (Edge e : graph.getNeighbors(sekarang)) {
                if (!e.isAktif()) continue; // skip edge yang dimatikan (buat fitur simulasi)

                int bobot = mode.equals("biaya") ? e.getBiayaRupiah() : e.getWaktuMenit();
                String idTujuan = e.getTujuan().getId();
                int jarakBaru = jarak.get(sekarang) + bobot;

                if (jarakBaru < jarak.get(idTujuan)) {
                    jarak.put(idTujuan, jarakBaru);
                    sebelum.put(idTujuan, sekarang);
                    pq.add(idTujuan);
                }
            }
        }

        if (jarak.get(tujuanId) == Integer.MAX_VALUE) {
            return new HasilRute(new ArrayList<>(), -1, false);
        }

        List<String> rute = new ArrayList<>();
        String node = tujuanId;
        while (node != null) {
            rute.add(graph.getNode(node).getNama());
            node = sebelum.get(node);
        }
        Collections.reverse(rute);

        return new HasilRute(rute, jarak.get(tujuanId), true);
    }

    // fitur "bandingkan dua kriteria rute"
    public static void bandingkanRute(Graph graph, String asalId, String tujuanId) {
        HasilRute rTercepat = cari(graph, asalId, tujuanId, "waktu");
        HasilRute rTermurah = cari(graph, asalId, tujuanId, "biaya");

        System.out.println("Rute tercepat (berdasarkan waktu):");
        if (rTercepat.ditemukan) {
            System.out.println("  " + rTercepat.rute);
            System.out.println("  Total waktu: " + rTercepat.totalBobot + " menit");
        } else {
            System.out.println("  Tidak ada rute.");
        }

        System.out.println("Rute termurah (berdasarkan biaya):");
        if (rTermurah.ditemukan) {
            System.out.println("  " + rTermurah.rute);
            System.out.println("  Total biaya: Rp" + rTermurah.totalBobot);
        } else {
            System.out.println("  Tidak ada rute.");
        }
    }
}