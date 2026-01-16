package org.uma.ed.demos.stack;

import org.uma.ed.datastructures.stack.ArrayStack;
import org.uma.ed.datastructures.stack.Stack;

import java.util.Scanner;

public class Typer extends ArrayStack<Character> {

    public static void type(String str){

        Stack<Character> stack = new ArrayStack<>(str.length());
        Stack<Character> temp = new ArrayStack<>(str.length());

        char[] ch = str.toCharArray();

        for(char c : ch){
            if(c == '#' && temp.isEmpty()){
                continue;
            }
            else if(c != '#'){
                temp.push(c);
            }
            else{
                temp.pop();
            }
        }

        while(!temp.isEmpty()){
            stack.push(temp.top());
            temp.pop();
        }

        StringBuilder finalStr = new StringBuilder();

        while(!stack.isEmpty()){
            finalStr.append(stack.top());
            stack.pop();
        }

        System.out.println(finalStr);

    }

    public static void main(String[] args){
        Scanner input = new Scanner(System.in);

        System.out.println("Enter a string");
        String s = input.nextLine();

        type(s);


    }
}
