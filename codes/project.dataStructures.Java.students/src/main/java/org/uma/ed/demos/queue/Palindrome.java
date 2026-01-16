package org.uma.ed.demos.queue;

import org.uma.ed.datastructures.queue.ArrayQueue;
import org.uma.ed.datastructures.stack.ArrayStack;
import java.util.Scanner;

public class Palindrome {

    public static void Pali(String str){
        char[] ch = str.toCharArray();

        ArrayQueue<Character> queue = ArrayQueue.withCapacity(ch.length);
        ArrayStack<Character> stack = ArrayStack.withCapacity(ch.length);

        for(char c : ch){
            stack.push(c);
            queue.enqueue(c);
        }

        StringBuilder stackStr = new StringBuilder();

        while(!stack.isEmpty()){
            stackStr.append(stack.top());
            //System.out.print(stack.top()+" ");
            stack.pop();
        }
        String sStr = stackStr.toString();

        StringBuilder queueStr = new StringBuilder();

        while(!queue.isEmpty()){
            queueStr.append(queue.first());
            //System.out.print(queue.first()+" ");
            queue.dequeue();
        }
        String qStr = queueStr.toString();

        if(sStr.equals(qStr)){
            System.out.println("It's a palindrome!! :D");
        }else {
            System.out.println("It's not a palindrome :(");
        }
    }


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        /*System.out.println("Sayı girmek için 1 yazı için 2:");

        if(input.nextInt() == 1){
            System.out.println("sayı gir:");
            int i = input.nextInt();

        } else if (input.nextInt() == 2) {
            System.out.println("yazı gir:");
            String s = input.nextLine();

        }
        else{
            throw new IllegalArgumentException("1 veya 2 gir!");
        }*/

        System.out.println("metin girin:");
        String s = input.nextLine();

        Pali(s);

    }
}
