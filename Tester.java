import java.util.ArrayList;

public class Tester
{
    public static void main(String[] args) throws java.io.FileNotFoundException
    {
        //3 accounts for 3 seperate algorithms
        Account gavinAccountAlgo1 = new Account(100000);
        Account gavinAccountAlgo2 = new Account(100000);
        Account gavinAccountAlgo3 = new Account(100000);
        
        RsiCalc r = new RsiCalc();
        //fill the data arrayList and print out data/RSI/SMA
        r.fillArray();
        r.calculateRsi();
        r.calculateSMA();
        r.print();
        
        //Run trading Algos
        System.out.println("ALGORITHM 1 TEST");
        r.algorithm1(gavinAccountAlgo1);
        
        System.out.println("ALGORITHM 2 TEST");
        r.algorithm2(gavinAccountAlgo2);
        
        System.out.println("ALGORITHM 3 TEST");
        r.algorithm3(gavinAccountAlgo3);
        
    }
}