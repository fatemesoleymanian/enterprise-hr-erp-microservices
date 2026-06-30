import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int n = input.nextInt();

        if (n == 0) {
            System.out.println(0);
            return;
        }

        int[] a = new int[n];

        for (int i = 0; i < n; i++) {
            a[i] = input.nextInt();
        }

        Arrays.sort(a);

        ArrayList<Integer> lastCode = new ArrayList<>();
        ArrayList<Integer> size = new ArrayList<>();

        for (int x : a) {

            int best = -1;

            for (int i = 0; i < lastCode.size(); i++) {
                if (lastCode.get(i) == x - 1) {

                    if (best == -1 || size.get(i) < size.get(best)) {
                        best = i;
                    }
                }
            }

            if (best == -1) {
                lastCode.add(x);
                size.add(1);
            } else {
                lastCode.set(best, x);
                size.set(best, size.get(best) + 1);
            }
        }

        int answer = Integer.MAX_VALUE;

        for (int len : size) {
            answer = Math.min(answer, len);
        }

        System.out.println(answer);
    }
}