//package com.test.hashtable;

/**
 * Filename: HashTabe.java
 * Author: Susan Gauch, converted to java by Matt Miller, debugged by Patrick Anderson.
 */

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class HashTable
{
    private int size;
    private long used;
    private long collision;
    private long lookups;
    private Node[] hashtable;

    /**
     * Initializes a hashtable with size 3 times the size given.
     * @param size One third of the hashtable size;
     */
    public HashTable(int size)
    {
        this.size=size*3;
        init();
    }

    /**
     * Copies a hashtable
     * @param ht
     */
    public HashTable(HashTable ht)
    {
        this.size=ht.getSize();
        used=ht.getUsed();
        collision=ht.getCollisions();
        lookups=ht.getLookups();
        hashtable=new Node[this.size];

        for(int i=0;i<this.size;i++)
            hashtable[i]=new Node(ht.getNode(i).getAddr(),ht.getNode(i).getName());
    }

    /**
     * Method to be called by constructors.
     * Also an easy way to reset a already made hashtable.
     * Requires that size already be set.
     */
    public void init()
    {
        used=0;
        collision=0;
        lookups=0;
        hashtable=new Node[this.size];

        //initialize the hashtable
        for(int i=0;i<this.size;i++)
            hashtable[i]=new Node("","");
    }

    /**
     * Prints the non empty contents of the hashtable to the file given.
     * @param filename String
     */
    public void print(String filename)
    {
        try
        {
            FileWriter fwriter=new FileWriter(filename);
            BufferedWriter out=new BufferedWriter(fwriter);

            for(int i=0;i<size;i++)
            {
                if(!hashtable[i].getAddr().equals("")) //if address is not empty string
                    out.write(hashtable[i].getAddr()+" "+hashtable[i].getName()+"\n");
            }
            out.close();
        }
        catch(IOException e)
        {
            System.err.println("IOEror: "+e.getMessage());
        }

        System.out.println("Collisions: "+collision+" Used: "+used+" Lookups: "+lookups);
    }

    /**
     * Insert string addr, and string name into hashtable, hashes on addr.
     * @param addr String to be hashed.
     * @param name String
     */
    public void insert(String addr,Object name)
    {
        int index = find(addr);

        //if not already in the table, insert it
        if(hashtable[index].getName().equals(""))
        {
            hashtable[index].setAddr(addr);
            hashtable[index].setName(name);
            used++;
        }
        else {
            hashtable[index].setAddr(addr);
            hashtable[index].setName(name);
        }
    }

    /**
     * Returns the index of the word in the table, or the index of a free space
     * in the table.
     * @param str String to hash.
     * @return index of the word, or of free space in which to place the word.
     */
    public int find(String str)
    {
        long sum=0;
        long index;

        //add all the characters of the string together
        for(int i=0;i<str.length();i++)
            sum=(sum*19)+str.charAt(i); //multiply sum by 19 and add byte value of char

        if(sum < 0)				// If calculation of sum was negative, make it positive
            sum = sum * -1;

        index= sum%size;
        int index2 = (int) index;

        /*
         * check to see if the word is in that location
         * if not there, do linear probing until word is found\
         * or empty location found
         */
        while(!hashtable[index2].getAddr().equals(str) && !hashtable[index2].getAddr().equals(""))
        {
            index2++;
            collision++;
            if(index2 >= size)
                index2 = 0;
        }

        return index2;
    }

    public boolean containsKey(String key){
        int index = find(key);
        return hashtable[index].getAddr().equals(key);
    }


    /**
     * Returns the name at the hashed location of addr.
     * @param addr String to be hashed.
     * @return Name in the table at the location of addr.
     */
    public Object getName(String addr)
    {
        int index=find(addr);
        lookups++;
        return hashtable[index].getName();
    }

    /**
     * Get the three statistics as a string.  Used, Collisions, and Lookups.
     * @return Used, Collisions, and Lookups as a string.
     */
    public String getUsage()
    {
        return "Used: "+used+" Collisions: "+collision+" Lookups: "+lookups;
    }

    /**
     * Get the amount in the table.
     * @return How full the table is. long
     */
    public long getUsed()
    {
        return used;
    }

    /**
     * Get the number of collisions.
     * @return How much you need to improve your hash function. long
     */
    public long getCollisions()
    {
        return collision;
    }

    /**
     * The number of lookups made.
     * @return long
     */
    public long getLookups()
    {
        return lookups;
    }

    /**
     * Gets the size of the array.
     * @return size, long
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Returns Node at location index.
     * @param index location in hashtable
     * @return Node at location index.
     */
    public Node getNode(int index)
    {
        return hashtable[index];
    }


    /**
     * Private class node to whole the actual data stored in the hashtable.
     * Provides standard accessor and mutator methods.
     */
    public class Node
    {
        private String addr;
        private Object name;

        public Node(String addr,Object name)
        {
            this.addr=addr;
            this.name=name;
        }

        public String getAddr()
        {
            return addr;
        }

        public Object getName()
        {
            return name;
        }

        public void setAddr(String addr)
        {
            this.addr=addr;
        }

        public void setName(Object name)
        {
            this.name=name;
        }
    }
}
