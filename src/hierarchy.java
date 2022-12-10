import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class hierarchy {
    final static int total_data = 10506;
    final static int L1_size = 1;
    final static int L2_size = 16;
    final static int L3_size1 = 128;
    final static int L3_size2 = 2;
    final static int L4_size = 4096;

    static int total_access = 0;
    static int total_hit = 0;
    static int L1_access = 0;
    static int L1_hit = 0;
    static int L2_access = 0;
    static int L2_hit = 0;
    static int L3_access = 0;
    static int L3_hit = 0;
    static int L4_access = 0;
    static int L4_hit = 0;
    public static float L1_missRatio() {
        return (1.0f - (float) L1_hit / L1_access);
    }
    public static float L2_missRatio() {
        return (1.0f - (float) L2_hit / L2_access);
    }
    public static float L3_missRatio() {
        return (1.0f - (float) L3_hit / L3_access);
    }
    public static float L4_missRatio() {
        return (1.0f - (float) L4_hit / L4_access);
    }
    public static void main(String[] args) {

        /* FILE INPUT SECTOR */
        String file_name = "1981-2021_USD-KRW_일일_종가.csv";
        File csv = new File(file_name);
        BufferedReader br = null;
        ArrayList<String> dataset = new ArrayList<>(); // EVEN -> DATE // ODD -> PRICE

        try { // VALID INPUT
            br = new BufferedReader(new FileReader(csv));
            String line = "";
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] token = line.split(",");
                dataset.add(token[0]); // DATE
                dataset.add(token[1]); // PRICE
            }
        }
        catch (Exception e) { // INVALID INPUT
            System.out.println("Error!");
        }

        // CACHE LEVEL
        ArrayList<Cache> L1 = new ArrayList<>();
        ArrayList<Cache> L2 = new ArrayList<>();
        ArrayList<Cache>[] L3 = new ArrayList[L3_size1];
        for (int i = 0; i < L3_size1; i++)
            L3[i] = new ArrayList<Cache>();
        ArrayList<Cache> L4 = new ArrayList<>();
        ArrayList<Cache> Disk = new ArrayList<>();

        // CACHE INITIALIZE
        L1.add(new Cache());

        for (int i = 0; i < L2_size; i++)
            L2.add(new Cache());

        for (int i = 0; i < L3_size1; i++)
            for (int j = 0; j < L3_size2; j++)
                L3[i].add(new Cache());

        for (int i = 0; i < L4_size; i++)
            L4.add(new Cache());

        for (int i = 0; i < total_data; i++)
            Disk.add(new Cache());

        // dataset -> Disk
        for (int i = 0; i < dataset.size(); i++) {
            if (i % 2 == 0) {
                Disk.get(i / 2).tag = Integer.parseInt(dataset.get(i).replace("-", ""));
                Disk.get(i / 2).valid = 1;
            }
            else
                Disk.get(i / 2).block.price = Float.parseFloat(dataset.get(i));
        }

        // Randomly Select Data
        ArrayList<Integer> used;
        Random r = new Random();
        int random;

        // Disk -> L4
        used = new ArrayList<>();
        for (int i = 0; i < L4_size / 4; i++) {
            random = r.nextInt(total_data / 4);
            if (!used.contains(random)) {
                used.add(random);
                L4.add(Disk.get(random * 4 + 0));
                L4.add(Disk.get(random * 4 + 1));
                L4.add(Disk.get(random * 4 + 2));
                L4.add(Disk.get(random * 4 + 3));
            }
            else
                i--;
        }
        for (int i = 0; i < L4_size; i++)
            System.out.println(L4.get(i).block.price);

        // L4 -> L3
        used = new ArrayList<>();
        for (int i = 0; i < L3_size1; i++) {
            random = r.nextInt(L4_size / 2);
            if (!used.contains(random)) {
                used.add(random);
                L3[i].add(0, L4.get(random * 2 + 0));
                L3[i].add(1, L4.get(random * 2 + 1));
            }
            else
                i--;
        }
        for (int i = 0; i < L3_size1; i++)
            System.out.printf("%f %f\n",L3[i].get(0).block.price, L3[i].get(1).block.price);

        // L3 -> L2
        used = new ArrayList<>();
        for (int i = 0; i < L2_size / 2; i++) {
            random = r.nextInt(L3_size1);
            if (!used.contains(random)) {
                used.add(random);
                L2.add(L3[random].get(0));
                L2.add(L3[random].get(1));
            }
            else
                i--;
        }
        for (int i = 0; i < L2_size; i++)
            System.out.println(L2.get(i).block.price);

        // L2 -> L1
        random = r.nextInt(L2_size);
        L1.add(L2.get(random));
        System.out.printf("\n%.2f", L1.get(0).block.price);

        // SAMPLE INPUTS TO MEASURE MISS RATIO
        String[] sample_date = {"2021-12-28", "2021-11-22", "2021-10-21", "2021-09-23", "2021-08-11", "2012-11-02", "2012-10-25", "2012-09-20", "2012-08-16", "2012-07-26"};
        int[] samples = new int[sample_date.length];

        for(int i=0; i < sample_date.length; i++)
            samples[i] = Integer.parseInt(sample_date[i].replace("-",""));

        for (int i = 0; i < samples.length; i++) {
            int seek;

            // SEEK SAMPLE IN L1
            seek = 0;
            if (samples[i] == L1.get(seek).tag) {
                System.out.println("Find: ₩" + L1.get(seek).block.price);
                continue;
            }

            // SEEK SAMPLE IN L2
            seek = 0;
            while (samples[i] != L2.get(seek).tag && seek < L2_size)
                seek++;

            if (seek < L2_size) {
                System.out.println("Find: ₩" + L2.get(seek).block.price);
                // copy to upper level

                //
                continue;
            }

            // SEEK SAMPLE IN L3
            // SEEK SAMPLE IN L4
            // SEEK SAMPLE IN Disk

        }
    }
}
