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
    static float L1_access = 0.0f;
    static float L1_hit = 0.0f;
    static float L2_access = 0.0f;
    static float L2_hit = 0.0f;
    static float L3_access = 0.0f;
    static float L3_hit = 0.0f;
    static float L4_access = 0.0f;
    static float L4_hit = 0.0f;
    public static float L1_hitRatio() {
        return (L1_hit / L1_access);
    }
    public static float L2_hitRatio() {
        return (L2_hit / L2_access);
    }
    public static float L3_hitRatio() {
        return (L3_hit / L3_access);
    }
    public static float L4_hitRatio() {
        return (L4_hit / L4_access);
    }
    public static void main(String[] args) {

        /* FILE INPUT SECTOR */
        String file_name = "1981-2021_USD-KRW_일일_종가.csv";
        File csv = new File(file_name);
        BufferedReader br;
        ArrayList<String> dataset = new ArrayList<>(); // EVEN -> DATE // ODD -> PRICE

        try { // VALID INPUT
            br = new BufferedReader(new FileReader(csv));
            String line;
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
            L3[i] = new ArrayList<>();
        ArrayList<Cache> L4 = new ArrayList<>();
        ArrayList<Cache> Disk = new ArrayList<>();

        // dataset -> Disk
        for (int i = 0; i < total_data; i++)
            Disk.add(new Cache());

        for (int i = 0; i < dataset.size(); i++) {
            if (i % 2 == 0) {
                Disk.get(i / 2).setTag(Integer.parseInt(dataset.get(i).replace("-", "")));
                Disk.get(i / 2).setValid(1);
            }
            else
                Disk.get(i / 2).setBlock(Float.parseFloat(dataset.get(i)));
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
                L4.add(Disk.get(random * 4));
                L4.add(Disk.get(random * 4 + 1));
                L4.add(Disk.get(random * 4 + 2));
                L4.add(Disk.get(random * 4 + 3));
            }
            else
                i--;
        }
//        for (int i = 0; i < L4_size; i++)
//            System.out.println(L4.get(i).block.price);

        // L4 -> L3
        used = new ArrayList<>();

        for (int i = 0; i < L3_size1; i++)
            for (int j = 0; j < L3_size2; j++)
                L3[i].add(new Cache());

        for (int i = 0; i < L3_size1; i++) {
            random = r.nextInt(L4_size / 2);
            if (!used.contains(random)) {
                used.add(random);
                L3[i].add(0, L4.get(random * 2));
                L3[i].add(1, L4.get(random * 2 + 1));
            }
            else
                i--;
        }
//        for (int i = 0; i < L3_size1; i++)
//            System.out.println(L3[i].get(0).block.price + " " + L3[i].get(1).block.price);

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
//        for (int i = 0; i < L2_size; i++)
//            System.out.println(L2.get(i).block.price);

        // L2 -> L1
        random = r.nextInt(L2_size);
        L1.add(L2.get(random));
//        System.out.println("\n" + L1.get(0).block.price + " valid:" + L1.get(0).valid + " tag:" + L1.get(0).tag);

        // SAMPLE INPUTS TO MEASURE MISS RATE
        String[] sample_date = {"1997-12-12", "1997-12-11", "1997-12-10", "1997-12-09",
                "1997-12-08", "1997-12-05", "1997-12-04", "1997-12-03", "1997-12-02",
                "1997-12-01", "1997-11-28", "1997-11-27", "1997-11-26", "1997-11-25",
                "1997-11-24", "1997-11-21", "1997-11-20", "1997-11-19", "1997-11-18",
                "1997-11-17", "1997-11-14", "1997-11-13", "1997-11-12", "2008-03-13",
                "2008-03-12", "2008-03-11", "2008-03-10", "2008-03-07", "2008-03-06",
                "2008-03-05", "2008-03-04", "2008-03-03", "2008-02-29", "2008-03-13",
                "2008-03-12", "2008-03-11", "2008-03-10", "2008-03-18", "2008-03-17",
                "2008-03-13", "2008-03-12", "2008-03-11", "2008-03-10", "2021-12-31",
                "1999-07-07", "1981-05-12", "2002-01-21", "2011-08-16", "2018-11-06",
                "2009-11-11", "1987-10-06", "2019-02-20", "1991-07-31", "2020-12-03",
                "1995-06-21", "2008-03-13", "2008-03-13", "2008-03-13", "2008-03-13",
                "1981-05-12", "1981-05-12", "1981-05-12", "1981-05-12", "1981-05-12",
                "1981-05-12",  "2021-12-31", "2021-12-31"};
        int[] samples = new int[sample_date.length];

        for(int i=0; i < sample_date.length; i++)
            samples[i] = Integer.parseInt(sample_date[i].replace("-",""));

        for (int sample : samples) {
            int seek;

            /* TO-DO
                COPY TO UPPER CLASS
                - 특정한 레벨의 캐쉬에서 데이터를 찾으면 해당 레벨 캐쉬에 있는 모든 상위 레벨의 캐쉬에 찾은 값을 복사해 넣는다.
                - L1을 제외하고는 2-4개 정도의 주변 데이터도 함께 복사해간다.
                - 참조된 캐쉬는 해당 캐쉬의 맨 뒤로 이동한다.
             */

            // SEEK SAMPLE IN L1
            L1_access++;
            seek = 0;
            if (sample == L1.get(seek).tag) {
                L1_hit++;
                System.out.println("In L1,\t Date:(" + sample + ") = ₩ " + L1.get(seek).block.price);
                continue;
            }

            // SEEK SAMPLE IN L2
            L2_access++;
            seek = 0;
            while (sample != L2.get(seek).tag) {
                seek++;
                if (seek == L2_size)
                    break;
            }

            if (seek < L2_size) {
                L2_hit++;
                System.out.println("In L2,\t Date:(" + sample + ") = ₩ " + L2.get(seek).block.price);
                // COPY TO UPPER CACHE: L2 -> L2, L1
                L1.remove(0);
                L1.add(L2.get(seek));
                L2.add(L2.get(seek));
                L2.remove(seek);
                continue;
            }

            // SEEK SAMPLE IN L3
            L3_access++;
            seek = 0;
            while (sample != L3[seek].get(0).tag && sample != L3[seek].get(1).tag) {
                seek++;
                if (seek == L3_size1)
                    break;
            }

            if (seek < L3_size1) {
                L3_hit++;
                if (sample == L3[seek].get(0).tag) {
                    System.out.println("In L3\t Date:(" + sample + ") = ₩ " + L3[seek].get(0).block.price);
                    L1.add(L3[seek].get(0));
                }
                else {
                    System.out.println("In L3,\t Date:(" + sample + ") = ₩ " + L3[seek].get(1).block.price);
                    L1.add(L3[seek].get(1));
                }
                // COPY TO UPPER CACHE: L3 -> L3, L2, L1
                L1.remove(0);

                L2.remove(0);
                L2.remove(0);
                L2.remove(0);
                L2.remove(0);
                L2.add(L3[seek].get(0));
                L2.add(L3[seek].get(1));
                if (seek == L3_size1 - 1) {
                    L2.add(L3[seek - 1].get(0));
                    L2.add(L3[seek - 1].get(1));
                }
                else {
                    L2.add(L3[seek + 1].get(0));
                    L2.add(L3[seek + 1].get(1));
                }

                for (int i = 0; i < L3_size1 - 1; i++)
                    L3[i] = L3[i + 1];
                for (int i = 0; i < L3_size1 - 1; i++)
                    L3[i] = L3[i + 1];
                L3[L3_size1 - 1].set(0, L2.get(L2_size - 1));
                L3[L3_size1 - 1].set(1, L2.get(L2_size - 2));
                L3[L3_size1 - 2].set(0, L2.get(L2_size - 3));
                L3[L3_size1 - 2].set(1, L2.get(L2_size - 4));
                continue;
            }

            // SEEK SAMPLE IN L4
            L4_access++;
            seek = 0;
            while (sample != L4.get(seek).tag) {
                seek++;
                if (seek == L4_size)
                    break;
            }

            if (seek < L4_size) {
                L4_hit++;
                System.out.println("In L4,\t Date:(" + sample + ") = ₩ " + L4.get(seek).block.price);
                // COPY TO UPPER CACHE: L4 -> L4, L3, L2, L1
                L1.add(L4.get(seek));
                L1.remove(0);

                L2.remove(0);
                L2.remove(0);
                L2.remove(0);
                L2.remove(0);

                for (int i = 0; i < L3_size1 - 1; i++)
                    L3[i] = L3[i + 1];
                for (int i = 0; i < L3_size1 - 1; i++)
                    L3[i] = L3[i + 1];

                if (seek < L4_size - 4) {
                    L2.add(L4.get(seek));
                    L2.add(L4.get(seek + 1));
                    L2.add(L4.get(seek + 2));
                    L2.add(L4.get(seek + 3));

                    L3[L3_size1 - 1].set(0, L4.get(seek));
                    L3[L3_size1 - 1].set(1, L4.get(seek + 1));
                    L3[L3_size1 - 2].set(0, L4.get(seek + 2));
                    L3[L3_size1 - 2].set(1, L4.get(seek + 3));

                    L4.add(L4.get(seek));
                    L4.add(L4.get(seek + 1));
                    L4.add(L4.get(seek + 2));
                    L4.add(L4.get(seek + 3));
                }
                else {
                    L4.add(L4.get(seek));
                    L4.add(L4.get(seek - 1));
                    L4.add(L4.get(seek - 2));
                    L4.add(L4.get(seek - 3));

                    L3[L3_size1 - 1].set(0, L4.get(seek));
                    L3[L3_size1 - 1].set(1, L4.get(seek - 1));
                    L3[L3_size1 - 2].set(0, L4.get(seek - 2));
                    L3[L3_size1 - 2].set(1, L4.get(seek - 3));

                    L4.add(L4.get(seek));
                    L4.add(L4.get(seek - 1));
                    L4.add(L4.get(seek - 2));
                    L4.add(L4.get(seek - 3));
                }

                L4.remove(0);
                L4.remove(0);
                L4.remove(0);
                L4.remove(0);
                continue;
            }

            // SEEK SAMPLE IN Disk
            seek = 0;
            while (sample != Disk.get(seek).tag) {
                seek++;
                if (seek == Disk.size())
                    break;
            }

            if (seek < Disk.size()) {
                System.out.println("In Disk, Date:(" + sample + ") = ₩ " + Disk.get(seek).block.price);
                // COPY TO UPPER CACHE: Disk -> L4, L3, L2, L1
                L1.add(Disk.get(seek));
                L1.remove(0);

                L2.remove(0);
                L2.remove(0);
                L2.remove(0);
                L2.remove(0);

                for (int i = 0; i < L3_size1 - 1; i++)
                    L3[i] = L3[i + 1];
                for (int i = 0; i < L3_size1 - 1; i++)
                    L3[i] = L3[i + 1];

                if (seek < Disk.size() - 4) {
                    L2.add(Disk.get(seek));
                    L2.add(Disk.get(seek + 1));
                    L2.add(Disk.get(seek + 2));
                    L2.add(Disk.get(seek + 3));

                    L3[L3_size1 - 1].set(0, Disk.get(seek));
                    L3[L3_size1 - 1].set(1, Disk.get(seek + 1));
                    L3[L3_size1 - 2].set(0, Disk.get(seek + 2));
                    L3[L3_size1 - 2].set(1, Disk.get(seek + 3));

                    L4.add(Disk.get(seek));
                    L4.add(Disk.get(seek + 1));
                    L4.add(Disk.get(seek + 2));
                    L4.add(Disk.get(seek + 3));
                }
                else {
                    L4.add(Disk.get(seek));
                    L4.add(Disk.get(seek - 1));
                    L4.add(Disk.get(seek - 2));
                    L4.add(Disk.get(seek - 3));

                    L3[L3_size1 - 1].set(0, Disk.get(seek));
                    L3[L3_size1 - 1].set(1, Disk.get(seek - 1));
                    L3[L3_size1 - 2].set(0, Disk.get(seek - 2));
                    L3[L3_size1 - 2].set(1, Disk.get(seek - 3));

                    L4.add(Disk.get(seek));
                    L4.add(Disk.get(seek - 1));
                    L4.add(Disk.get(seek - 2));
                    L4.add(Disk.get(seek - 3));
                }

                L4.remove(0);
                L4.remove(0);
                L4.remove(0);
                L4.remove(0);
                System.out.println();
            }

        }

        // PRINT OUT MISS RATIO OR HIT RATIO
        System.out.println();
        System.out.println("L1 Cache Miss Rate : " + (1.0f - L1_hitRatio()) * 100.0f + "%");
        System.out.println("L2 Cache Miss Rate : " + (1.0f - L2_hitRatio()) * 100.0f + "%");
        System.out.println("L3 Cache Miss Rate : " + (1.0f - L3_hitRatio()) * 100.0f + "%");
        System.out.println("L4 Cache Miss Rate : " + (1.0f - L4_hitRatio()) * 100.0f + "%");
    }
}
