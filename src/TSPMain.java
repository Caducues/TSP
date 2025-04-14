import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class TSPMain {
    static double[][] cities; // Koordinatlar artık double
    static boolean[] visited;
    static double totalDistance = 0;
    static List<Integer> route = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Veri dosyaları
        String[] fileNames = {
                "Datasets/tsp_51_1",
                "Datasets/tsp_150_2",
                "Datasets/tsp_318_2",
                "Datasets/tsp_3038_1",
                "Datasets/tsp_14051_1",
                "Datasets/tsp_85900_1"
        };

        // Çıktı dosyası
        BufferedWriter writer = new BufferedWriter(new FileWriter("TSP_results.txt"));

        // Öğrenci Bilgileri
        writer.write("Öğrenci No: 222805014, Ad Soyad: Murat Taha Akyüz\n\n");

        // Tüm dosyaları işle
        for (String fileName : fileNames) {
            System.out.println("Çalışma dosyası: " + fileName);
            processFile(fileName, writer);
        }

        writer.close();
    }

    // Her dosyayı ayrı ayrı işleyen fonksiyon
    public static void processFile(String fileName, BufferedWriter writer) throws IOException {
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);

            int n = myReader.nextInt(); // İlk satır: şehir sayısı
            cities = new double[n][2];
            visited = new boolean[n];
            int index = 0;

            while (myReader.hasNextDouble() && index < n) {
                cities[index][0] = myReader.nextDouble();
                cities[index][1] = myReader.nextDouble();
                index++;
            }
            myReader.close();

            // Algoritmayı çalıştır
            greedyTSP(0); // Şehir 0'dan başla
            twoOpt();     // 2-opt ile iyileştir

            // Sonuçları yaz
            writer.write("Dosya Boyutu " + n + ":\n");
            writer.write("Optimal maliyet değeri: " + totalDistance + "\n");
            writer.write("Optimal maliyeti sağlayan path: " + getRoute() + "\n\n");

            // Konsola yaz
            System.out.println("Optimal maliyet değeri: " + totalDistance);
            System.out.println("Optimal maliyeti sağlayan path: " + getRoute());

            // Sıfırla
            totalDistance = 0;
            route.clear();
            for (int i = 0; i < visited.length; i++) {
                visited[i] = false;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Dosya bulunamadı: " + fileName);
            e.printStackTrace();
        }
    }

    // Greedy algoritması
    public static void greedyTSP(int startCity) {
        int currentCity = startCity;
        visited[currentCity] = true;
        route.add(currentCity);

        for (int count = 0; count < cities.length - 1; count++) {
            int nextCity = -1;
            double shortest = Double.MAX_VALUE;

            for (int i = 0; i < cities.length; i++) {
                if (!visited[i]) {
                    double distance = CitiesDistance(currentCity, i);
                    if (distance < shortest) {
                        shortest = distance;
                        nextCity = i;
                    }
                }
            }

            if (nextCity != -1) {
                visited[nextCity] = true;
                totalDistance += shortest;
                route.add(nextCity);
                currentCity = nextCity;
            }
        }

        // Başlangıç noktasına dön
        totalDistance += CitiesDistance(currentCity, startCity);
        route.add(startCity);
    }

    // 2-opt algoritması
    public static void twoOpt() {
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            for (int i = 1; i < route.size() - 2; i++) {
                for (int j = i + 1; j < route.size() - 1; j++) {
                    double currentDistance = CitiesDistance(route.get(i - 1), route.get(i)) +
                            CitiesDistance(route.get(j), route.get(j + 1));
                    double newDistance = CitiesDistance(route.get(i - 1), route.get(j)) +
                            CitiesDistance(route.get(i), route.get(j + 1));

                    if (newDistance < currentDistance) {
                        swap(i, j);
                        totalDistance -= currentDistance - newDistance;
                        improvement = true;
                    }
                }
            }
        }
    }

    // Rota üzerinde iki noktayı yer değiştir
    public static void swap(int i, int j) {
        while (i < j) {
            int temp = route.get(i);
            route.set(i, route.get(j));
            route.set(j, temp);
            i++;
            j--;
        }
    }

    // İki şehir arası mesafe
    public static double CitiesDistance(int x, int y) {
        if (x == y) return 0;

        double dx = cities[x][0] - cities[y][0];
        double dy = cities[x][1] - cities[y][1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Rota bilgisini metin olarak döndür
    public static String getRoute() {
        StringBuilder sb = new StringBuilder();
        for (Integer city : route) {
            sb.append(city).append(" → ");
        }
        return sb.substring(0, sb.length() - 3); // Son ok'u sil
    }
}
