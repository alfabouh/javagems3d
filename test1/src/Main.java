import javax.swing.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        String message = JOptionPane.showInputDialog("Введите исходное бинарное сообщение:");
        if(message==null) return;
        if(!message.matches("[01]+")){
            JOptionPane.showMessageDialog(null,"Ошибка ввода! Вводите только 0 или 1.");
            return;
        }

        int k = message.length();
        int r = 1;
        while((1<<r) < k + r + 1) r++;
        int n = k + r;
        int[] code = new int[n+1];

        for(int i=0,j=1;i<k;j++){
            if(!isPowerOfTwo(j)) code[j] = message.charAt(i++)-'0';
        }

        for(int i=0;i<r;i++){
            int p = 1<<i;
            int sum=0;
            for(int j=1;j<=n;j++){
                if((j & p) !=0) sum ^= code[j];
            }
            code[p]=sum;
        }

        StringBuilder encoded = new StringBuilder();
        for(int i=1;i<=n;i++) encoded.append(code[i]);
        JOptionPane.showMessageDialog(null,"Закодированное сообщение: "+encoded);

        String errorPosStr = JOptionPane.showInputDialog("Введите позицию для симулированной ошибки (0 - без ошибки):");
        int errorPos = 0;
        try{errorPos = Integer.parseInt(errorPosStr);}catch(Exception e){}
        if(errorPos>0 && errorPos<=n) code[errorPos]^=1;

        StringBuilder received = new StringBuilder();
        for(int i=1;i<=n;i++) received.append(code[i]);
        JOptionPane.showMessageDialog(null,"Принятое сообщение: "+received);

        int syndrome=0;
        for(int i=0;i<r;i++){
            int p=1<<i, sum=0;
            for(int j=1;j<=n;j++){
                if((j & p)!=0) sum ^= code[j];
            }
            if(sum!=0) syndrome |= p;
        }

        if(syndrome>0){
            code[syndrome]^=1;
            JOptionPane.showMessageDialog(null,"Обнаружена и исправлена ошибка на позиции: "+syndrome);
        } else JOptionPane.showMessageDialog(null,"Ошибок не обнаружено.");

        StringBuilder corrected = new StringBuilder();
        for(int i=1;i<=n;i++) corrected.append(code[i]);
        JOptionPane.showMessageDialog(null,"Исправленное сообщение: "+corrected);
    }

    static boolean isPowerOfTwo(int x){return (x & (x-1))==0;}
}