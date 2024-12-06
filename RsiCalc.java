import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RsiCalc
{
    //Global variables
    private double rsi;
    private double avgU;
    private double avgD;

    //Various arrayLists for data storage
    private ArrayList<Double> data = new ArrayList<>();
    private ArrayList<Double> rsiArray = new ArrayList<>();
    private ArrayList<Double> avgUp = new ArrayList<>();
    private ArrayList<Double> avgDown = new ArrayList<>();
    private ArrayList<Double> sma = new ArrayList<>();

    //getter methods 
    public double getAvgU()
    {
        return avgU;
    }

    public double getAvgD()
    {
        return avgD;
    }

    public int getavgUsize()
    {
        return avgUp.size();
    }

    public int getavgDsize()
    {
        return avgDown.size();
    }

    //fills the arraylist "data" with csv data downloaded from the internet
    //specifically Netflix stock data 
    public void fillArray() throws FileNotFoundException
    {
        File file1 = new File("netflixData.txt");
        Scanner fileReader = new Scanner(file1);

        while(fileReader.hasNextDouble())
        {
            data.add(fileReader.nextDouble());
        }

    }

    //calculates and fills the "averages" arraylists used for moving rsi calculations
    public double calculateRsi()
    {
        calculateAverages();
        rsiArray();
        return rsi;
    }

    public void calculateAverages()
    {
        ArrayList<Double> downSwing = new ArrayList<>();
        ArrayList<Double> upSwing = new ArrayList<>();

        //these loops load the up and down moves into their respective arraylists
        for(int i = 1; i < data.size() - 1; i++)
        {
            double swing = (data.get(i) - data.get(i-1));
            if(swing <= 0)
            {

                upSwing.add(0.00);
            }
            else
            {
                upSwing.add(swing);
            }
        }
        for(int i = 1; i < data.size() - 1; i++)
        {
            double swing = (data.get(i) - data.get(i-1));
            if(swing >= 0)
            {

                downSwing.add(0.00);
            }
            else
            {
                downSwing.add(-1*swing);
            }
        }

        //loops to calculate averages
        for(int i = 0; i < upSwing.size() - 1; i++)
        {
            if(i == 0)
            {
                avgU = (1/5.0) * upSwing.get(i) + (4/5.0);
                avgUp.add(avgU);
            }
            else
            {
                avgU = (1.0/5.0) * upSwing.get(i) + (4.0/5.0) * avgU;
                avgUp.add(avgU);
            }
        }

        for(int i = 0; i < downSwing.size() - 1; i++)
        {
            if(i == 0)
            {
                avgD = (1/5.0) * downSwing.get(i) + (4/5.0);       
                //add to arrayList of averages for later computation of moving rsi
                avgDown.add(avgD);
            }
            else
            {
                avgD = (1.0/5.0) * downSwing.get(i) + (4.0/5.0) * avgD;
                //add to arrayList of averages for later computation of moving rsi
                avgDown.add(avgD);
            }
        }

        // get the relative Strength
        double relativeStrength = avgU/avgD;

        //final formula to get RSI
        rsi = 100 - (100/(1+relativeStrength));
    }

    //loop that calculates all/moving rsi values based on the averages
    public void rsiArray()
    {
        for(int i = 0; i < avgUp.size() - 1 ;i++)
        {
            double relativeStrength = avgUp.get(i)/avgDown.get(i);
            double temp = 100 - (100/(1+relativeStrength));
            temp = Math.round(temp * 100.0) / 100.0;
            rsiArray.add(temp);
        }

    }

    //Calculate the SMA(simple moving Average) this will be a 5 day SMA
    public void calculateSMA()
    {
        for (int i = 0; i <= data.size() - 5; i++) {
            double sum = 0;

            for (int j = i; j < i + 5; j++) 
            {
                sum += data.get(j);
            }

            //Calculate the average (SMA) and add to the list
            double average = sum / 5;
            average = Math.round(average * 100.0) / 100.0;  
            sma.add(average);
        }
    }

    //Calculates whether or not a buy is high probability based on its trajectory and current posistion
    public boolean rsiProbableBuy(int i)
    {
        if(i < rsiArray.size()-1)
        {
            double rsiSlope = rsiArray.get(i);
            rsiSlope -= rsiArray.get(i-1);
            rsiSlope -= rsiArray.get(i-2);
            if(rsiSlope < 0 && rsiArray.get(i) < 36)
            {
                return true;
            }
        }
        return false;
    }

    public void print()
    {
        System.out.println("Stock data: " + data.toString());
        System.out.println("RSI Values: " + rsiArray.toString());
        System.out.println("SMA Values: " + sma.toString());
    }

    //buy and hold algorithm (100% of the account to begin, option to sell at later date) 
    public void algorithm1(Account userInput)
    {
        //convert account balance to shares
        double shares = userInput.getBalance() / data.get(0);
        userInput.setBalance(0.0);

        //Loop that gives the user the opportunity to Sell a portion of their shares on any given day
        //gives the option to skip to a later date
        for(int i = 0; i < data.size() - 1; i+=0)
        {
            if(i <= 252)
            {
                Scanner scan = new Scanner(System.in);
                System.out.println("You currently own " + shares + " shares of netflix on the " + i + " day out of 253");
                System.out.println("Your shares are currently worth $" + data.get(i) + " a piece");
                System.out.println("Would you like to sell?  Y/N");
                String input = scan.nextLine();
                if(input.equals("Y"))
                {
                    System.out.println("How many?");
                    Double input2 = scan.nextDouble();
                    shares -= input2;
                    double conversion = input2 * data.get(i);
                    conversion = Math.round(conversion * 100.0) / 100.0;
                    userInput.setBalance(conversion);
                    System.out.println("You have sold " + input2 + " shares of stock for $" + conversion + " on the " + i + " day out of 253");
                    System.out.println("You have $" + userInput.getBalance() + " in your account");
                }
                else
                {
                    double dummyAction = 0;
                }

                System.out.println("What day would you like to skip to? Cannot exceed 253");
                int input3 = scan.nextInt();
                i = input3 - 1;
            }

        }
        //End of the algorithm automatically liquidates all shares at the end of the year 
        double conversion = shares * data.get(data.size()-1);
        shares -= shares;
        conversion = Math.round(conversion * 100.0) / 100.0;
        userInput.setBalance(conversion);
        System.out.println("The year is over and your shares have been automatically liquidated for $" + data.get(data.size()-1) + " a piece");
        System.out.println("You have $" + userInput.getBalance() + " in your account" + "\n" + "============================================");
    }

    //Uses RSI and SMA to determine buy and sell points...If buy is executed it will be with 25% buying power
    public void algorithm2(Account userInput)
    {
        double shares = 0; 
        double rsiSlope = 0;
        double buyAmount = userInput.getBalance() * 0.25;
        buyAmount = Math.round(buyAmount * 100.0) / 100.0;
        double smaAcceptedRangeBuy = 0;
        double smaAcceptedRangeSell = 0;
        for(int i = 0; i < data.size() - 1; i++)
        {
            if(i < 15)
            {
                continue;
            }
            //Handles different size of arrayLists, keeps the range of accepted values the same as the maximum value when maximum input is reached    
            if(i < sma.size()-1)
            {
                smaAcceptedRangeBuy = 15 + sma.get(i);
            }
            else
            {
                smaAcceptedRangeBuy = smaAcceptedRangeBuy;
            }
            if(i < sma.size()-1)
            {
                smaAcceptedRangeSell = sma.get(i) + 25;
            }
            else
            {
                smaAcceptedRangeSell = smaAcceptedRangeSell;
            }

            double sellAmount = shares * data.get(i);
            sellAmount = Math.round(sellAmount * 100.0) / 100.0;

            //Buying algo
            //Buys shares if the price is within a certain range from SMA and the RSI deems the trade probable
            if(data.get(i) < smaAcceptedRangeBuy && rsiProbableBuy(i))
            {
                System.out.println("Purchased: $" + buyAmount + " worth of shares at a price of $" + data.get(i));
                shares += buyAmount/data.get(i);
                userInput.setBalance(userInput.getBalance()-buyAmount);
                buyAmount = userInput.getBalance() * 0.25;

                System.out.println("You currently own " + shares + " shares of netflix on the " + i + " day out of 253");
                System.out.println("Your shares are currently worth $" + data.get(i) + " a piece" + "\n");

            }
            //Selling algo
            //Opposite of Buy
            if(data.get(i) > smaAcceptedRangeSell && !rsiProbableBuy(i))
            {
                //So the process doesnt activate multiple times in a row
                if(shares == 0)
                {
                    continue;
                }
                System.out.println("Sold: $" + sellAmount + " worth of shares");
                shares = 0;
                userInput.deposit(sellAmount);

                System.out.println("All shares have been sold on the " + i + " day out of 253");
                System.out.println("You currently have: $" + userInput.getBalance() + " in your account" + "\n");  
            }
        }
        //End of the algorithm automatically liquidates all shares at the end of the year 
        double conversion = shares * data.get(data.size()-1);
        shares -= shares;
        conversion = Math.round(conversion * 100.0) / 100.0;
        userInput.deposit(conversion);
        System.out.println("The year is over and your shares have been automatically liquidated for $" + data.get(data.size()-1) + " a piece");
        System.out.println("You have $" + userInput.getBalance() + " in your account" + "\n" + "============================================");
    }

    //Buys Shares everytime Netflix Earning reports are above expected values
    public void algorithm3(Account userInput)
    {
        ArrayList<Double> earningsReportExpected = new ArrayList<>();
        earningsReportExpected.add(2.20);
        earningsReportExpected.add(4.51);
        earningsReportExpected.add(4.70);
        earningsReportExpected.add(5.09);
        ArrayList<Double> earningsReportActual = new ArrayList<>();
        earningsReportActual.add(2.11);
        earningsReportActual.add(5.28);
        earningsReportActual.add(4.88);
        earningsReportActual.add(5.40);
        double shares = 0; 
        double buyAmount = userInput.getBalance() * 0.50;
        buyAmount = Math.round(buyAmount * 100.0) / 100.0;
        for(int i = 0; i < data.size();i++)
        {
            if(i == 38)
            {
                if(earningsReportActual.get(0) > earningsReportExpected.get(0))
                {
                    System.out.println("Purchased: $" + buyAmount + " worth of shares at a price of $" + data.get(i));
                    shares += buyAmount/data.get(i);
                    userInput.setBalance(userInput.getBalance()-buyAmount);
                    buyAmount = userInput.getBalance() * 0.50;

                    System.out.println("You currently own " + shares + " shares of netflix on the " + i + " day out of 253");
                    System.out.println("Your shares are currently worth $" + data.get(i) + " a piece" + "\n");
                }
                else
                {
                    continue;   
                }
            }
            if(i == 98)
            {
                if(earningsReportActual.get(1) > earningsReportExpected.get(1))
                {
                    System.out.println("Purchased: $" + buyAmount + " worth of shares at a price of $" + data.get(i));
                    shares += buyAmount/data.get(i);
                    userInput.setBalance(userInput.getBalance()-buyAmount);
                    buyAmount = userInput.getBalance() * 0.50;

                    System.out.println("You currently own " + shares + " shares of netflix on the " + i + " day out of 253");
                    System.out.println("Your shares are currently worth $" + data.get(i) + " a piece" + "\n");
                }
                else
                {
                    continue;   
                }
            }
            if(i == 160)
            {
                if(earningsReportActual.get(2) > earningsReportExpected.get(2))
                {
                    System.out.println("Purchased: $" + buyAmount + " worth of shares at a price of $" + data.get(i));
                    shares += buyAmount/data.get(i);
                    userInput.setBalance(userInput.getBalance()-buyAmount);
                    buyAmount = userInput.getBalance() * 0.50;

                    System.out.println("You currently own " + shares + " shares of netflix on the " + i + " day out of 253");
                    System.out.println("Your shares are currently worth $" + data.get(i) + " a piece" + "\n");
                }
                else
                {
                    continue;   
                }
            }
            if(i == 224)
            {
                if(earningsReportActual.get(3) > earningsReportExpected.get(3))
                {
                    System.out.println("Purchased: $" + buyAmount + " worth of shares at a price of $" + data.get(i));
                    shares += buyAmount/data.get(i);
                    userInput.setBalance(userInput.getBalance()-buyAmount);
                    buyAmount = userInput.getBalance() * 0.50;

                    System.out.println("You currently own " + shares + " shares of netflix on the " + i + " day out of 253");
                    System.out.println("Your shares are currently worth $" + data.get(i) + " a piece" + "\n");
                }
                else
                {
                    continue;   
                }
            }
        }

        //End of the algorithm automatically liquidates all shares at the end of the year 
        double conversion = shares * data.get(data.size()-1);
        shares -= shares;
        conversion = Math.round(conversion * 100.0) / 100.0;
        userInput.deposit(conversion);
        System.out.println("The year is over and your shares have been automatically liquidated for $" + data.get(data.size()-1) + " a piece");
        System.out.println("You have $" + userInput.getBalance() + " in your account");

    }
}